package kr.co.digitalanchor.studytime.monitor;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.app.AppManageService;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.use.Usages;
import kr.co.digitalanchor.studytime.use.UsagesFactory;
import kr.co.digitalanchor.studytime.view.LockView;
import kr.co.digitalanchor.utils.AppOpListenerFactory;
import kr.co.digitalanchor.utils.IntentFilteredData;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.Subscriptions;

import static kr.co.digitalanchor.utils.AndroidUtils.getCurrentTimeIncludeMs;
import static kr.co.digitalanchor.utils.Intents.ACTION_ALERT_ALARM;
import static kr.co.digitalanchor.utils.Intents.ACTION_ATTACH_ALARM;
import static kr.co.digitalanchor.utils.Intents.ACTION_DETACH_ALARM;
import static kr.co.digitalanchor.utils.Intents.ACTION_LOCAL_UNLOCK_FINISH;
import static kr.co.digitalanchor.utils.Intents.ACTION_MIDNIGHT;
import static kr.co.digitalanchor.utils.Intents.ACTION_USAGE_CHANGED;
import static kr.co.digitalanchor.utils.Intents.ACTION_USAGE_SWITCH;


/**
 * 정책에 의한 잠금 상태를 관리하는 서비스입니다.
 * 정책에 대한 모든 동작이 이 서비스에서 최종적으로 결정됩니다.
 */
public class LockService extends Service {

    @Nullable
    private LockView mLockView;
    @Nullable
    private BroadcastReceiver mAccessibilityUsageReceiver;
    @Nullable
    private AppOpsManager.OnOpChangedListener mOnOpChangedListener;
    @Nullable
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener;
    @Nullable
    private IntentFilteredData mIntentFilteredData;
    @Nullable
    private Usages mUsages;

    private LockServiceHandler mHandler = new LockServiceHandler(this);

    private Subscription mUsageSubscription = Subscriptions.unsubscribed();
    private Subscription mFindLockSubscription = Subscriptions.unsubscribed();
    private Subscription mChangeSubscription = Subscriptions.unsubscribed();

    private final Map<String, Subscription> mUnlockSubscriptions = new HashMap<>();
    private final static BehaviorSubject<String> mChangeSubject = BehaviorSubject.create();

    //private static DBHelper mDBHelper = new DBHelper(STApplication.applicationContext);
    private static DBHelper mDBHelper = DBHelper.getInstance(STApplication.applicationContext);

    View blockView = null;
    WindowManager mWindowManager;
    WindowManager.LayoutParams mLayoutParams;

    private static final int REBOOT_DELAY_TIMER = 2 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("LockService onCreate: ");
        mHandler.foreground();
        mLockView = new LockView(this.getApplicationContext());

        createUsages();

        //접근성 데이터 수신.
        mAccessibilityUsageReceiver = new LockServiceReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USAGE_SWITCH);
        filter.addAction(ACTION_USAGE_CHANGED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mAccessibilityUsageReceiver, filter);

        //UsageStats 활성화 여부 수신.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String op = AppOpsManager.OPSTR_GET_USAGE_STATS;
            /*mOnOpChangedListener = AppOpListenerFactory.add(op, new AppOpListenerFactory.AppOpChangedListener() {
                @Override
                public void onChanged() {
                    mHandler.recreateUsage();
                }
            });*/
            mOnOpChangedListener = AppOpListenerFactory.add(
                    op, () -> mHandler.recreateUsage()
            );
        }

        mHandler.findAbleLocks();
        unregisterRestartAlarm();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy: ");

        if (mLockView != null) {
            mLockView.detach();
            mLockView = null;
        }

        mIntentFilteredData = null;

        AppOpListenerFactory.remove(mOnOpChangedListener);
        mOnOpChangedListener = null;

        unregisterReceiver(mAccessibilityUsageReceiver);
        mAccessibilityUsageReceiver = null;

        mUsageSubscription.unsubscribe();
        mFindLockSubscription.unsubscribe();
        mChangeSubscription.unsubscribe();
        mUsages = null;
        mHandler.release();
        mHandler = null;

        registerRestartAlarm();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                Logger.d("onStartCommand: %s", action);
                switch (action) {
                    //잠금 시기별 이벤트 처리.
                    case ACTION_ALERT_ALARM: {
                        break;
                    }
                    case ACTION_ATTACH_ALARM:
                    case ACTION_DETACH_ALARM: {

                        if(action.equalsIgnoreCase(ACTION_ATTACH_ALARM)){
                            writeLog();
                        }
                        if (true) {
                            mHandler.findAbleLocks();
                            return START_STICKY;
                        }
                        break;
                    }

                    case ACTION_LOCAL_UNLOCK_FINISH: {
                        mHandler.findAbleLocks();
                        break;
                    }
                    case ACTION_MIDNIGHT: {
                        mHandler.findAbleLocks();
                        break;
                    }
                }
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver != null && filter != null) {
            try {
                return super.registerReceiver(receiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (receiver != null) {
            try {
                super.unregisterReceiver(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 설정된 정책을 찾아서 현재 시간에 따른 처리를 진행합니다.
     */
    public void findAbleLocks() {
        if (mLockView == null) return;
        Logger.d("findAbleLocks: ");

        mFindLockSubscription.unsubscribe();
        mHandler.foreground();

        onSubmitAbleLocks();
    }

    /**
     * 현재 시간에 할당된 정책에 따라 잠금을 할지 여부를 정하고 실행합니다.
     */
    private void onSubmitAbleLocks() {
        Logger.d("onSubmitAbleLocks: %s");

        if (mChangeSubscription.isUnsubscribed()) {
            Logger.d("mIntentFilteredData start");
            mIntentFilteredData = new IntentFilteredData();

            Logger.d("mIntentFilteredData end");

            mChangeSubscription = mChangeSubject.distinctUntilChanged()
                    .subscribe(this::onPackageChanged);

            Logger.d("subscrbie : mChangeSubscription");
        }
        if (mUsageSubscription.isUnsubscribed()) {
            onPackageChanged(mChangeSubject.getValue());

            mUsageSubscription = Observable
                    .interval(1, TimeUnit.SECONDS, Schedulers.computation())
                    /*.map(new Func1<Long, Object>() {

                        @Override
                        public Object call(Long aLong) {
                            mUsages.pkg();
                        }
                    })*/
                    .map(l -> mUsages.pkg())
                    .distinctUntilChanged()
                    /*.subscribe(new Action1<Object>() {

                        @Override
                        public void call(Object o) {
                            mChangeSubject.onNext();
                        }
                    })*/
                    .subscribe(mChangeSubject::onNext, error -> Logger.e("mUsageSubscription subscribe error"));
            Logger.d("subscrbie : " + "mChangeSubject");
        } else {
            Logger.d("not mUsageSubscription.isUnsubscribed(): ");

            Logger.i("mChangeSubject : " + mChangeSubject.getValues());
            onPackageChanged(mChangeSubject.getValue());
        }

        Logger.d("onLockStart(); start");
        onLockStart();
    }


    /**
     * 정책 잠금 처리가 시작되었습니다.
     */
    private void onLockStart() {
        Logger.d("onLockStart: ");
        mHandler.foreground();
    }

    /**
     * 정책 잠금 처리가 종료되었습니다.
     */
    private void onLockStop() {
        Logger.d("onLockStop: ");
        mHandler.foreground();
    }

    /**
     * Accessibility 를 통해 전달받은 실행중인 앱 정보가 도착했습니다.
     *
     * @param pkg 실행중인 앱.
     */
    public void onAccessibilityUsageChanged(@Nullable String pkg) {
        Logger.d("start onAccessibilityUsageChanged: %s", pkg);
        mChangeSubject.onNext(pkg);
        Logger.d("end onAccessibilityUsageChanged: %s", pkg);
    }

    public static void onAccess(@Nullable String pkg) {
        Logger.d("onAccessibilityUsageChanged: %s", pkg);
        mChangeSubject.onNext(pkg);
    }

    /**
     * 화면 켜짐 상태가 변경되었습니다.
     *
     * @param on 켜졌으면 true.
     */
    public void onScreenOfOffChanged(boolean on) {
        Logger.d("onScreenOfOffChanged() called with: on = [%s]", on);
        if (on) {
            mHandler.findAbleLocks();
        } else {
            mUsageSubscription.unsubscribe();
        }
    }

    /**
     * 실행중인 앱이 변경되었습니다.
     * 조건에 따라 잠금 화면 부착 및 제거를 실행합니다.
     *
     * @param pkg 실행중인 앱.
     */
    private void onPackageChanged(String pkg) {

        Logger.d("onPackageChanged start %s", pkg);
        boolean isLa = false;
        if (pkg != null) {
            if (AppManageService.laucherList != null) {
                ArrayList<String> names = AppManageService.laucherList;
                for (int i = 0; i < names.size(); i++) {
                    //Launcher나 settings이면 lsLa를 true로 설정
                    if (!pkg.contains("settings") && pkg.equalsIgnoreCase((String) names.get(i))) {
                        isLa = true;
                        break;
                    }
                }
            }
        } else {
            //package가 null이면
            if (mDBHelper.ONOFF == 1) {
                Logger.i("pkg null block");
                mHandler.attach();
                return;
            } else {
                Logger.i("pkg null unblock");
                mHandler.detach();
                return;
            }
        }

        if (mIntentFilteredData == null) {
            Logger.d("mIntentFilteredData == null");
            mHandler.detach();
            return;

        } else {

            Logger.i("XX package is " + pkg);

            if (pkg.contains("studytime")) {
                Logger.d("studytime : %s", pkg);
                mHandler.detach();
                return;
            } else if (mIntentFilteredData.isCallOrSms(pkg)) {
                Logger.d("mIntentFilteredData.isCallOrSms(%s)", pkg);
                mHandler.detach();
                return;
            } else if (isLa && DBHelper.ONOFF == 1) {
                Logger.d("isLauncher(%s) || DBHelper.ONOFF == 1", pkg);
                mHandler.attach();
                return;
            } else if (isLa && DBHelper.ONOFF != 1) {
                Logger.d("isLauncher(%s) || DBHelper.ONOFF != 1", pkg);
                mHandler.detach();
                return;

            } else if (!mDBHelper.isExcepted(pkg) ) {
                Logger.d("!mDBHelper.isExcepted(%s)", pkg);
                /*if (pkg.contains("googlequicksearchbox") && DBHelper.ONOFF == 1) {
                    Logger.i("googlequicksearchbox");
                    mHandler.attach();
                    return;
                }else if(pkg.contains("googlequicksearchbox") && DBHelper.ONOFF != 1){
                    Logger.i("googlequicksearchbox");
                    mHandler.detach();
                    return;
                }*/
                if(DBHelper.ONOFF ==1 ) {
                    mHandler.attach();
                    return;
                }else {
                    mHandler.detach();
                    return;
                }
            } else if (mDBHelper.isExcepted(pkg)) {
                Logger.d("mDBHelper.isExcepted(%s)", pkg);
                Logger.d("pkg.contains(\"launcher\")" + pkg.contains("launcher"));
                Logger.d("DBHelper.ONOFF : " + DBHelper.ONOFF);
                if (pkg.contains("launcher")  && DBHelper.ONOFF == 1) {
                    mHandler.attach();
                    return;
                } else {
                    mHandler.detach();
                    return;
                }
            } else {
                Logger.d("not mIntentFilteredData.isCallOrSms(pkg)");
                if (DBHelper.ONOFF == 1) {
                    mHandler.attach();
                    return;
                } else {
                    mHandler.detach();
                    return;
                }
            }
        }
    }

    /**
     * 실행중인 앱을 가져오기 위한 유틸리티를 재생성합니다.
     * 권한이 변경되거나 없을 경우 재생성이 필요합니다.
     */
    public void createUsages() {
        Logger.d("createUsages: ");
        mUsages = UsagesFactory.create();
    }

    /**
     * 잠금 화면을 부착합니다.
     */
    public void attachFromHandler() {
        if (mLockView != null) {
            Logger.d("attachFromHandler: ");
            mLockView.attach();
        }
    }

    /**
     * 잠금 화면을 제거합니다.
     */
    public void detachFromHandler() {

        Logger.d("detachFromHandler start");
        if (mLockView != null) {
            Logger.d("detachFromHandler: ");
            mLockView.detach();
        }
    }

    /**
     * 알림 생성을 위한 {@link NotificationManager}를 생성하여 반환합니다.
     *
     * @return NotificationManager.
     */
    private NotificationManager notificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    private void registerRestartAlarm() {

        Intent intent = new Intent(LockService.this, AllIntentReceiver.class);
        intent.setAction("kr.co.digitalanchor.action.SERVICE_START");
        PendingIntent sender = PendingIntent.getBroadcast(LockService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += REBOOT_DELAY_TIMER;

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, REBOOT_DELAY_TIMER, sender);
    }

    private void unregisterRestartAlarm() {

        Intent intent = new Intent(LockService.this, AllIntentReceiver.class);
        intent.setAction("kr.co.digitalanchor.action.SERVICE_START");
        PendingIntent sender = PendingIntent.getBroadcast(LockService.this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    public LockServiceHandler getmHandler(){
        return mHandler;
    }

    private File getFileSystem(){

        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_ALARMS), "logForOnOffForChild1.txt");
        if(!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*if (!file.mkdirs()) {
                Logger.e("Directory not created");
            }else{
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }

        return file;
    }

    private void writeLog(){
        File file = getFileSystem();

        Logger.i("File path : " + file.getAbsolutePath());

        //File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_ALARMS), "logForOnOff.txt");

        //String filename = "logForOnOffParent";
        FileOutputStream outputStream;
        try{

            String value = "[자녀잠금] " + getCurrentTimeIncludeMs() + "\n";

            outputStream = new FileOutputStream(file, true);
            outputStream.write(value.getBytes());
            outputStream.close();

        }catch (Exception e){
            Logger.e(e.getMessage());
        }


    }
}
