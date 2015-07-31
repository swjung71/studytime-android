package kr.co.digitalanchor.studytime.login;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.devicepolicy.AdminReceiver;
import kr.co.digitalanchor.studytime.model.AllPackage;
import kr.co.digitalanchor.studytime.model.AllPackageResult;
import kr.co.digitalanchor.studytime.model.ChildRegResult;
import kr.co.digitalanchor.studytime.model.ChildRegister;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.signup.ClauseViewActivity;
import kr.co.digitalanchor.utils.AndroidUtils;
import kr.co.digitalanchor.utils.MD5;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-15.
 */
public class AddInfoActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_ADD_INFO = 50001;
    private final int REQUEST_UPLOAD_PACKAGES = 50002;
    private final int COMPLETE_ADD_INFO = 50003;

    private final int ACTIVATION_REQUEST = 40003;

    EditText mEditName;

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

    private boolean isModify;

    private DBHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_info);

        getIntentData();

        initView();

        mHelper = new DBHelper(getApplicationContext());
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

        mEditName = (EditText) findViewById(R.id.editName);

        mEditName.setVisibility(isModify ? View.VISIBLE : View.GONE);

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

            case REQUEST_UPLOAD_PACKAGES:


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

                STApplication.putBoolean(StaticValues.SHOW_ADMIN, resultCode != RESULT_OK);

                completeRegister(mParentID, mChildID);

                sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));

                sendEmptyMessage(COMPLETE_ADD_INFO, 300);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        if (isModify) {

            STApplication.stopAllActivity();
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

        if (data.containsKey("Modify")) {

            isModify = data.getBoolean("Modify", false);


        } else {

            isModify = false;
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

        if (isModify) {

            if (TextUtils.isEmpty(mEditName.getText().toString())) {

                model.setName(mModel.getName());

            } else {

                model.setName(mEditName.getText().toString());
            }

        } else {

            model.setName(mModel.getName());
        }

        SimpleXmlRequest request = HttpHelper.getChildRegister(model,
                new Response.Listener<ChildRegResult>() {
                    @Override
                    public void onResponse(ChildRegResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                dismissLoading();

                                Logger.d("gfd " + response.getParentID() + " " + response.getChildID());

                                mParentID = response.getParentID();
                                mChildID = response.getChildID();

                                if (!isModify) {

                                    showAdmin();

                                } else {

                                    sendEmptyMessage(COMPLETE_ADD_INFO);
                                }


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

        Logger.i(parentId + " " + childId + "  " + mModel.getName());

        mHelper.insertAccount(childId, mModel.getName(), parentId);

    }

    private void showAdmin() {

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                new ComponentName(this, AdminReceiver.class));

       startActivityForResult(intent, ACTIVATION_REQUEST);

//        startActivityForResult(intent, REQUEST_UPLOAD_PACKAGES);
    }

    /**
     * Package 목록 보내기
     */
    private void requestAddApps() {

        showLoading();

        Account account = mHelper.getAccountInfo();

        List<PackageModel> packages = getAppListFromDevice();

        mHelper.addApplications(packages);

        AllPackage model = new AllPackage();

        model.setPackages(packages);
        model.setChildId(account.getID());
        model.setParentId(account.getParentId());

        SimpleXmlRequest request = HttpHelper.getAddAppList(model,
                new Response.Listener<AllPackageResult>() {
                    @Override
                    public void onResponse(AllPackageResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                // TODO local db update
                                updateLocalDB(response.getPackages());

                            default:

                                dismissLoading();

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

    private void updateLocalDB(List<PackageModel> packages) {

        for (PackageModel model : packages) {

            mHelper.updateApplicationAfterReg(model.getPackageName(), model.getPackageId(),
                    model.getHasIconDB());
        }
    }

    /**
     * 처음 한번 호출
     *
     * @return
     */
    private List<PackageModel> getAppListFromDevice() {

        PackageManager manager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        List<PackageModel> packageModels = new ArrayList<>();

        for (ResolveInfo r : apps) {

            PackageInfo packageInfo = null;

            try {

                packageInfo = manager.getPackageInfo(r.activityInfo.packageName, 0);

            } catch (PackageManager.NameNotFoundException e) {

                continue;
            }

            if (packageInfo == null) {

                continue;
            }

            PackageModel model = new PackageModel();

            model.setPackageName(packageInfo.packageName);
            model.setHash(MD5.getHash(packageInfo.packageName));
            model.setLabelName(packageInfo.applicationInfo.loadLabel(manager).toString());
            model.setPackageVersion(packageInfo.versionName);
            model.setIsExceptionApp(0);
            model.setIconHash(MD5.getHash(packageInfo.packageName + packageInfo.versionName));

            model.setTimestamp(AndroidUtils.convertTimeStamp4Chat(packageInfo.firstInstallTime));

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                model.setIsDefaultApp(1);

            } else {

                model.setIsDefaultApp(0);
            }

            packageModels.add(model);
        }

        return packageModels;
    }
}
