package kr.co.digitalanchor.studytime.monitor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;

import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.block.BlockActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;

public class MonitorService extends Service {

    private final int notificationId = Integer.MAX_VALUE;

    /// 1 Second = 1000 Milli Seconds
    private final double ONE_SEC = 1000.0f;
    private final long ONE_SECOND = 1000L;

    private static final int REBOOT_DELAY_TIMER = 2 * 1000;

    Timer timerDaemon;

    /// Timer Tasks
    TimerTask taskBlocking;     /// Block App

    TimerTask taskPreventAdmin; // Block device admin

    TimerTask taskSyncData;

    TimerTask taskUpdatePackageList;

    TimerTask taskUpdateDB;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d("start MonitorService");

        showNotification();

        /// Running Timer Task as Daemon
        timerDaemon = new Timer(true);

        /// Create Tasks
        taskBlocking = new TimerTaskWork(this);
        taskPreventAdmin = new TimerTaskPreventUncheckDeviceAdmin(this);
        taskSyncData = new TimerTaskSyncData(this);
        taskUpdatePackageList = new TimerTaskUpdatePackageList(this);
        taskUpdateDB = new TimerTaskUpdateDB(this);

        timerDaemon.scheduleAtFixedRate(taskBlocking, 0, (long) (double) (0.5f * ONE_SEC));                  // 500 Milli Seconds
        timerDaemon.scheduleAtFixedRate(taskPreventAdmin, 0, (long) (double) (0.5f * ONE_SEC));
        timerDaemon.scheduleAtFixedRate(taskSyncData, 100L * ONE_SECOND, 600L * ONE_SECOND);
        timerDaemon.scheduleAtFixedRate(taskUpdatePackageList, 150L * ONE_SECOND, 6L + 60L * 60L * ONE_SECOND);
        timerDaemon.scheduleAtFixedRate(taskUpdateDB, 24L * 60L * 60L * ONE_SECOND, 24L * 60L * 60L * ONE_SECOND);

        unregisterRestartAlarm();

    }

    /**
     * onDestroy
     * 1. 단말 감시하는 스레드를 종료
     * 2. 노피티피케이션을 삭제
     */
    @Override
    public void onDestroy() {

        Logger.d("Destroy MonitorService");

        killApplication();

        if (timerDaemon != null) {

            timerDaemon.cancel();
        }

        registerRestartAlarm();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.d("onStartCommand");

        startForeground(notificationId, showNotification());

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Notification showNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        DBHelper helper = new DBHelper(getApplicationContext());

        if (helper.getOnOff() != 1) {

            builder.setContentText("스마트폰 잠금 해제");

        } else {

            builder.setContentText("스마트폰 잠금");

        }

        builder.setSmallIcon(R.drawable.icon_notificaiton).setContentTitle("스터디타임")
                .setLargeIcon(bm);


        return builder.build();
    }

    private void registerRestartAlarm() {

        Intent intent = new Intent(MonitorService.this, AllIntentReceiver.class);
        intent.setAction("kr.co.digitalanchor.action.SERVICE_START");
        PendingIntent sender = PendingIntent.getBroadcast(MonitorService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += REBOOT_DELAY_TIMER;

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, REBOOT_DELAY_TIMER, sender);
    }

    private void unregisterRestartAlarm() {

        Intent intent = new Intent(MonitorService.this, AllIntentReceiver.class);
        intent.setAction("kr.co.digitalanchor.action.SERVICE_START");
        PendingIntent sender = PendingIntent.getBroadcast(MonitorService.this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    private void killApplication() {

        Intent block = new Intent(getApplicationContext(), BlockActivity.class);

        block.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(block);

        Intent main = new Intent(Intent.ACTION_MAIN);

        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, main, PendingIntent.FLAG_ONE_SHOT);

        final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);

    }
}
