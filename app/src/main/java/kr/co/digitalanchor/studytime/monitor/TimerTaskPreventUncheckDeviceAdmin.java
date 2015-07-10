package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.List;
import java.util.SortedMap;
import java.util.TimerTask;
import java.util.TreeMap;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.block.BlockPasswordActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-07-01.
 */
public class TimerTaskPreventUncheckDeviceAdmin extends TimerTask {

    Context context;

    ActivityManager mActivityManager;

    DBHelper helper;

    public TimerTaskPreventUncheckDeviceAdmin(Context context) {

        this.context = context;

        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        helper = new DBHelper(context);

    }

    @Override
    public void run() {


        if (helper.isAllow() != 1) {

            return;
        }

        String topActivity = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            topActivity = checkRunningSettings();

            if (TextUtils.isEmpty(topActivity)) {

                topActivity = null;

            } else if (!topActivity.equals("com.android.settings.DeviceAdminAdd")) {

                topActivity = null;
            }

        } else {

            topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();

            if (!topActivity.equals("com.android.settings.DeviceAdminAdd")) {

                topActivity = null;
            }
        }

        if (!TextUtils.isEmpty(topActivity)) {

            Logger.d(topActivity);

            Intent intent = new Intent(context, BlockPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    private String checkRunningSettings() {

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

        if (currentInfo != null) {// avoid null err b/c of some unknow reason

            return currentInfo.pkgList[0].contains("SSettings") ? currentInfo.pkgList[0] : null;

        } else {

            return null;

        }
    }



}
