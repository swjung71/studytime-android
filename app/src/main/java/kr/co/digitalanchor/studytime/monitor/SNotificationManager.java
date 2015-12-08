package kr.co.digitalanchor.studytime.monitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.intro.IntroActivity;

/**
 * Created by Thomas on 2015-12-08.
 */
public class SNotificationManager {

    private static SNotificationManager mInstance = null;

    private final Context mContext;
    private final NotificationManager mManager;

    private Notification mForegroundNoti;

    public SNotificationManager(Context context) {

        mContext = context;

        mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private Notification getForegroundNoti(int isOff) {

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                5001, new Intent(mContext, IntroActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        builder.setContentTitle("스터디타임");

        String text = null;

        if (isOff != 1) {

            text = "스마트폰 잠금 해제";

        } else {

            text = "스마트폰 잠금";

        }

        builder.setContentText(text);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
        builder.setSmallIcon(R.drawable.icon_notificaiton);
        builder.setAutoCancel(false);
        builder.setOngoing(true);

        if (Build.VERSION.SDK_INT > 16) {

            builder.setStyle((new NotificationCompat.BigTextStyle()).bigText("스터디타임").setBigContentTitle(text));
        }

        builder.setPriority(-2);

        return builder.build();
    }

    public Notification getForegroundNotification(int isOff) {

        if (mForegroundNoti == null) {

            mForegroundNoti = getForegroundNoti(isOff);
        }
        return mForegroundNoti;
    }

    public void showForeground(int isOff) {

        mManager.notify(5001, getForegroundNotification(isOff));
    }
}
