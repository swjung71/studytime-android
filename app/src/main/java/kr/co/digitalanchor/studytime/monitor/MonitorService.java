package kr.co.digitalanchor.studytime.monitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;

import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.digitalanchor.studytime.R;

public class MonitorService extends Service {

    private final int notificationId;

    /// 1 Second = 1000 Milli Seconds
    private final double    ONE_SEC = 1000.0f;

    Timer timerDaemon;

    /// Timer Tasks
    TimerTask taskBlocking;     /// Block App

    public MonitorService() {

        notificationId = MonitorService.class.hashCode();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d("start MonitorService");

        showNotification();

        /// Running Timer Task as Daemon
        timerDaemon = new Timer(true);

        /// Create Tasks
        taskBlocking    = new TimerTaskWork(this);

        timerDaemon.scheduleAtFixedRate(taskBlocking, 0,    (long) (double) (0.5f * ONE_SEC));                  // 500 Milli Seconds

    }

    @Override
    public void onDestroy() {

        Logger.d("Destroy MonitorService");

        timerDaemon.cancel();

        dismissNotification();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.d("onStartCommand");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle("스터디타임").setContentText("제어중");

        Notification note = builder.build();
        note.flags  = Notification.FLAG_NO_CLEAR;
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(notificationId, note);
    }

    private void dismissNotification() {

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(notificationId);
    }
}
