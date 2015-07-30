package kr.co.digitalanchor.studytime.intro;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.igaworks.IgawCommon;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.chat.ChildChatActivity;
import kr.co.digitalanchor.studytime.control.ListChildActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.login.LoginActivity;
import kr.co.digitalanchor.studytime.login.LoginChildActivity;
import kr.co.digitalanchor.studytime.model.GCMUpdate;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.GetVersion;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.VersionResult;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-10.
 */
public class IntroActivity extends BaseActivity implements View.OnClickListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    View mLayoutParentIntro;

    View mLayoutChildIntro;

    View mLayoutSelectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // TODO Application class에서 호출 하면 안됨?
        IgawCommon.startApplication(this);

        setContentView(R.layout.activity_intro);

        initialize();

        selectIntro();

    }

    private void initialize() {

        mLayoutParentIntro = findViewById(R.id.layoutParentIntro);

        mLayoutChildIntro = findViewById(R.id.layoutChildIntro);

        mLayoutSelectMode = findViewById(R.id.layoutSelectMode);

        findViewById(R.id.buttonModeKids).setOnClickListener(this);

        findViewById(R.id.buttonModeParent).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (isDuplicateRuns()) {

            return;
        }

        switch (v.getId()) {

            case R.id.buttonModeKids:

                showChildIntro();

                break;

            case R.id.buttonModeParent:

                showParentIntro();

                break;

            default:

                Toast.makeText(getApplicationContext(), "selected default in onClick()", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void selectIntro() {

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        if (TextUtils.isEmpty(account.getID())) {

            startActivity(new Intent(getApplicationContext(), GuideActivity.class));

            return;

        } else if (account.isChild() < 0) {

            // 로그인 되었지만 부모용인지 알수 없음

            return;

        } else if (account.isChild() == 1) {

            // 부모용으로 로그인
            showParentIntro();

        } else {

            // 자녀용으로 로그인
            showChildIntro();
        }
    }

    private void showParentIntro() {

        mLayoutSelectMode.setVisibility(View.GONE);

        mLayoutParentIntro.setVisibility(View.VISIBLE);

        registerGCM();

    }

    private void showChildIntro() {

        mLayoutSelectMode.setVisibility(View.GONE);

        mLayoutChildIntro.setVisibility(View.VISIBLE);

        registerGCM();

    }

    private void showNextScreen(final int delayed) {

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        final Intent intent = new Intent();

        if (!TextUtils.isEmpty(account.getID())) {

            if (account.isChild() < 0) {

                return;

            } else if (account.isChild() == 1) {

                intent.setClass(getApplicationContext(), ListChildActivity.class);

            } else {

                intent.setClass(getApplicationContext(), ChildChatActivity.class);
            }

        } else if (mLayoutParentIntro.getVisibility() == View.VISIBLE) {


            intent.setClass(getApplicationContext(), LoginActivity.class);


        } else if (mLayoutChildIntro.getVisibility() == View.VISIBLE) {

            intent.setClass(getApplicationContext(), LoginChildActivity.class);

        }

        getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(intent);

                finish();

            }
        }, delayed);
    }

    private void registerGCM() {

        if (checkPlayServices()) {

            registerInBackground();

        }
    }

    // new
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {

                Logger.d("This device is not supported.");
                finish();

            }

            return false;
        }

        return true;
    }

    private String getRegistrationId() {

        String registrationId = STApplication.getRegistrationId();

        if (TextUtils.isEmpty(registrationId)) {

            Logger.d("Registration not found.");
        }

        int registeredVersion = STApplication.getRegisteredVersion();

        int currentVersion = STApplication.getAppVersionCode();

        if (registeredVersion != currentVersion) {

            Logger.d("Registration App version changed.");

            return "";
        }

        return registrationId;
    }

    private void registerInBackground() {

        Logger.d("OK");

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {

                    try {

                        AdvertisingIdClient.Info adInfo =
                                AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());

                        STApplication.putString(StaticValues.AD_ID, adInfo.getId());

                        Logger.d("ad id " + adInfo.getId());

                    } catch (Exception e) {

                        Logger.e(e.toString());

                    }

                    /// Validation
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

                    /// Get Registration ID
                    String id = gcm.register(StaticValues.GCM_SENDER_ID);


                    storeRegistrationId(id);

                    Logger.d("registration id " + id);

                    return "Succeed";

                } catch (IOException ex) {

                    Logger.e(ex.toString());

                    return "Failed";
                }
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

                if (result.compareTo("Succeed") == 0) {

                    getUpdateGCM();

                    //showNextScreen(300);
                }
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(String id) {

        int version = STApplication.getAppVersionCode();

        STApplication.putRegistrationId(id);
        STApplication.putRegisteredVersion(version);
    }

    private void getUpdateGCM() {

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        if (TextUtils.isEmpty(account.getID())) {

            getAvailableUpdate();

            return;
        }

        GCMUpdate model = new GCMUpdate();

        model.setGCM(STApplication.getRegistrationId());
        model.setId(account.getID());
        model.setIsChild((account.getIsChild() == 0) ? 1 : 0);

        SimpleXmlRequest request = HttpHelper.getUpdate(model, new Response.Listener<GeneralResult>() {
            @Override
            public void onResponse(GeneralResult response) {

                getAvailableUpdate();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                getAvailableUpdate();
            }
        });

        addRequest(request);
    }

    private void getAvailableUpdate() {

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        if (account == null) {

            account = new Account();
            account.setIsChild(1);
        }

        GetVersion model = new GetVersion();

        model.setIsChild((account.isChild() == 0) ? 1 : 0);

        SimpleXmlRequest request = HttpHelper.getVersion(model, new Response.Listener<VersionResult>() {
            @Override
            public void onResponse(VersionResult response) {

                switch (response.getResultCode()) {

                    case SUCCESS:

                        if (STApplication.isUpdate(response.getVersion())) {

                            MaterialDialog.Builder builder = new MaterialDialog.Builder(IntroActivity.this);

                            builder.title("업데이트")
                                    .content("새로운 버전이 출시되었습니다.\n업데이트 해주세요.")
                                    .positiveText("확인").negativeText("취소").cancelable(false).callback(new MaterialDialog.Callback() {
                                @Override
                                public void onPositive(MaterialDialog materialDialog) {

                                    materialDialog.dismiss();

                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));

                                    finish();
                                }

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {

                                    showNextScreen(10);

                                }
                            }).build().show();

                        } else {

                            showNextScreen(10);
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
}
