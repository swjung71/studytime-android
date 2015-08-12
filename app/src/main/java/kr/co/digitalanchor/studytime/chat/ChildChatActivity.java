package kr.co.digitalanchor.studytime.chat;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.app.AppManageService;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.login.AddInfoActivity;
import kr.co.digitalanchor.studytime.model.ChatSend;
import kr.co.digitalanchor.studytime.model.ChatSendResult;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.ChatMessage;
import kr.co.digitalanchor.studytime.monitor.DownloadService;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.StaticValues.NEW_MESSAGE_ARRIVED;
import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-15.
 */
public class ChildChatActivity extends BaseActivity implements View.OnClickListener,
        MessageReceiver.OnMessageListener{

    private final int REQUEST_SEND_MESSAGE = 50001;

    private final int UPDATE_MESSAGE_LIST = 50002;

    ImageButton mButtonSetting;

    ImageButton mButtonSend;

    Button mButtonClose;

    EditText mEditMessage;

    ListView mListChat;

    View mLayoutGuide;

    DBHelper mHelper;

    Account mAccount;

    MessageReceiver messageReceiver;

    List<ChatMessage> mMessages;

    ChildChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_chat);

        mHelper = new DBHelper(getApplicationContext());

        mAccount = mHelper.getAccountInfo();

        initView();

        firstVisit();

        startMonitorService();
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
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        STApplication.stopAllActivity();
    }

    private void initView() {

        mButtonSetting = (ImageButton) findViewById(R.id.buttonSetting);
        mButtonSetting.setOnClickListener(this);

        mButtonSend = (ImageButton) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(this);

        mButtonClose = (Button) findViewById(R.id.buttonClose);
        mButtonClose.setOnClickListener(this);

        mEditMessage = (EditText) findViewById(R.id.editMessage);

        mLayoutGuide = findViewById(R.id.layoutGuide);

        mListChat = (ListView) findViewById(R.id.listChat);
        mListChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mListChat.setStackFromBottom(true);

        mMessages = new ArrayList<>();
        mAdapter = new ChildChatAdapter(getApplicationContext(), mMessages, mAccount.getID());

        mListChat.setAdapter(mAdapter);
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

            case R.id.buttonSetting:

                AndroidUtils.hideKeyboard(mEditMessage);

                showAdditionalInfo();

//                Intent intent = new Intent(getApplicationContext(), AppManageService.class);
//                intent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_SYNC);
//
//                startService(intent);

//                startService(new Intent(getApplicationContext(), DownloadService.class));

                break;

            case R.id.buttonClose:

                showGuide(false);

                break;

            case R.id.buttonSend:

                sendEmptyMessage(REQUEST_SEND_MESSAGE);

                break;


            default:

                break;
        }
    }

    @Override
    public void onReceiveMessage() {

        sendEmptyMessage(UPDATE_MESSAGE_LIST);
    }

    private void showGuide(boolean flag) {

        if (flag) {

            mLayoutGuide.setVisibility(View.VISIBLE);

        } else {

            mLayoutGuide.setVisibility(View.GONE);
        }
    }

    private void insertSendMessage() {

        String message = mEditMessage.getText().toString();

        if (TextUtils.isEmpty(message)) {

            return ;
        }

        long primaryKey = mHelper.insertMessageBeforeSend(mAccount.getParentId(), mAccount.getParentName(),
                mAccount.getID(), message, System.currentTimeMillis(), 1, 0, 0, 0);

        if (primaryKey < 0) {

            return;
        }

        sendEmptyMessage(UPDATE_MESSAGE_LIST);

        requestSendMessage(primaryKey);
    }

    private void updateMessageList() {

        Logger.d("updateMessageList guest " + mAccount.getParentId());

        List<ChatMessage> messages = null;

        if (mMessages.size() <= 0) {

            messages = mHelper.getMessages(mAccount.getParentId());

        } else {

            long timeStamp = mMessages.get(mMessages.size() - 1).getTimeStamp();

            messages = mHelper.getMessages(mAccount.getParentId(), timeStamp);
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

    private void requestSendMessage(long pk) {

        String msg = mEditMessage.getText().toString();

        if (TextUtils.isEmpty(msg)) {

            Logger.d("return");

            return;
        }

        mEditMessage.setText("");

        ChatSend model = new ChatSend();

        model.setSenderID(mAccount.getID());
        model.setSenderName(mAccount.getName());

        model.setReceiverID(mAccount.getParentId());

        model.setMessage(msg);
        model.setTime(System.currentTimeMillis());

        model.setMsgType(0);
        model.setIsChildSender(1);
        model.setIsChildReceiver(0);
        model.setIsGroup(0);
        model.setMessagePk(pk);

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

    private void firstVisit() {

        boolean isFirst = STApplication.getBoolean(StaticValues.IS_SHOW_GUIDE, true);

        if (isFirst) {

            AndroidUtils.showKeyboard(mEditMessage);

            STApplication.putBoolean(StaticValues.IS_SHOW_GUIDE, false);

            mLayoutGuide.setVisibility(View.VISIBLE);
        }
    }

    private void showAdditionalInfo() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), AddInfoActivity.class);

        Account account = mHelper.getAccountInfo();

        intent.putExtra("ParentID", account.getParentId());
        intent.putExtra("Name", account.getName());
        intent.putExtra("Modify", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

        startActivity(intent);
    }
}
