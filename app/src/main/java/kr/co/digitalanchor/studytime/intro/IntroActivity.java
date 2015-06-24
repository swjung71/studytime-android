package kr.co.digitalanchor.studytime.intro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

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
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-06-10.
 */
public class IntroActivity extends BaseActivity implements View.OnClickListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // TODO Application class에서 호출 하면 안됨?
        IgawCommon.startApplication(this);

        setContentView(R.layout.activity_intro);

        initialize();

        if (checkPlayServices()) {

            registerInBackground();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initialize() {

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

                // showOfferWall();

                break;

            default:

                Toast.makeText(getApplicationContext(), "selected default in onClick()", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void selectIntro() {

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        String id = STApplication.getString("ParentID");

        String isParent = STApplication.getString("isParent");

        if (TextUtils.isEmpty(account.getID())) {

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

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), SplashParentActivity.class);

        startActivity(intent);

        finish();
    }

    private void showChildIntro() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), SplashChildActivity.class);

        startActivity(intent);

        finish();
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Logger.e("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    /// Validation
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

                    /// Get Registration ID
                    String id = gcm.register(StaticValues.GCM_SENDER_ID);

                    STApplication.putString(StaticValues.GCM_REG_ID, id);

                    Logger.d("registration id " + id);

                    return "Succeed";

                } catch (IOException ex) {

                    Toast.makeText(getApplicationContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    return "Failed";
                }
            }

            @Override
            protected void onPostExecute(String strResult) {
                super.onPostExecute(strResult);

                if ("Succeed".equals(strResult)) {

                    selectIntro();

                } else {


                }
            }
        }.execute(null, null, null);

    }
}
