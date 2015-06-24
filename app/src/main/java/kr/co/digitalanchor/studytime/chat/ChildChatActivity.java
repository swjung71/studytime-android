package kr.co.digitalanchor.studytime.chat;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-06-15.
 */
public class ChildChatActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CHAT_SEND = 50001;

    ImageButton mButtonSetting;

    ImageButton mButtonSend;

    Button mButtonClose;

    EditText mEditMessage;

    ListView mChatList;

    View mLayoutGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_chat);

        initView();
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

        mChatList = (ListView) findViewById(R.id.listChat);
     }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_CHAT_SEND:

                break;

            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonSetting:

                showGuide(true);

                break;

            case R.id.buttonClose:

                showGuide(false);

                break;

            case R.id.buttonSend:

                break;


            default:

                break;
        }
    }

    private void showGuide(boolean flag) {

        if (flag) {

            mLayoutGuide.setVisibility(View.VISIBLE);

        } else {

            mLayoutGuide.setVisibility(View.GONE);
        }
    }
}
