package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.List;
import java.util.TimerTask;

import kr.co.digitalanchor.studytime.block.BlockActivity;

/**
 * Created by Thomas on 2015-06-24.
 */
public class TimerTaskWork extends TimerTask {

    private Context mContext;

    private ActivityManager mActivityManager;


    public TimerTaskWork(Context context) {

        mContext = context;

        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void run() {

        String currentPackage = null;

        // monitor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            currentPackage = checkRunningPackage();

        } else {

            List localList = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(10);

            for (int i = 0; i < localList.size(); i++) {

                ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo) localList.get(i);

                if (info.numRunning > 0) {

                    currentPackage = info.topActivity.getPackageName();

                    break;
                }
            }
        }

        Logger.d("get " + currentPackage);

        // kill
        if (TextUtils.isEmpty(currentPackage)) {

            // not work

        } else if (currentPackage.compareTo("kr.co.digitalanchor.studytime") != 0) {

            Logger.d("kill " + currentPackage);

            killApplication(currentPackage);
        }
    }

    private String checkRunningPackage() {

        final int START_TASK_TO_FRONT = 2;

        ActivityManager.RunningAppProcessInfo currentInfo = null;

        Field field = null;

        try {

            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");

        } catch (NoSuchFieldException e) {

            Logger.e(e.getMessage());
        }

        List<ActivityManager.RunningAppProcessInfo> appList = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo app : appList) {

            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

                Integer state = null;

                try {

                    state = field.getInt(app);

                } catch (Exception ex) {

                    ex.printStackTrace();
                }

                if (state != null && state == START_TASK_TO_FRONT) {

                    currentInfo = app;
                    break;
                }
            }
        }

        if (currentInfo != null) // avoid null err b/c of some unknow reason
            return currentInfo.pkgList[0];
        else {
            return null;
        }
    }

    private void killApplication(String packageName) {

        Intent block = new Intent(mContext, BlockActivity.class);

        block.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(block);

        mActivityManager.killBackgroundProcesses(packageName);

        Intent main = new Intent(Intent.ACTION_MAIN);

        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(mContext, 0, main, PendingIntent.FLAG_ONE_SHOT);

        final AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, intent);

    }
}
