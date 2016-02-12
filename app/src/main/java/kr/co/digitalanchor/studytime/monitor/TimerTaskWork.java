package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.app.AppManageService;
import kr.co.digitalanchor.studytime.block.BlockActivity;
import kr.co.digitalanchor.studytime.database.AdultDBHelper;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.utils.AndroidUtils;
import kr.co.digitalanchor.utils.MD5;
import kr.co.digitalanchor.utils.MD5ForAdultURL;

/**
 * Created by Thomas on 2015-06-24.
 */
public class TimerTaskWork extends TimerTask {

    private Context mContext;

    private ActivityManager mActivityManager;

    private DBHelper mHelper;


    /** first app user */
    public static final int AID_APP = 10000;

    /** offset for uid ranges for each user */
    public static final int AID_USER = 100000;

    public TimerTaskWork(Context context) {

        mContext = context;

        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        mHelper = new DBHelper(mContext);

    }

    @Override
    public void run() {

        String currentPackage = null;

        // monitor
        // SWJ SM-N920S에서 확인
        // SWJ LOLIPOP_MR1 =  android 5.1
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {


            List localList = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(10);

            for (int i = 0; i < localList.size(); i++) {

                ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo) localList.get(i);

                if (info.numRunning > 0) {

                    currentPackage = info.topActivity.getPackageName();

                    Logger.d("SWJ true package : " + currentPackage);
                    break;
                }
            }
        }else{

            currentPackage = checkRunningPackage();

            Logger.d("SWJ currentPackage 1 : " + currentPackage);

            if(currentPackage == null || currentPackage.isEmpty() || currentPackage.equalsIgnoreCase("")) {
            //if(true){
                currentPackage = getForegroundApp2();

                Logger.d("SWJ currentPackage 6 : " + currentPackage);

                if(currentPackage != null){
                    if(currentPackage.contains("com.google.android.play.games")){
                        currentPackage = "com.google.android.play.games";
                    } else if (currentPackage.equalsIgnoreCase("com.google.android.gms.ui")){
                        currentPackage = "com.google.android.gms";
                    }
                }


                //List<PackageModel> models = mHelper.getPackageList();
//            PackageModel model = mHelper.getPackage(currentPackage);


                if (!haveToBlock(currentPackage)
                        || currentPackage.contains("ahnmobilesecurity")
                        //|| currentPackage.contains("com.google.android.gms")
                        || currentPackage.contains("googlequicksearchbox")
                        //|| currentPackage.contains("com.google.android.apps.plus")
                        //|| currentPackage.contains("com.google.android.gm")
                        || currentPackage.contains("com.ahnlab")
                        || currentPackage.contains("com.estsoft.alyac")
                        || currentPackage.contains("naver.lineantivirus")
                    //|| currentPackage.contains("com.google.android.talk")
                        || currentPackage.contains("com.skt.skaf.OA00199800")
                        || currentPackage.contains("com.android.calendar")
                        //|| currentPackage.contains("com.sec.android")
                    //|| currentPackage.contains("com.google.android.apps.magazines")
                    || currentPackage.contains("com.sec.android.gallery3d")
                    ) {
                        currentPackage = "android";
                    }

                    Logger.d("SWJ currentPackage result : " + currentPackage);
                }
            }
        //{
            //currentPackage = checkRunningPackage();
            /*currentPackage = checkRunningPackageTest1();

            Logger.d("SWJ currentPackage1 : " + currentPackage);


            currentPackage = checkRunningPackageTest2();
            Logger.d("SWJ currentPackage2 : " + currentPackage);


            currentPackage = getForegroundApp();
            Logger.d("SWJ currentPackage 3 : " + currentPackage);
            */

            //currentPackage = printForegroundTask();
            //Logger.d("SWJ currentPackage 4 : " + currentPackage);

            //currentPackage = printForgroundTask2();
            //Logger.d("SWJ currentPackage 5 : " + currentPackage);



//            List localList = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(10);
//
//            for (int i = 0; i < localList.size(); i++) {
//
//                ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo) localList.get(i);
//
//                if (info.numRunning > 0) {
//
//                    info.topActivity.getPackageName();
//
//                    Logger.d("SWJ true currentPackage  : " + info.topActivity.getPackageName());
//
//                    break;
//                }
//            }
//
//        } else {
//
//
//        }

//        if (!currentPackage.equals("kr.co.digitalanchor.studytime"))
//            Logger.d("pk [" + currentPackage + "]  version = " + Build.VERSION.SDK_INT);


        if (mHelper.getOnOff() == 1) {


            Logger.d("swj pk [" + currentPackage + "]  version = " + Build.VERSION.SDK_INT);

            // kill

            if (currentPackage == null
                    || TextUtils.isEmpty(currentPackage)
                    || isLauncher(currentPackage)
//                    || isDialer(currentPackage)
                    || currentPackage.contains(".mms")
                    || currentPackage.contains(".contacts")
                    || currentPackage.contains("com.android.phone")
                    //SWJ 2016-01-08 세팅도 예외 앱은 아님
                    //|| currentPackage.contains("com.android.settings")


                    || currentPackage.contains("com.android.dialer")
                    || currentPackage.equals("android")
                    || currentPackage.equals("com.android.systemui")
                    //SWJ 2016-01-08 세팅도 예외 앱은 아님
                    //|| currentPackage.equals("com.lge.settings.easy")
                    || currentPackage.equals("com.lge.bluetoothsetting")
                    || currentPackage.equals("com.skt.prod.phone")
                    || currentPackage.equals("com.sec.imsphone")
                    || currentPackage.equals("com.sec.ims")
                    || currentPackage.equals("com.sec.phone")
                    || currentPackage.equals("com.android.incallui")) {

                return;

                // not work
            } else if (STApplication.getBoolean(StaticValues.SHOW_ADMIN, false)
                    && currentPackage.contains("com.android.packageinstaller")) {

                return;

                // not work

            } else if (currentPackage.compareTo("kr.co.digitalanchor.studytime") == 0) {

                Logger.d("pk [" + currentPackage + "]  version = " + Build.VERSION.SDK_INT);

                return;

            } else if (!mHelper.isExcepted(currentPackage)) {

                //killApplication(currentPackage);

                Logger.d("swj kill app : " + currentPackage);
                blockApplication(currentPackage);

                return;
            }
        }

        //web 차단
        if (STApplication.getBoolean(StaticValues.IS_SITE_BLOCK, true)
                && !TextUtils.isEmpty(currentPackage)
                && (currentPackage.equals("com.android.browser")
                || currentPackage.equals("com.google.android.browser") // nexus
                || currentPackage.equals("com.android.chrome")
                || currentPackage.equals("com.sec.android.app.sbrowser"))) {

            String url = getRecentUrl(currentPackage);

            if (TextUtils.isEmpty(url) || url.equals(StaticValues.BLOCK_PAGE_URL)) {

                return;
            }

            Logger.d("url = " + url);

            String[] data = extractUrlParts(url);

            if (data == null || data.length < 1) {

                return;
            }

            AdultDBHelper helper = new AdultDBHelper(mContext);

            if (helper.isAdultURL(new MD5ForAdultURL().toDigest(data[0]), data[1])) {

                blockWebPage(currentPackage);
            }
        }
    }

    private String checkRunningPackageTest2(){

        List<AndroidAppProcess>  processes = ProcessManager.getRunningForegroundApps(mContext);

        String packageName = null;
        boolean isFirst = true;
        for (AndroidAppProcess process: processes) {



            if(process.foreground && isFirst){
                try {
                    if(process.status().getValue("State").equalsIgnoreCase("R")){
                        isFirst = false;
                        packageName = process.getPackageName();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return packageName;
    }

    private String checkRunningPackageTest1(){

        List<AndroidProcess>  processes = ProcessManager.getRunningProcesses();

        for (AndroidProcess process:processes) {

            try {

                if(process.stat().state()=='R'){
                    return process.name;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
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

                    //SWJ 하위버전에서 하면 NullPoint발생
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

    private void blockApplication(String packageName) {

        STApplication.applicationHandler.post(new Runnable() {
            @Override
            public void run() {

                LayoutInflater inflater = LayoutInflater.from(mContext);
                View layout = inflater.inflate(R.layout.activity_block, null);

                if (STApplication.toast == null)
                    STApplication.toast = new Toast(mContext);

                STApplication.toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                STApplication.toast.setDuration(Toast.LENGTH_SHORT);
                STApplication.toast.setMargin(0.0f, 0.0f);
                STApplication.toast.setView(layout);
                STApplication.toast.show();

            }
        });

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//                | Intent.FLAG_ACTIVITY_FORWARD_RESULT
//                | Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
//                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);

        intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);

        mContext.startActivity(intent);

        intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Logger.d("BlockApplication : " + intent.getComponent());

        mContext.startActivity(intent);
    }

    private void killApplication(String packageName) {

        //SWJ
        Intent home = new Intent();
        home.setAction(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
//        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//                | Intent.FLAG_ACTIVITY_FORWARD_RESULT
//                | Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
//                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Logger.d("KillApplication home " + home.getComponent() + " package " + home.getPackage());
        mContext.startActivity(home);

        Intent block = new Intent(mContext, BlockActivity.class);

        block.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(block);

        mActivityManager.killBackgroundProcesses(packageName);

        Intent main = new Intent(Intent.ACTION_MAIN);

        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Logger.d("KillApplication main " + main.getComponent() + " package " + main.getPackage());

        //SWJ 일회용 pendingIntent
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, main, PendingIntent.FLAG_ONE_SHOT);

        final AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);
    }

    private String getRecentUrl(String currentPackage) {

        if (TextUtils.isEmpty(currentPackage)) {

            return null;
        }

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

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(StaticValues.BLOCK_PAGE_URL));


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

        Uri localUri = null;

        switch (packageName) {

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


    private String[] extractUrlParts(String url) {

        String[] data = null;

        Pattern urlPattern = STApplication.getUrlPattern();

        Matcher mc = urlPattern.matcher(url);

        if (mc.matches()) {

            StringBuffer buffer = new StringBuffer();

            buffer.append(mc.group(1) + "://");

            if (mc.group(2).startsWith("wwww.")) {

                buffer.append(mc.group(2).replaceFirst("wwww.", ""));

            } else {

                buffer.append(mc.group(2));
            }

            if (TextUtils.isEmpty(mc.group(3))) {

                buffer.append(":80");

            } else {

                buffer.append(mc.group(3));
            }

            data = new String[]{buffer.toString(), mc.group(5)};
        }

        return data;
    }

    public static String getForegroundApp() {
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = Integer.MAX_VALUE;
        String foregroundProcess = null;

        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }

            int pid;
            try {
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                String cgroup = read(String.format("/proc/%d/cgroup", pid));

                String[] lines = cgroup.split("\n");

                if (lines.length != 2) {
                    continue;
                }

                String cpuSubsystem = lines[0];
                String cpuaccctSubsystem = lines[1];

                if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                    // not an application process
                    continue;
                }

                if (cpuSubsystem.endsWith("bg_non_interactive")) {
                    // background policy
                    continue;
                }

                String cmdline = read(String.format("/proc/%d/cmdline", pid));

                if (cmdline.contains("com.android.systemui")) {
                    continue;
                }

                int uid = Integer.parseInt(
                        cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                if (uid >= 1000 && uid <= 1038) {
                    // system process
                    continue;
                }

                int appId = uid - AID_APP;
                int userId = 0;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.
                while (appId > AID_USER) {
                    appId -= AID_USER;
                    userId++;
                }

                if (appId < 0) {
                    continue;
                }

                // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
                // String uidName = String.format("u%d_a%d", userId, appId);

                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }

                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
                if (oomscore < lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return foregroundProcess;
    }

    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString();
    }

    private String printForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager)mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e("SWJ", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    private String printForgroundTask2(){
        Hashtable<String, List<ActivityManager.RunningServiceInfo>> hashtable = new Hashtable<String, List<ActivityManager.RunningServiceInfo>>();
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {

            String pkgName = rsi.service.getPackageName();

            return pkgName;
        }
        return null;
        /*
            if (hashtable.get(pkgName) == null) {
                List<ActivityManager.RunningServiceInfo> list = new ArrayList<ActivityManager.RunningServiceInfo>();
                list.add(rsi);
                hashtable.put(pkgName, list);
            } else {
                hashtable.get(pkgName).add(rsi);
            }
        }

        int i = 0;
        int size =  hashtable.size();
        for (Iterator it = hashtable.keySet().iterator(); it.hasNext(); i++) {
            String key = (String) it.next();
            List<ActivityManager.RunningServiceInfo> value = hashtable.get(key);
            ProcessItem item = new ProcessItem(getContext(), value.get(0).pid, key, totalCpu, totalRam);
            if (!whiteList.contains(item.pkgName)) {
                if (!killList.contains(item.pkgName)) {
                    killList.add(item.pkgName);
                    ramTotal += item.ram;

                    if (getListener() != null) {
                        Progress progress = new Progress(this);
                        progress.setArg1(i);
                        progress.setArg2(size);
                        progress.setMsg(item.appName);
                        progress.setObj(item);
                        getListener().onExamining(progress);
                    }
                }
            }
        }
        hashtable.clear();
        hashtable = null;
        */
    }


    public static String getForegroundApp2()
    {
        int lowestOomScore = Integer.MAX_VALUE;
        String foregroundProcess = null;

        List<String> lsFiles = runCommand("ls -l /proc");
        for (String lsFile : lsFiles)
        {
            if (!lsFile.startsWith("d"))
                continue;

            String[] fields = lsFile.split("\\s+");

            if (fields.length != 6)
                continue;

            int pid;
            try
            {
                pid = Integer.parseInt(fields[5]);
            }
            catch (NumberFormatException e) {continue;}

            String user = fields[1];

            if (!user.contains("app") && !(user.startsWith("u") && user.contains("_a")))
                continue;

            try
            {

                String cmdline = read2("/proc/"+pid+"/cmdline");

                if (cmdline == null || cmdline.contains("/") || cmdline.contains(" ") ||
                        !cmdline.contains(".") || cmdline.contains("com.android.systemui"))
                    continue;


                File oomScoreAdj = new File("/proc/"+pid+"/oom_score_adj");
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read2(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }

                /*Logger.i("cmdLine :" + cmdline);

                String exe = read2("/proc/"+pid+"/exe");
                Logger.i("exe name : " + exe);

                String exec2 = read2("/proc/" + pid + "/exe");

                String loginuid = read2("/proc/"+pid+"/loginuid");

                Logger.i("loginuid " + loginuid);

                Logger.i("exec name : " + exec2);

                String status = read2("/proc/"+pid+"/status");
                Logger.i("status : " + status);*/


                int oomscore = Integer.parseInt(read2("/proc/"+pid+"/oom_score"));
                if (oomscore < lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return foregroundProcess != null ? foregroundProcess.split(":")[0] : null;
    }

    private static List<String> runCommand(String cmd)
    {
        ArrayList<String> rv = new ArrayList<String>();
        try
        {
            Process p = null;
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            for (String line = reader.readLine(); line != null; line = reader.readLine())
                rv.add(line);

            reader.close();

            return rv;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static byte []buffer;

    private static String read2(String path) throws IOException
    {
        File file = new File(path);
        try
        {
            FileInputStream fis = new FileInputStream(file);

            if (buffer ==  null)
                buffer = new byte[1000];

            int readLen, index = 0;
            while (index < buffer.length && (readLen = fis.read(buffer,index,buffer.length - index))>0)
                index += readLen;

            fis.close();

            return new String(buffer,0,index).trim();
        }
        catch(Exception e)
        {}

        return null;
    }

    public boolean haveToBlock(String packageName) {

        HashMap<String, PackageModel> listInDB = mHelper.getPackageStateList();

        if(listInDB.containsKey(packageName) && (listInDB.get(packageName)).getIsExceptionApp() == 0){
            return true;
        }

        return false;
    }

}
