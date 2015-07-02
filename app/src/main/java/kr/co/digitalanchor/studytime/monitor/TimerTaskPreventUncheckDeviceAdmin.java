package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.content.Context;

import java.util.TimerTask;

/**
 * Created by Thomas on 2015-07-01.
 */
public class TimerTaskPreventUncheckDeviceAdmin extends TimerTask {

    Context context;

    ActivityManager manager;

    public TimerTaskPreventUncheckDeviceAdmin(Context context) {

        this.context = context;

        manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    }

    @Override
    public void run() {

        doCheck();

    }

    private void doCheck() {

        String topActivity = manager.getRunningTasks(1).get(0).topActivity.getClassName();

        if (topActivity.equals("com.android.settings.DeviceAdminAdd")) {


        }
    }
}
