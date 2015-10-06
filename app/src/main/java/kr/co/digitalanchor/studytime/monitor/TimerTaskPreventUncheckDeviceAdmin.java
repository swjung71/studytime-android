package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.block.BlockPasswordLayout;
import kr.co.digitalanchor.studytime.block.BlockSettingLayout;
import kr.co.digitalanchor.studytime.database.DBHelper;

/**
 * Created by Thomas on 2015-07-01.
 */
public class TimerTaskPreventUncheckDeviceAdmin extends TimerTask {

    Context context;

    ActivityManager mActivityManager;

    WindowManager windowManager;

    WindowManager.LayoutParams layoutParams;

    DBHelper helper;

    Object object;

    View blockView = null;

    View settingView = null;

    public TimerTaskPreventUncheckDeviceAdmin(Context context) {

        this.context = context;

        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        helper = new DBHelper(context);

        // additional
        object = new Object();

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


    }

    @Override
    public void run() {

        if (helper.isAllow() != 1)
            return;

        if (helper.isAllow() == 1 && STApplication.getBoolean(StaticValues.SHOW_ADMIN, false)
                && isblock()) {

            showBlockView();

            return;

        } else {

            hideBlockView();

        }

        if (!STApplication.isAccessibilityEnabled() && !isSettings()) {

//            Logger.d("Disabled");

            showBlockSettingView();

        } else {

//            Logger.d("Enabled");

            hideBlockSettingView();
        }
    }

    private void hideBlockView() {

        synchronized (object) {

            STApplication.applicationHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (blockView == null) {

                        return;
                    }

                    windowManager.removeView(blockView);

                    blockView = null;
                }
            });
        }
    }

    private void showBlockView() {

        synchronized (object) {

            STApplication.applicationHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (blockView != null) {

                        return;
                    }

                    blockView = new BlockPasswordLayout(context);

                    windowManager.addView(blockView, layoutParams);

                }
            });
        }
    }


    private boolean isblock() {

        String packageName = null;

        boolean isAdmin = false;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            packageName = mActivityManager.getRunningAppProcesses().get(0).processName;

        } else {

            ComponentName info = mActivityManager.getRunningTasks(1).get(0).topActivity;

            packageName = info.getPackageName();

//            Logger.d(info.getClassName());

            isAdmin = info.getClassName().equalsIgnoreCase("com.android.settings.DeviceAdminAdd");

        }

        return !(context.getPackageName().equals(packageName) || isAdmin);
    }

    private void showBlockSettingView() {

        synchronized (object) {

            STApplication.applicationHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (settingView != null) {

                        return;
                    }

                    settingView = new BlockSettingLayout(context);

                    windowManager.addView(settingView, layoutParams);
                }
            });
        }
    }

    private void hideBlockSettingView() {

        synchronized (object) {

            STApplication.applicationHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (settingView == null) {

                        return;
                    }

                    windowManager.removeView(settingView);

                    settingView = null;

                }
            });
        }
    }

    private boolean isSettings() {

        String packageName = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            packageName = checkRunningPackage();

        } else {

            List localList = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(10);

            for (int i = 0; i < localList.size(); i++) {

                ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo) localList.get(i);

                if (info.numRunning > 0) {

                    packageName = info.topActivity.getPackageName();

                    break;
                }
            }
        }

//        Logger.d(packageName);

        if ((System.currentTimeMillis() - STApplication.getLong(StaticValues.SHOW_SETTING, 0L) < 15000)
                || context.getPackageName().equals(packageName)/* || packageName.equals("android"))*/) {

            return true;

        } else {

            return false;
        }

//        ArrayList<String> names = getSettingsNames();
//
//        for (int i = 0; ; i++) {
//
//            if (i >= names.size()) {
//
//                return false;
//            }
//
////            Logger.d(names.get(i));
//
//            if (packageName.equalsIgnoreCase((String) names.get(i))) {
//
//                return true;
//            }
//        }
    }

    private ArrayList<String> getSettingsNames() {

        ArrayList<String> names = new ArrayList<>();

        PackageManager manager = context.getPackageManager();

        Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS", null);

        intent.addCategory("android.intent.category.DEFAULT");

        Iterator iterator = manager.queryIntentActivities(intent, Intent.FLAG_ACTIVITY_NO_ANIMATION).iterator();


        while (iterator.hasNext()) {

            names.add(((ResolveInfo) iterator.next()).activityInfo.packageName);
        }

//        Logger.d(names.size() + "");

        return names;
    }

    private String checkRunningPackage() {

        final int START_TASK_TO_FRONT = 2;

        ActivityManager.RunningAppProcessInfo currentInfo = null;

        Field field = null;

        try {

            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");

        } catch (Exception e) {

            return context.getPackageName();
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

            return currentInfo.pkgList[0];

        } else {

            return null;

        }
    }
}
