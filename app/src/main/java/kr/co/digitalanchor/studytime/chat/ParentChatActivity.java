package kr.co.digitalanchor.studytime.chat;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.ChatSend;
import kr.co.digitalanchor.studytime.model.ChatSendResult;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ParentChatActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_SEND_MESSAGE = 50001;

    EditText mEditMessage;

    ImageButton mButtonSend;

    ListView mListChat;

    DBHelper mHelper;

    Child mChild;

    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_chat);

        initView();

        getData();

        mHelper = new DBHelper(getApplicationContext());

        mAccount = mHelper.getAccountInfo();
    }

    private void initView() {

        mEditMessage = (EditText) findViewById(R.id.editMessage);

        mButtonSend = (ImageButton) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(this);

        mListChat = (ListView) findViewById(R.id.listMessage);
    }

    private void getData() {

        Bundle data = getIntent().getExtras();

        if (data == null) {

            finish();

            return;
        }

        mChild = new Child();

        if (data.containsKey("ChildID")) {

            mChild.setChildID(data.getString("ChildID"));
        }

        if (data.containsKey("Name")) {

            mChild.setName(data.getString("Name"));
        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_SEND_MESSAGE:

                requestSendMessage();

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

    private void requestSendMessage() {

        String msg = mEditMessage.getText().toString();

        if (TextUtils.isEmpty(msg)) {

            Logger.d("return");

            return;
        }

        mEditMessage.setText("");

        ChatSend model = new ChatSend();

        model.setSenderID(mAccount.getID());
        model.setSenderName(mAccount.getName());

        model.setReceiverID(mChild.getChildID());
        model.setMessage(msg);
        model.setTime(AndroidUtils.getCurrentTime4Chat());

        model.setMsgType(0);
        model.setIsChildSender(0);
        model.setIsChildReceiver(1);
        model.setIsGroup(0);

        long pk = mHelper.insertChat(model.getIsGroup(), model.getSenderID(), model.getReceiverID(), model.getSenderName(),
                model.getMessage(), model.getTime(), 1, 0, model.getMsgType());

        if (pk < 0) {

            return;
        }

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

        if (request != null) {

            mQueue.add(request);
        }
    }
}
