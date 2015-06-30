package kr.co.digitalanchor.studytime.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.control.ListChildActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.NewPassword;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentPhoneInfo;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.signup.SignUpActivity;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-10.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_LOGIN = 50001;
    private final int COMPLETE_LOGIN = 50002;
    private final int REQUEST_PARENT_INFO = 50003;
    private final int REQUEST_FIND_PASSWORD = 50004;

    EditText mEditEmailAddr;

    EditText mEditPassword;

    DBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_login);

        initView();

        mHelper = new DBHelper(getApplicationContext());

    }

    private void initView() {

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

            case REQUEST_PARENT_INFO:

                requestPhoneInfo();

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

        showLoading();

        ParentLogin model = new ParentLogin();

        model.setEmail(mEditEmailAddr.getText().toString());

        model.setPassword(mEditPassword.getText().toString());

        SimpleXmlRequest request = HttpHelper.getParentLogin(model, new Response.Listener<ParentLoginResult>() {

            @Override
            public void onResponse(ParentLoginResult res) {

                Logger.d(res.toString());

                switch (res.getResultCode()) {

                    case SUCCESS:

                        mHelper.insertAccount(res.getParentID(), 1, res.getName(), /*res.getCoin()*/"200", res.getEmail());

                        mHelper.insertChildren(res.getChildren());

                        sendEmptyMessage(REQUEST_PARENT_INFO);

                        break;

                    default:

                        handleResultCode(res.getResultCode(), res.getResultMessage());

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

        } else {

            dismissLoading();
        }
    }

    private void completeLogin() {

        showMain();

    }

    private void requestPhoneInfo() {

        ParentPhoneInfo model = new ParentPhoneInfo();

        Account account = mHelper.getAccountInfo();

        // ParentID
        model.setParentID(account.getID());

        model.setLang(STApplication.getLanguageCode());

        model.setPhoneNumber(STApplication.getPhoneNumber());

        model.setAppVersion(STApplication.getAppVersionName());

        // GCM
        model.setGcm(STApplication.getString(StaticValues.GCM_REG_ID));

        model.setNationCode(STApplication.getNationalCode());

        SimpleXmlRequest request = HttpHelper.getParentPhoneInfo(model,
                new Response.Listener<GeneralResult>() {

                    @Override
                    public void onResponse(GeneralResult response) {

                        dismissLoading();

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                sendEmptyMessage(COMPLETE_LOGIN);

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

        } else {

            dismissLoading();
        }
    }

    private boolean isValidateEmailInfo() {

        String msg = null;
        String temp = null;

        do {

            temp = mEditEmailAddr.getText().toString();

            if (TextUtils.isEmpty(temp)) {

                msg = "이메일을 입력하세요.";

                break;

            }

            if (!StringValidator.isEmail(temp)) {

                msg = "이메일 형식에 맞지 않습니다.";

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

        showLoading();

        NewPassword model = new NewPassword();

        model.setEmail(mEditEmailAddr.getText().toString());

        SimpleXmlRequest request = HttpHelper.getTemporaryPassword(model,
                new Response.Listener<GeneralResult>() {
                    @Override
                    public void onResponse(GeneralResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                dismissLoading();

                                MaterialDialog.Builder buidler = new MaterialDialog.Builder(LoginActivity.this);

                                buidler.title("비밀번호 찾기")
                                        .content("회원가입하신 이메일 주소로 임시 비밀번호를 전송해 드립니다.")
                                        .positiveText("확인").build().show();

                                break;

                            default:

                                handleResultCode(response.getResultCode(),
                                        response.getResultMessage());

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

    private void showSignUp() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), SignUpActivity.class);

        startActivity(intent);
    }

    private void showMain() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ListChildActivity.class);

        startActivity(intent);

        finish();
    }
}
