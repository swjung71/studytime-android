package kr.co.digitalanchor.studytime.monitor;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.afollestad.materialdialogs.Utils;
import com.orhanobut.logger.Logger;

import java.io.CharArrayReader;
import java.lang.ref.WeakReference;


/**
 * {@link B} 처리 중 {@link android.os.Looper}가 필요하거나, 딜레이가 필요한 처리를 할 때 사용됩니다.
 */
public final class LockServiceHandler extends Handler {

    private static final int WHAT_TOAST = 1;
    private static final int WHAT_CREATE_USAGES = 2;
    private static final int WHAT_ATTACH = 3;
    private static final int WHAT_DETACH = 4;
    private static final int WHAT_FIND_ABLE_LOCKS = 5;
    private static final int WHAT_FIND_ABLE_LOCKS_DELAYED = 6;
    private static final int WHAT_FOREGROUND = 7;
    private static final int WHAT_SCREEN = 8;
    private static final int WHAT_CHANGE = 9;
    private static final String EXTRA_LOCK = "l";

    private WeakReference<LockService> mService;

    public LockServiceHandler(LockService service) {
        super();
        mService = new WeakReference<>(service);
    }

    public void release() {
        removeMessages(WHAT_TOAST);
        removeMessages(WHAT_CREATE_USAGES);
        removeMessages(WHAT_ATTACH);
        removeMessages(WHAT_DETACH);
        removeMessages(WHAT_FIND_ABLE_LOCKS);
        removeMessages(WHAT_FIND_ABLE_LOCKS_DELAYED);
        removeMessages(WHAT_FOREGROUND);
        if (mService != null) mService.clear();
        mService = null;
    }

    public void toast(String msg) {
        sendMessage(obtainMessage(WHAT_TOAST, msg));
    }

    /**
     * 실행 앱 확인 기능을 재생성합니다.
     */
    public void recreateUsage() {
        if (!hasMessages(WHAT_CREATE_USAGES)) {
            sendEmptyMessageDelayed(WHAT_CREATE_USAGES, 50);
        }
    }

    /**
     * 잠금 화면을 부착합니다.
     */
    public void attach() {
        removeMessages(WHAT_DETACH);
        removeMessages(WHAT_ATTACH);
        sendEmptyMessageDelayed(WHAT_ATTACH, 50);
    }

    /**
     * 잠금 화면을 제거합니다.
     */
    public void detach() {
        removeMessages(WHAT_DETACH);
        removeMessages(WHAT_ATTACH);
        sendEmptyMessageDelayed(WHAT_DETACH, 50);
    }

    /**
     * 현재 시간에 맞는 잠금 정보를 찾아 잠금 유무, 설정값에 따른 처리를 실행합니다.
     */
    public void findAbleLocks() {
        removeMessages(WHAT_FIND_ABLE_LOCKS);
        sendEmptyMessageDelayed(WHAT_FIND_ABLE_LOCKS, 1000);
    }

    /**
     * {@link #findAbleLocks()} 실행시 딜레이를 부여할 수 있습니다.
     *
     * @param delayMillis 딜레이.
     */
    public void findAbleLocksDelayed(long delayMillis) {
        sendEmptyMessageDelayed(WHAT_FIND_ABLE_LOCKS_DELAYED, delayMillis);
    }

    /**
     * 서비스를 Foreground 로 전환합니다. 잠금 정보가 null 이 아니라면 해당 설정에 따라 메세지를 표시합니다.
     *
     */
    public void foreground() {
        Message message = obtainMessage(WHAT_FOREGROUND);
        message.sendToTarget();
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg == null) {

            Logger.i("msg is null");
            return;
        }
        if (mService == null) {
            Logger.i("mService is null");
            return;
        }
        LockService service = mService.get();
        if (service == null) {
            Logger.i("service is null");
            return;
        }
        switch (msg.what) {
            case WHAT_TOAST: {
                //Utilstoast(msg.obj.toString(), msg.arg1);
                break;
            }
            case WHAT_CREATE_USAGES: {
                service.createUsages();
                break;
            }
            case WHAT_ATTACH: {
                Logger.i("what attach");
                service.attachFromHandler();
                break;
            }
            case WHAT_DETACH: {
                Logger.i("what detach");
                service.detachFromHandler();
                break;
            }
            case WHAT_FIND_ABLE_LOCKS: {
                Logger.i("service findable locks");
                service.findAbleLocks();
                break;
            }
            case WHAT_FIND_ABLE_LOCKS_DELAYED: {
                Logger.i("this.findableLocks");
                this.findAbleLocks();
                break;
            }
            case WHAT_FOREGROUND: {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    //onHandleForeground(service);
                }
                //onHandleForeground(service);
                break;
            }
        }
    }

    /*private void onHandleForeground(B service) {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent = new Intent(service, B.class);
        PendingIntent pd = PendingIntent.getService(service, 1, intent, flags);

        String title = "스터디타임";
        String text = null;


        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(service)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setShowWhen(false)
                .setContentIntent(pd);

        //아이콘 투명 처리.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(service, R.color.colorPrimary);
            builder.setSmallIcon(R.drawable.status_ic);
            builder.setColor(color);
        } else {
            Resources res = service.getResources();
            Bitmap icon = BitmapFactory.decodeResource(res, R.drawable.status_ic);
            builder.setSmallIcon(R.drawable.transparent);
            builder.setLargeIcon(icon);
        }
        service.startForeground(1, null);
        service.startForeground(1, builder.build());
    }*/
}