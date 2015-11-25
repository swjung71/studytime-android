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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import kr.co.digitalanchor.studytime.app.AppIconUploader;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.devicepolicy.AdminReceiver;
import kr.co.digitalanchor.studytime.model.AddPackageElement;
import kr.co.digitalanchor.studytime.model.AddPackageModel;
import kr.co.digitalanchor.studytime.model.AllPackageResult;
import kr.co.digitalanchor.studytime.model.Child;
import kr.co.digitalanchor.studytime.model.ChildRegResult;
import kr.co.digitalanchor.studytime.model.ChildRegister;
import kr.co.digitalanchor.studytime.model.PackageResult;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.AndroidUtils;
import kr.co.digitalanchor.utils.MD5;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-11-10.
 */
public class SelectChildActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener {

    private final int REQUEST_ADD_INFO = 50001;
    private final int REQUEST_UPLOAD_PACKAGES = 50002;
    private final int COMPLETE_ADD_INFO = 50003;

    private final int ACTIVATION_ACCESS_REQUEST = 40002;

    private final int ACTIVATION_REQUEST = 40003;

    ListView mList;

    View mHeader;

    View mFooter;

    ChildListAdapter mAdapter;

    List<Child> mChildren;

    private DBHelper mHelper;

    String mParentID;
    String mChildID;
    String mName;
    String mExpiration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_select);

        Bundle data = getIntent().getExtras();

        if (data != null) {

            mParentID = data.getString("ParentID");

        }

        mHelper = new DBHelper(getApplicationContext());

        initView();

        requestChildList();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void initView() {

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        mHeader = inflater.inflate(R.layout.layout_child_header, null);
        mFooter = inflater.inflate(R.layout.layout_child_footer_c, null);
        mFooter.setOnClickListener(this);

        mList = (ListView) findViewById(R.id.list);

        mList.setOnItemClickListener(this);

        mList.addHeaderView(mHeader);
        mList.addFooterView(mFooter);

        mList.setAdapter(makeAdapter());

    }

    private ChildListAdapter makeAdapter() {

        if (mChildren == null) {

            mChildren = new ArrayList<>();

        }

        mAdapter = new ChildListAdapter(getApplicationContext(), 0, mChildren);

        return mAdapter;
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_ADD_INFO:

                requestChildReg();

                break;

            case REQUEST_UPLOAD_PACKAGES:

                requestAddApps();

                break;

            case COMPLETE_ADD_INFO:

                startService(new Intent(getApplicationContext(), AppIconUploader.class));

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

//                completeRegister(mParentID, mChildID);
//
//                sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));
//
//                sendEmptyMessage(COMPLETE_ADD_INFO);

                sendEmptyMessage(REQUEST_UPLOAD_PACKAGES);

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            if (position < 1) {

                return;
            }

            Child child = mChildren.get(position - 1);

            mName = child.getName();

            mChildID = child.getChildID();

            mExpiration = child.getExpirationYN();

            sendEmptyMessage(REQUEST_ADD_INFO);

        } catch (Exception e) {

            Logger.e(e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.footerSendLink:

                setResult(INPUT_ADD_INFO);

                finish();

                break;
        }
    }

    private void requestChildList() {

        showLoading();

        ParentModel model = new ParentModel();

        model.setParentId(mParentID);

        SimpleXmlRequest request = HttpHelper.getSyncParentData(model,
                new Response.Listener<ParentLoginResult>() {
                    @Override
                    public void onResponse(ParentLoginResult res) {

                        dismissLoading();

                        switch (res.getResultCode()) {

                            case SUCCESS:

                                List<Child> list = res.getChildren();

                                if (list != null) {

                                    for (Child child : list) {

                                        child.setName(AndroidUtils.convertFromUTF8(child.getName()));

                                        mChildren.add(child);

                                    }

                                    if (mChildren.size() >= StaticValues.MAX_CHILDREN_COUNT) {

                                        mFooter.setVisibility(View.GONE);
                                    }

                                    mAdapter.notifyDataSetChanged();
                                }

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

        addRequest(request);
    }

    private void requestChildReg() {

        showLoading();

        ChildRegister model = new ChildRegister();

        model.setParentID(mParentID);

        model.setChildId(mChildID);

        model.setName(mName);

        // 전화번호
        model.setPhoneNumber(STApplication.getPhoneNumber());

        // 국가 코드
        model.setNationalCode(STApplication.getNationalCode());

        // GCM
        model.setGcm(STApplication.getString(StaticValues.GCM_REG_ID));

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
                    public void onResponse(ChildRegResult res) {

                        dismissLoading();

                        switch (res.getResultCode()) {

                            case SUCCESS:

                                mParentID = res.getParentID();
                                mChildID = res.getChildID();

                                showAdmin();

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

        addRequest(request);
    }

    private void completeRegister(String parentId, String childId) {

        Logger.i(parentId + " " + childId + "  " + mName);

        mHelper.insertAccount(childId, mName, parentId, mExpiration);

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

        List<AddPackageElement> packages = getAppListFromDevice();

        mHelper.addAppList(packages);

        if (packages != null)
            packages.clear();

        packages = null;

        packages = mHelper.getAddPackageList();

        AddPackageModel model = new AddPackageModel();

        model.setPackages(packages);
        model.setChildId(mChildID);
        model.setParentId(mParentID);

        SimpleXmlRequest request = HttpHelper.getAddAppList(model,
                new Response.Listener<AllPackageResult>() {
                    @Override
                    public void onResponse(AllPackageResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                // TODO local db update
                                updateLocalDB(response.getPackages());

                                completeRegister(mParentID, mChildID);

                                sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));

                                sendEmptyMessage(COMPLETE_ADD_INFO);

                                dismissLoading();

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

    private void updateLocalDB(List<PackageResult> packages) {

        if (packages == null) {

            return;
        }

        for (PackageResult model : packages) {

            mHelper.updateApplicationAfterReg(model.getPackageName(), model.getPackageId(),
                    model.getDoExistInDB(), model.getState(), 0);
        }
    }

    /**
     * 처음 한번 호출
     */
    private List<AddPackageElement> getAppListFromDevice() {

        PackageManager manager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        List<AddPackageElement> packageModels = new ArrayList<>();

        for (ResolveInfo r : apps) {

            PackageInfo packageInfo = null;

            try {

                packageInfo = manager.getPackageInfo(r.activityInfo.packageName, 0);

            } catch (PackageManager.NameNotFoundException e) {

                continue;
            }

            if (packageInfo == null) {

                continue;

            } else if (packageInfo.packageName.equals(getApplicationContext().getPackageName())
                    || packageInfo.packageName.contains(".mms")
                    || packageInfo.packageName.contains(".contacts")
                    || packageInfo.packageName.contains("com.android.phone")
                    || packageInfo.packageName.contains("com.android.settings")
                    || packageInfo.packageName.contains("com.android.dialer")) {

                continue;
            }

            AddPackageElement model = new AddPackageElement();

            model.setPackageName(packageInfo.packageName);
            model.setHash(MD5.getHash(packageInfo.packageName));
            model.setLabelName(packageInfo.applicationInfo.loadLabel(manager).toString());
            model.setPackageVersion(packageInfo.versionName);
            model.setIsExceptionApp(0);
            model.setHasIcon(1);

            model.setTimestamp(AndroidUtils.convertCurrentTime4Chat(packageInfo.firstInstallTime));

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
