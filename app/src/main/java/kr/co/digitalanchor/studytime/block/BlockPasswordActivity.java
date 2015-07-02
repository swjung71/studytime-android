package kr.co.digitalanchor.studytime.block;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;

/**
 * Created by Thomas on 2015-06-11.
 */
public class BlockPasswordActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CONFIRM = 50001;

    private EditText mEditPassword;

    private Button mButtonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {

        setContentView(R.layout.activity_block_password);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mButtonConfirm = (Button) findViewById(R.id.buttonConfirm);
        mButtonConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonConfirm:

                sendEmptyMessage(REQUEST_CONFIRM);

                break;

            default:

                break;

        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_CONFIRM:

                    requestConfirm();

                break;

            default:

                break;
        }
    }

    private void requestConfirm() {

        DBHelper helper = new DBHelper(this);

        helper.updateAllow(0);

        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        finish();
    }

    @Override
    public void onBackPressed() {

        killDeviceAdmin();

    }

    private void killDeviceAdmin() {

        String packageName = null;

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List localList = manager.getRunningTasks(3);

        for (int i = 0; i < localList.size(); i++) {

            ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo) localList.get(i);

            if (info.topActivity.getPackageName().contains("setting")
                    || info.topActivity.getPackageName().contains("Setting") ) {

                packageName = info.topActivity.getPackageName();

                break;
            }
        }

        if (!TextUtils.isEmpty(packageName)) {

            manager.killBackgroundProcesses(packageName);
        }

        Intent main = new Intent(Intent.ACTION_MAIN);

        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(),
                0, main, PendingIntent.FLAG_ONE_SHOT);

        final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 300, intent);
    }
}
