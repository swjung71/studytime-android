package kr.co.digitalanchor.studytime.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.control.ControlChildActivity;
import kr.co.digitalanchor.studytime.signup.SignUpActivity;
import kr.co.digitalanchor.utils.StringValidator;

/**
 * Created by Thomas on 2015-06-10.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_LOGIN = 50001;

    EditText mEditEmailAddr;

    EditText mEditPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_login);

        mEditEmailAddr = (EditText) findViewById(R.id.editEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        ((Button) findViewById(R.id.buttonLogin)).setOnClickListener(this);

        ((Button) findViewById(R.id.buttonSignUp)).setOnClickListener(this);

        ((Button) findViewById(R.id.buttonFindPwd)).setOnClickListener(this);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_LOGIN:

                requestParentLogin();

                break;

            default:

                break;
        }

    }

    @Override
    public void onClick(View v) {

        if (isDuplicateRuns()) {

            return;
        }

        switch (v.getId()) {

            case R.id.buttonSignUp:

                showSignUp();

                break;

            case R.id.buttonLogin:

                if (isValidateInputInfo()) {

                    // TODO 다음 화면

                    sendEmptyMessage(REQUEST_LOGIN);

                    Toast.makeText(getApplicationContext(), "다음 화면 : 메인 화면", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.buttonFindPwd:

                break;

            default:

                break;
        }
    }

    private boolean isValidateInputInfo() {

        String msg = null;
        String temp = null;

        do {

            temp = mEditEmailAddr.getText().toString();

            if (TextUtils.isEmpty(temp)) {

                msg = "경고 문구 : 이메일 미 입력";

                break;

            }

            if (!StringValidator.isEmail(temp)) {

                msg = "경고 문구 : 이메일 형식 틀림";

                break;
            }

            temp = mEditPassword.getText().toString();

            if (TextUtils.isEmpty(temp)) {

                msg = "경고 문구 : 비밀번호 미 입력";

                break;
            }

        } while (false);

        if (TextUtils.isEmpty(msg)) {

            return true;

        } else {

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private void requestParentLogin() {

        String tmp = null;


    }

    private void showSignUp() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), SignUpActivity.class);

        startActivity(intent);
    }

    private void showMain() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ControlChildActivity.class);

        startActivity(intent);

        finish();
    }
}
