package kr.co.digitalanchor.studytime.login;

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
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.model.ChildRegResult;
import kr.co.digitalanchor.studytime.model.ChildRegister;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-15.
 */
public class AddInfoActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_ADD_INFO = 50001;
    private final int COMPLETE_ADD_INFO = 50002;

    EditText mEditBirthDate;

    RadioGroup mCheckGender;

    CheckBox mCheckServiceInfo;

    CheckBox mCheckPersonalInfo;

    Button mButtonServiceInfo;

    Button mButtonPersonalInfo;

    Button mButtonConfirm;

    ChildRegister mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_info);

        initView();

        getIntentData();
    }

    private void initView() {

        mEditBirthDate = (EditText) findViewById(R.id.editBirthDate);

        mCheckGender = (RadioGroup) findViewById(R.id.radioGender);

        mCheckServiceInfo = (CheckBox) findViewById(R.id.checkServiceInfo);

        mCheckPersonalInfo = (CheckBox) findViewById(R.id.checkPersonalInfo);

        mButtonServiceInfo = (Button) findViewById(R.id.buttonServiceInfo);
        mButtonServiceInfo.setOnClickListener(this);

        mButtonPersonalInfo = (Button) findViewById(R.id.buttonPersonalInfo);
        mButtonPersonalInfo.setOnClickListener(this);

        mButtonConfirm = (Button) findViewById(R.id.buttonConfirm);
        mButtonConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonServiceInfo:

                Toast.makeText(getApplicationContext(), "서비스 이용약관", Toast.LENGTH_SHORT).show();

                break;

            case R.id.buttonPersonalInfo:

                Toast.makeText(getApplicationContext(), "개인정보 취급방침", Toast.LENGTH_SHORT).show();

                break;

            case R.id.buttonConfirm:

                if (isValidate()) {

                    sendEmptyMessage(REQUEST_ADD_INFO);
                }

                break;

            default:

                break;
        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_ADD_INFO:

                requestSendAdditionalInfo();

                break;

            case COMPLETE_ADD_INFO:

                setResult(RESULT_OK);

                finish();

                break;


            default:

                break;
        }
    }

    private boolean isValidate() {

        String tmp = null;
        String msg = null;

        do {

            tmp = mEditBirthDate.getText().toString();

            if (!TextUtils.isEmpty(tmp) && !StringValidator.isBirthDay(tmp)) {

                msg = "경고 : 생년월일 포멧 틀림";

                break;
            }

            if (!mCheckServiceInfo.isChecked()) {

                msg = "경고 : 미 체크";

                break;
            }

            if (!mCheckPersonalInfo.isChecked()) {

                msg = "경고 : 미 체크";

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

    private void getIntentData() {

        Bundle data = getIntent().getExtras();

        mModel = new ChildRegister();

        if (data.containsKey("ParentID")) {

            mModel.setParentID(data.getString("ParentID"));
        }

        if (data.containsKey("Name")) {

            mModel.setName(data.getString("Name"));
        }
    }

    private void requestSendAdditionalInfo() {

        ChildRegister model = null;

        String tmp = null;

        try {

            model = mModel.clone();

        } catch (CloneNotSupportedException e) {

            model = new ChildRegister();
        }

        // 전화번호
        model.setPhoneNumber(STApplication.getPhoneNumber());

        // 국가 코드
        model.setNationalCode(STApplication.getNationalCode());

        // GCM
        model.setGcm(STApplication.getString(StaticValues.GCM_REG_ID));

        // Gender
        switch (mCheckGender.getCheckedRadioButtonId()) {

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

        tmp = mEditBirthDate.getText().toString();

        // Birth date
        model.setBirthday(TextUtils.isEmpty(tmp) ? "" : tmp);

        // App Version
        model.setAppVersion(STApplication.getAppVersionName());

        // 유니크 넘버
        model.setDevNum(STApplication.getDeviceNumber());

        model.setMac(STApplication.getMAC());

        SimpleXmlRequest request = HttpHelper.getChildRegister(model,
                new Response.Listener<ChildRegResult>() {
                    @Override
                    public void onResponse(ChildRegResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                STApplication.putString("ParentID", response.getParentID());
                                STApplication.putString("ChildID", response.getChildID());
                                STApplication.putString("isParent", "child");

                                sendEmptyMessage(COMPLETE_ADD_INFO);

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

        System.out.println("mQueue.add");

        if (request != null) {

            System.out.println("mQueue.add 01");

            mQueue.add(request);
        }
    }
}
