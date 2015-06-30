package kr.co.digitalanchor.studytime.login;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
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
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.devicepolicy.AdminReceiver;
import kr.co.digitalanchor.studytime.model.ChildRegResult;
import kr.co.digitalanchor.studytime.model.ChildRegister;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.signup.ClauseViewActivity;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-15.
 */
public class AddInfoActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_ADD_INFO = 50001;
    private final int COMPLETE_ADD_INFO = 50002;
    private final int ACTIVATION_REQUEST = 50002;

    EditText mEditBirthDate;

    RadioGroup mCheckGender;

    CheckBox mCheckServiceInfo;

    CheckBox mCheckPersonalInfo;

    Button mButtonServiceInfo;

    Button mButtonPersonalInfo;

    Button mButtonConfirm;

    ChildRegister mModel;

    private String mParentID;

    private String mChildID;

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

                showClause(0);

                break;

            case R.id.buttonPersonalInfo:

                showClause(1);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case ACTIVATION_REQUEST:

                if (resultCode == RESULT_OK) {

                    completeRegister(mParentID, mChildID);

                    sendEmptyMessage(COMPLETE_ADD_INFO);

                } else {

                    STApplication.resetApplication();
                }
        }
    }

    private boolean isValidate() {

        String tmp = null;
        String msg = null;

        do {

            tmp = mEditBirthDate.getText().toString();

            if (!TextUtils.isEmpty(tmp) && !StringValidator.isBirthDay(tmp)) {

                msg = "생년월일 형식에 맞지 않습니다.";

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

    private void showClause(int opt) {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ClauseViewActivity.class);

        intent.putExtra("position", opt);
        intent.putExtra("isParent", false);

        startActivity(intent);
    }

    private void requestSendAdditionalInfo() {

        showLoading();

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

        // 언어 설정
        model.setLang(STApplication.getLanguageCode());

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

                                dismissLoading();

                                mParentID = response.getParentID();
                                mChildID = response.getChildID();

                                showAdmin();

//                                completeRegister(mParentID, mChildID);

//                                sendEmptyMessage(COMPLETE_ADD_INFO);

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

    private void completeRegister(String parentId, String childId) {

        Logger.i(parentId + " " + childId);

        DBHelper helper = new DBHelper(getApplicationContext());

        helper.insertAccount(childId, mModel.getName(), parentId);

    }

    private void showAdmin() {

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                new ComponentName(this, AdminReceiver.class));

        startActivityForResult(intent, ACTIVATION_REQUEST);
    }
}
