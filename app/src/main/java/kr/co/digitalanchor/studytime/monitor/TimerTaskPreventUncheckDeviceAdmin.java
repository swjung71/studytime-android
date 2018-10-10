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

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.orhanobut.logger.Logger;

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

    private static boolean isAccessbilityBlockShow = false;

    public TimerTaskPreventUncheckDeviceAdmin(Context context) {

        this.context = context;

        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //helper = new DBHelper(context);
        helper = DBHelper.getInstance(context);

        // additional
        object = new Object();

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        Logger.d("DeviceAdmin is started");

    }

    @Override
    public void run() {

        //Logger.d("DeviceAdmin");
        int allow = helper.isAllow();
        if (allow != 1) {
            //Logger.d("swj is isAllow ==1 ");
            return;
        }

        //device admin check하고 차단화면 뛰우는 곳
        //SWJ SM-N920S에서 if 문을 삭제하고 테스트하기 (성공하면 A.class에서 block하는 것 삭제
        if (Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT) {

            //isAllow() :비밀번호를 입력하여 삭제할 수 있도록 설정되었는가 ?, isAllow 1이면 허락이 아님, 비밀번호를 넣으면 0
            //device admin이 설정되어 있는지 아닌지 확인하는 곳, 설정이 안되어 있을 때 뛰우는 것
            //isblock() admin 화면인가?
            //SHOW_ADMIN 의 의미는 onDisable되면 true가 됨, 켜져 있으면 false
            if (allow == 1 && STApplication.getBoolean(StaticValues.SHOW_ADMIN, false)
                    && isblock()) {

                showBlockView();
                Logger.d("SWJ device admin in TimerTaskerDeviceAdmin ");
                return;

            } else {
                Logger.d("is hideBlockView in TimerTaskerDeviceAdmin");
                hideBlockView();

            }

        }

        //Accessbility 체크하고 차단화면 뛰우는 곳
        //false && false
        //is not setting &&
        if (!STApplication.isAccessibilityEnabled() && !isSettings()) {

            Logger.d("SWJ Disabled, so Show in TimerTaskDeviceAdmin");

            showBlockSettingView();

        } else {

            //Logger.d("Enabled, so not show in TimerTaskDeviceAdmin");

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


    //Admin화면인지 아닌지 확인하는 곳
    private boolean isblock() {

        String packageName = null;

        boolean isAdmin = false;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            //SWJ
            //packageName = checkRunningPackageTest2();
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

        isAccessbilityBlockShow = true;
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

            //SWJ 테스트 해야함
            //packageName = checkRunningPackageTest2();
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

        //SWJ 2016-01-08
        if(context.getPackageName().equals(packageName)){

            //Logger.d("DeviceAdmin package name is same");
            //Logger.d("context getPackageName : " + context.getPackageName());
            //Logger.d("packageName : " + packageName);
            return true;

        }else {

            //Logger.d("DeviceAdmin package name is not same");
            //Logger.d("context getPackageName : " + context.getPackageName());
            //Logger.d("packageName : " + packageName);

            //처음이면 무조건 뛰움
            if(!STApplication.isFirstTimeAccessbilityTurnOn){

                //한번도 block화면 안보여줬으면 보여주기
                if(!isAccessbilityBlockShow) {
                    return false;
                //처음이면서 한번 block을 보여줬으면 return, 그 이후 한번이라도 setting이 되었으면 아래의 것을 따름
                }else{
                    if(System.currentTimeMillis() - STApplication.getLong(StaticValues.SHOW_SETTING, 0L) > 15000 ){
                        return false;
                    }else {
                        return true;
                    }
                }
            }

            //한번이라도 setting되었다가 변경하는 경우
            if(System.currentTimeMillis() - STApplication.getLong(StaticValues.SHOW_SETTING, 0L) > 3000 ){
                    Logger.d("over 3000");
                    return false;
            }

            Logger.d("under 3000");
            return true;

        }

//        if ((System.currentTimeMillis() - STApplication.getLong(StaticValues.SHOW_SETTING, 0L) < 15000)
//                || context.getPackageName().equals(packageName)/* || packageName.equals("android"))*/) {
//
//            return true;

//        } else {

//            return false;
//        }

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

    private String checkRunningPackageTest2(){

        List<AndroidAppProcess>  processes = ProcessManager.getRunningForegroundApps(context);

        String packageName = null;
        boolean isFirst = true;
        for (AndroidAppProcess process: processes) {

            Logger.d("SWJ process " + process.getPackageName() + " foreground " + process.foreground);

            if(process.foreground && isFirst){
                isFirst = false;
                packageName = process.getPackageName();
            }

        }
        return packageName;
    }

    private String checkRunningPackageTest1(){

        final int START_TASK_TO_FRONT = 2;

        ActivityManager.RunningAppProcessInfo currentInfo = null;

        Field field = null;

        try {

            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");

        } catch (NoSuchFieldException e) {

            Logger.e(e.getMessage());
        }

        List<ActivityManager.RunningAppProcessInfo>  infos = ProcessManager.getRunningAppProcessInfo(context);

        for (ActivityManager.RunningAppProcessInfo app : infos) {

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
