package kr.co.digitalanchor.studytime.devicepolicy;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.block.BlockActivity;

/**
 * Created by Thomas on 2015-06-24.
 */
public class AdminReceiver extends DeviceAdminReceiver {

    static final String TAG = "DemoDeviceAdminReceiver";

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

        killApplication("end");

        return super.onDisableRequested(context, intent);
    }

    private void killApplication(String packageName) {

        Context context = STApplication.applicationContext;

        Intent block = new Intent(context, BlockActivity.class);

        block.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(block);

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        manager.killBackgroundProcesses(packageName);

        Intent main = new Intent(Intent.ACTION_MAIN);

        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(context, 0, main, PendingIntent.FLAG_ONE_SHOT);

        final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, intent);

    }


}
