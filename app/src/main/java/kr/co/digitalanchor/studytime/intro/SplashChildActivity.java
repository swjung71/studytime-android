package kr.co.digitalanchor.studytime.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.chat.ChildChatActivity;
import kr.co.digitalanchor.studytime.login.LoginChildActivity;

/**
 * Created by Thomas on 2015-06-15.
 */
public class SplashChildActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showNextScreen();
    }

    private void showNextScreen() {

        SharedPreferences pref = STApplication.getPreference();

        if (TextUtils.isEmpty(pref.getString("ParentID", null))) {

            // login 안했으면, 로그인

            getHandler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), LoginChildActivity.class);

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
                    intent.setClass(getApplicationContext(), ChildChatActivity.class);

                    startActivity(intent);

                    finish();

                }
            }, 1000);
        }
    }


}
