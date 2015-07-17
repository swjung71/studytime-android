package kr.co.digitalanchor.studytime.chat;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.ChatSend;
import kr.co.digitalanchor.studytime.model.ChatSendResult;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.ChatMessage;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;
import static kr.co.digitalanchor.studytime.StaticValues.NEW_MESSAGE_ARRIVED;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ParentChatActivity extends BaseActivity implements View.OnClickListener,
        MessageReceiver.OnMessageListener {

    private final int REQUEST_SEND_MESSAGE = 50001;

    private final int REQUEST_NEW_MESSAGE = 50002;

    private final int UPDATE_MESSAGE_LIST = 50003;

    TextView mLabelTitle;

    EditText mEditMessage;

    ImageButton mButtonSend;

    ListView mListChat;

    DBHelper mHelper;

    Child mChild;

    Account mAccount;

    List<ChatMessage> mMessages;

    ParentChatAdapter mAdapter;

    MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_chat);

        mHelper = new DBHelper(getApplicationContext());

        mAccount = mHelper.getAccountInfo();

        getData();

        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (messageReceiver == null) {

            messageReceiver = new MessageReceiver();
            messageReceiver.setMessageListener(this);

        }

        IntentFilter intentFilter = new IntentFilter(NEW_MESSAGE_ARRIVED);

        registerReceiver(messageReceiver, intentFilter);

        sendEmptyMessage(UPDATE_MESSAGE_LIST);
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (messageReceiver != null) {

            unregisterReceiver(messageReceiver);
        }
    }

    @Override
    public void onReceiveMessage() {

        sendEmptyMessage(UPDATE_MESSAGE_LIST);
    }

    private void initView() {

        mLabelTitle = (TextView) findViewById(R.id.header);
        mLabelTitle.setText(mChild.getName());

        mEditMessage = (EditText) findViewById(R.id.editMessage);

        mButtonSend = (ImageButton) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(this);

        mListChat = (ListView) findViewById(R.id.listMessage);
        mListChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mListChat.setStackFromBottom(true);

        mMessages = new ArrayList<>();
        mAdapter = new ParentChatAdapter(getApplicationContext(), mMessages, mAccount.getID());

        mListChat.setAdapter(mAdapter);

    }

    private void getData() {

        Bundle data = getIntent().getExtras();

        if (data == null) {

            finish();

            return;
        }

        String id = null;

        mChild = null;

        if (data.containsKey("ChildID")) {

            id = data.getString("ChildID");

            mChild = mHelper.getChild(id);

        } else {


        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_SEND_MESSAGE:

                insertSendMessage();

                break;

            case UPDATE_MESSAGE_LIST:

                updateMessageList();

                break;

            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonSend:

                Logger.d("R.id.buttonSend");

                sendEmptyMessage(REQUEST_SEND_MESSAGE);

                break;

            default:

                break;
        }
    }

    private void insertSendMessage() {

        String message = mEditMessage.getText().toString();

        if (TextUtils.isEmpty(message)) {

            return ;
        }

        ChatSend model = new ChatSend();

        long primaryKey = mHelper.insertMessageBeforeSend(mChild.getChildID(), mChild.getName(),
                mAccount.getID(), message, System.currentTimeMillis(), 1, 0, 0, 0);

        Logger.d(AndroidUtils.convertCurrentTime4Chat(System.currentTimeMillis()));

        if (primaryKey < 0) {

            return;
        }

        model.setSenderID(mAccount.getID());
        model.setSenderName(mAccount.getName());

        model.setReceiverID(mChild.getChildID());
        model.setMessage(message);
        model.setTime(System.currentTimeMillis());

        model.setMsgType(0);
        model.setIsChildSender(0);
        model.setIsChildReceiver(1);
        model.setIsGroup(0);
        model.setMessagePk(primaryKey);

        sendEmptyMessage(UPDATE_MESSAGE_LIST);

        requestSendMessage(model);
    }

    private void updateMessageList() {

        Logger.d("updateMessageList guest " + mChild.getChildID());

        mHelper.initMessageCount(mChild.getChildID());

        List<ChatMessage> messages = null;

        if (mMessages.size() <= 0) {

            messages = mHelper.getMessages(mChild.getChildID());

        } else {

            long timeStamp = mMessages.get(mMessages.size() - 1).getTimeStamp();

            messages = mHelper.getMessages(mChild.getChildID(), timeStamp);
        }

        if (messages == null) {

            Logger.d("message" + (messages == null));

            return;
        }

        Logger.d("message count " + messages.size());

        for (ChatMessage message : messages) {

            mMessages.add(message);

        }

        mAdapter.notifyDataSetChanged();

        mListChat.setSelection(mMessages.size() - 1);

    }

    private void requestSendMessage(ChatSend model) {

        String msg = mEditMessage.getText().toString();

        if (TextUtils.isEmpty(msg)) {

            Logger.d("return");

            return;
        }

        mEditMessage.setText("");

        SimpleXmlRequest request = HttpHelper.getSendChat(model,
                new Response.Listener<ChatSendResult>() {
                    @Override
                    public void onResponse(ChatSendResult response) {

                        Logger.d(response.toString());

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                break;

                            default:

                                handleResultCode(response.getResultCode(), response.getResultMessage());

                                break;
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        handleError(error);
                    }
                });

        addRequest(request);
    }
}
