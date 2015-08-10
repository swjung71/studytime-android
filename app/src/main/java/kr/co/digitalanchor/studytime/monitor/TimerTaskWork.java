package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.block.BlockActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;

/**
 * Created by Thomas on 2015-06-24.
 */
public class TimerTaskWork extends TimerTask {

    private Context mContext;

    private ActivityManager mActivityManager;

    private DBHelper mHelper;


    public TimerTaskWork(Context context) {

        mContext = context;

        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        mHelper = new DBHelper(mContext);
    }

    @Override
    public void run() {

        if (mHelper.getOnOff() != 1) {

            return;
        }

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

//        Logger.d("pk [" + currentPackage + "]  version = " + Build.VERSION.SDK_INT);

        // kill
        if (TextUtils.isEmpty(currentPackage)
                || isLauncher(currentPackage)
                || currentPackage.contains(".mms")
                || currentPackage.contains(".contacts")
                || currentPackage.contains("com.android.phone")
                || currentPackage.contains("com.android.settings")
                || currentPackage.contains("com.android.dialer")
                || currentPackage.equals("android")
                || currentPackage.equals("com.android.systemui")
                || currentPackage.equals("com.lge.settings.easy")
                || currentPackage.equals("com.lge.bluetoothsetting")) {

            // not work
        } else if (STApplication.getBoolean(StaticValues.SHOW_ADMIN, false)
                && currentPackage.contains("com.android.packageinstaller")) {

            // not work

        } else if (currentPackage.compareTo("kr.co.digitalanchor.studytime") == 0) {

//            Logger.d("pk [" + currentPackage + "]  version = " + Build.VERSION.SDK_INT);

        } else if (!mHelper.isExcepted(currentPackage)) {

            killApplication(currentPackage);

            return;
        }
/*
        if (currentPackage.equals("com.android.browser")
                || currentPackage.equals("com.google.android.browser") // nexus
                || currentPackage.equals("com.android.chrome")
                || currentPackage.equals("com.sec.android.app.sbrowser")) {

            String url = getRecentUrl(currentPackage);

            if (TextUtils.isEmpty(url)) {

                return;
            }

            // TODO url check by db
           if (mHelper.isAdultURL(url, directory)) {

                blockWebPage(currentPackage, url);
            }
        }
*/
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

        if (currentInfo != null) {// avoid null err b/c of some unknow reason

            return currentInfo.pkgList[0];

        } else {

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

        alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);

    }

    private String getRecentUrl(String currentPackage) {

        String sortOrder = String.format("%s DESC limit 1",
                new Object[]{"date"});
        ContentResolver contentResolver = mContext.getContentResolver();

        Uri localUri = null;

        switch (currentPackage) {

            case "com.sec.android.app.sbrowser":

                localUri = Uri
                        .parse("content://com.sec.android.app.sbrowser.browser/history");

                break;

            case "com.android.chrome":

                localUri = Uri
                        .parse("content://com.android.chrome.browser/history");

                break;

            default:

                localUri = Browser.BOOKMARKS_URI; // not works on all phone

                break;
        }

        String[] historyArray = Browser.HISTORY_PROJECTION;
        String[] time = new String[1];
        time[0] = String.valueOf(System.currentTimeMillis());

        Cursor cursor = contentResolver.query(localUri, historyArray,
                "date < ?", time, sortOrder);

        String url = null;

        if (cursor != null) {
            if ((cursor.moveToFirst()) && (cursor.getCount() > 0)) {

                url = cursor.getString(1);// URL
            }

            cursor.close();

        }

        return url;

    }

    private void blockWebPage(String packageName) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(StaticValues.BLOCK_PAGE_URL));


        switch (packageName) {

            case "com.sec.android.app.sbrowser":

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName(packageName,
                        "com.sec.android.app.sbrowser.SBrowserMainActivity");

                break;

            case "com.android.browser":

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName(packageName,
                        "com.android.browser.BrowserActivity");

                break;

            case "com.android.chrome":

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName(packageName,
                        "com.google.android.apps.chrome.Main");

                break;
        }

        intent.putExtra(Browser.EXTRA_APPLICATION_ID, packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String[] time = new String[1];
        time[0] = String.valueOf(System.currentTimeMillis());

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri localUri = Browser.BOOKMARKS_URI; // not works on all phone

        try {
            String args[] = new String[]{"" + time};
            contentResolver.delete(localUri,
                    "date < ? ORDER BY date DESC LIMIT 1", args);
        } catch (Exception ex) {
            // log
        }

        // open our block site
        mContext.startActivity(intent);

    }

    private boolean isLauncher(String packageName) {

        ArrayList<String> names = getLauncherNames();

        for (int i = 0; ; i++) {

            if (i >= names.size()) {

                return false;
            }

            if (packageName.equalsIgnoreCase((String) names.get(i))) {

                return true;
            }
        }
    }

    private ArrayList<String> getLauncherNames() {

        ArrayList<String> names = new ArrayList<>();

        PackageManager manager = mContext.getPackageManager();

        Intent intent = new Intent("android.intent.action.MAIN", null);

        intent.addCategory("android.intent.category.HOME");

        Iterator iterator = manager.queryIntentActivities(intent, Intent.FLAG_ACTIVITY_NO_ANIMATION).iterator();

        while (iterator.hasNext()) {

            names.add(((ResolveInfo) iterator.next()).activityInfo.packageName);
        }

        return names;
    }

}
