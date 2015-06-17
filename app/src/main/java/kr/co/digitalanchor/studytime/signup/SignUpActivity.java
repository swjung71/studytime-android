package kr.co.digitalanchor.studytime.signup;

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

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.util.HashMap;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.model.ParentRegResult;
import kr.co.digitalanchor.studytime.model.ParentRegister;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.utils.StringValidator;

/**
 * Created by Thomas on 2015-06-11.
 */
public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_REGISTER = 50001;

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

            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonServiceInfo:


                break;

            case R.id.buttonPersonalInfo:

                break;

            case R.id.buttonSignUp:

                if (isValidateInfo()) {

                    sendEmptyMessage(REQUEST_REGISTER);
                }

                break;

            default:

                break;
        }
    }

    private void requestRegister() {

        String temp = null;

        ParentRegister model = new ParentRegister();

        model.setEmail(mEditEmailAddr.getText().toString());

        temp = mEditName.getText().toString();

        if (!TextUtils.isEmpty(temp)) {

            model.setName(temp);
        }

        temp = null;

        temp = mEditBirthDate.getText().toString();

        if (!TextUtils.isEmpty(temp)) {

            model.setBirthday(temp);
        }

        switch (mRadioGender.getId()) {

            case R.id.male:

                model.setSex("0");

                break;

            case R.id.female:

                model.setSex("1");

                break;

            default:

                model.setSex(null);

                break;
        }

        // 전화번호

        // 국가코드

        model.setAppVersion(STApplication.getAppVersionName());

        Serializer serializer = new Persister();

        StringWriter writer = new StringWriter();

        try {

            serializer.write(model, writer);

        } catch (Exception e) {


        }

        HashMap<String, String> map = new HashMap<>();

        map.put("xml", writer.toString());

        mQueue.add(HttpHelper.getParentRegister(map, new Response.Listener<ParentRegResult>() {
            @Override
            public void onResponse(ParentRegResult response) {

                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }));

    }

    private boolean isValidateInfo() {

        String temp = null;

        String msg = null;

        do {

            // check required info

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

            if (!StringValidator.isPassword(temp)) {

                msg = "경고 문구 : 비밀번호 형식 틀림";

                break;
            }

            temp = mEditPasswordA.getText().toString();

            if (TextUtils.isEmpty(temp)) {

                msg = "경고 문구 : 비밀번호 재입력 안함";

                break;
            }

            if (temp.compareTo(mEditPassword.getText().toString()) != 0) {

                msg = "경고 문구 : 입력된 비밀번호 불일치";

                break;
            }

            if (!mCheckServiceInfo.isChecked()) {

                msg = "경고 문구 : 서비스 이용약관에 미 동의";

                break;
            }

            if (!mCheckPersonalInfo.isChecked()) {

                msg = "경고 문구 : 개인정보 취급방침에 미 동의";

                break;
            }

            // check optional info

            temp = mEditBirthDate.getText().toString();

            if (!TextUtils.isEmpty(temp) && !StringValidator.isBirthDay(temp)) {

                msg = "경고 문구 : 생년월일 형식 틀림";

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
}
