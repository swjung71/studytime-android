package kr.co.digitalanchor.studytime.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-06-15.
 */
public class LoginChildActivity extends BaseActivity implements View.OnClickListener {

    EditText mEditEmailAddr;

    EditText mEditPassword;

    EditText mEditChildName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);

        initView();
    }

    private void initView() {

        mEditEmailAddr = (EditText) findViewById(R.id.editEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditChildName = (EditText) findViewById(R.id.editChildName);

        ((Button) findViewById(R.id.buttonLogin)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonLogin:

                showAddInfo();

                break;

            default:

                break;
        }
    }

    private void showAddInfo() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), AddInfoActivity.class);

        startActivity(intent);
    }
}
