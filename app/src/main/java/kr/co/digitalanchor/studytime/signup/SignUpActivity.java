package kr.co.digitalanchor.studytime.signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.model.ParentRegResult;
import kr.co.digitalanchor.studytime.model.ParentRegister;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-11.
 */
public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_REGISTER = 50001;

    private final int COMPLETE_REGISTER = 50002;

    EditText mEditEmailAddr;

    EditText mEditPassword;

    EditText mEditPasswordA;

    EditText mEditBirthDate;

    EditText mEditName;

    RadioGroup mRadioGender;

    CheckBox mCheckServiceInfo;

    Button mButtonServiceInfo;

    CheckBox mCheckPersonalInfo;

    Button mButtonPersonalInfo;

    Button mButtonSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        initView();
    }

    private void initView() {

        mEditEmailAddr = (EditText) findViewById(R.id.editEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditPasswordA = (EditText) findViewById(R.id.editPasswordAgain);

        mEditName = (EditText) findViewById(R.id.editName);

        mEditBirthDate = (EditText) findViewById(R.id.editBirthDate);

        mRadioGender = (RadioGroup) findViewById(R.id.radioGender);

        mCheckServiceInfo = (CheckBox) findViewById(R.id.checkServiceInfo);

        mButtonServiceInfo = (Button) findViewById(R.id.buttonServiceInfo);

        mCheckPersonalInfo = (CheckBox) findViewById(R.id.checkPersonalInfo);

        mButtonPersonalInfo = (Button) findViewById(R.id.buttonPersonalInfo);

        mButtonSignUp = (Button) findViewById(R.id.buttonSignUp);

        // set listener
        mButtonServiceInfo.setOnClickListener(this);

        mButtonPersonalInfo.setOnClickListener(this);

        mButtonSignUp.setOnClickListener(this);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_REGISTER:

                requestRegister();

                break;

            case COMPLETE_REGISTER:

                completeRegister();

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

            case R.id.buttonServiceInfo:

                showClause(0);

                break;

            case R.id.buttonPersonalInfo:

                showClause(1);

                break;

            case R.id.buttonSignUp:

                if (isValidateInfo()) {

                    sendEmptyMessage(REQUEST_REGISTER);
                }

                break;

            default:

                // it is dfault

                break;
        }
    }

    private void requestRegister() {

        String temp = null;

        ParentRegister model = new ParentRegister();

        model.setLang(STApplication.getLanguageCode());

        model.setEmail(mEditEmailAddr.getText().toString());

        model.setPassword(mEditPassword.getText().toString());

        temp = mEditName.getText().toString();

        if (!TextUtils.isEmpty(temp)) {

            model.setName(temp);

        } else {

            model.setName("");
        }

        temp = null;

        temp = mEditBirthDate.getText().toString();

        if (!TextUtils.isEmpty(temp)) {

            model.setBirthday(temp);

        } else {

            model.setBirthday("");
        }

        switch (mRadioGender.getCheckedRadioButtonId()) {

            case R.id.male:

                model.setSex("0");

                break;

            case R.id.female:

                model.setSex("1");

                break;

            default:

                model.setSex("");

                break;
        }

        // 전화번호
        model.setPhoneNumber(STApplication.getPhoneNumber());

        // 국가코드
        model.setNationalCode(STApplication.getNationalCode());

        // App 버전
        model.setAppVersion(STApplication.getAppVersionName());


        mQueue.add(HttpHelper.getParentRegister(model, new Response.Listener<ParentRegResult>() {
            @Override
            public void onResponse(ParentRegResult response) {

                Logger.d(response.toString());

                switch (response.getResultCode()) {

                    case SUCCESS:

                        sendEmptyMessage(COMPLETE_REGISTER);

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
        }));
    }

    private void completeRegister() {

        Toast.makeText(getApplicationContext(), "회원가입 성공하였습니다.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private boolean isValidateInfo() {

        String temp = null;

        String msg = null;

        do {

            // check required info

            temp = mEditEmailAddr.getText().toString();

            if (TextUtils.isEmpty(temp)) {

                msg = "이메일을 입력하세요.";

                break;
            }

            if (!StringValidator.isEmail(temp)) {

                msg = " 이메일 형식에 맞지 않습니다.";

                break;
            }

            temp = mEditPassword.getText().toString();

            if (TextUtils.isEmpty(temp)) {

                msg = "비밀번호를 입력하세요.";

                break;
            }

            if (!StringValidator.isPassword(temp)) {

                msg = "비밀번호 형식에 맞지 않습니다.";

                break;
            }

            temp = mEditPasswordA.getText().toString();

            if (TextUtils.isEmpty(temp)) {

                msg = "비밀번호를 재입력하세요.";

                break;
            }

            if (temp.compareTo(mEditPassword.getText().toString()) != 0) {

                msg = "비밀번호가 일치하지 않습니다.";

                break;
            }

            if (!mCheckServiceInfo.isChecked()) {

                msg = "서비스 이용약관을 동의하세요.";

                break;
            }

            if (!mCheckPersonalInfo.isChecked()) {

                msg = "개인정보 취급방침에 동의하세요.";

                break;
            }

            // check optional info

            temp = mEditBirthDate.getText().toString();

            if (!TextUtils.isEmpty(temp) && !StringValidator.isBirthDay(temp)) {

                msg = "생년월일 형식에 맞지 않습니다.";

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

    private void showClause(int opt) {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ClauseViewActivity.class);

        intent.putExtra("position", opt);

        startActivity(intent);

    }
}
