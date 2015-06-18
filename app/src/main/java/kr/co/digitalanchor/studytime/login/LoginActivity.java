package kr.co.digitalanchor.studytime.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.control.ControlChildActivity;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.signup.SignUpActivity;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-10.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_LOGIN = 50001;
    private final int COMPLETE_LOGIN = 50002;
    private final int REQUEST_FIND_PASSWORD = 50003;

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

            case COMPLETE_LOGIN:

                completeLogin();

                break;

            case REQUEST_FIND_PASSWORD:

                requestParentFindPwd();

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

                if (isValidateLoginInfo()) {

                    sendEmptyMessage(REQUEST_LOGIN);

                }

                break;

            case R.id.buttonFindPwd:

                if (isValidateEmailInfo()) {

                    sendEmptyMessage(REQUEST_FIND_PASSWORD);
                }

                break;

            default:

                break;
        }
    }

    private boolean isValidateLoginInfo() {

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

        ParentLogin model = new ParentLogin();

        model.setEmail(mEditEmailAddr.getText().toString());

        model.setPassword(mEditPassword.getText().toString());

        SimpleXmlRequest request = HttpHelper.getParentLogin(model, new Response.Listener<ParentLoginResult>() {

            @Override
            public void onResponse(ParentLoginResult response) {

                System.out.println("onResponse");

                switch (response.getResultCode()) {

                    case SUCCESS:

                        break;

                    default:

                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // TODO
            }
        });

        if (request != null) {

            mQueue.add(request);

        }
    }

    private void completeLogin() {

        STApplication.putString("isParent", "parent");

        STApplication.putString("id", "id");

        showMain();

    }

    private boolean isValidateEmailInfo() {

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

        } while (false);

        if (TextUtils.isEmpty(msg)) {

            return true;

        } else {

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private void requestParentFindPwd() {

        Toast.makeText(getApplicationContext(), "개발중", Toast.LENGTH_SHORT).show();
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
