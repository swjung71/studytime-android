package kr.co.digitalanchor.studytime.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.control.ListChildActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.login.LoginActivity;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-06-15.
 */
public class SplashParentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showNextScreen();
    }

    private void showNextScreen() {

        DBHelper helper = new DBHelper(getApplicationContext());

        Account account = helper.getAccountInfo();

        if (TextUtils.isEmpty(account.getID())) {

            // login 안했으면, 로그인

            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), LoginActivity.class);

                    startActivity(intent);

                    finish();
                }
            }, 1000);

        } else {

            // login 했으면, 메인

            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), ListChildActivity.class);

                    startActivity(intent);

                    finish();
                }
            }, 1000);
        }
    }
}
