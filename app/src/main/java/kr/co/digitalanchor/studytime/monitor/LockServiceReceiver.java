package kr.co.digitalanchor.studytime.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityEvent;

import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import kr.co.digitalanchor.studytime.use.ScreenOnOff;
import kr.co.digitalanchor.studytime.use.ScreenOnOffFactory;

import static kr.co.digitalanchor.utils.Intents.ACTION_USAGE_CHANGED;
import static kr.co.digitalanchor.utils.Intents.ACTION_USAGE_SWITCH;
import static kr.co.digitalanchor.utils.Intents.EXTRA_PACKAGE;


/**
 * 상태 정보를 수신하여 {@link B} 로 전달하는 {@link BroadcastReceiver}입니다.
 * LockService 처리 중 BroadcastReceiver 가 필요하고 Manifest 에 등록할 수 없는 이벤트를 수신할 때 사용됩니다.
 */
public final class LockServiceReceiver extends BroadcastReceiver {

    @NonNull
    private final WeakReference<LockService> mService;
    private final ScreenOnOff mScreen = ScreenOnOffFactory.create();

    public LockServiceReceiver(LockService service) {
        mService = new WeakReference<>(service);
        service.onScreenOfOffChanged(mScreen.isOn());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("start LockServiceReceiver " );
        //유효성 검증.
        if (intent == null){
            Logger.d("intent == null");
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            Logger.d("action == null");
            return;
        }
        LockService service = mService.get();
        if (service == null) {
            Logger.d("service == null");
            return;
        }
        //데이터 처리.
        switch (action) {

            //접근성에서 보낸 메세지. 실행 앱 변경.
            case ACTION_USAGE_CHANGED:
            case ACTION_USAGE_SWITCH: {
                String pkg = intent.getStringExtra(EXTRA_PACKAGE);
                Logger.i("LockServiceReceiver : " + pkg);
                service.onAccessibilityUsageChanged(pkg);
                break;
            }
            //화면 ON, OFF
            case Intent.ACTION_SCREEN_ON:
            case Intent.ACTION_SCREEN_OFF: {
                Logger.d("ACTION_SCREEN_ON_OFF");
                service.onScreenOfOffChanged(mScreen.isOn());
                break;
            }
        }

       /* Message msg = new Message();
        msg.what = REQUEST_CHECK;
        msg.obj = intent;
        mHandler.sendMessage(msg);*/
        return;

    }

    private void check(Intent intent){

        Logger.d("start check ");
        LockService service = mService.get();
        String action = intent.getAction();

        switch (action) {
            //접근성에서 보낸 메세지. 실행 앱 변경.
            case ACTION_USAGE_CHANGED: {
                String pkg = intent.getStringExtra(EXTRA_PACKAGE);
                Logger.i("LockServiceReceiver : " + pkg);
                service.onAccessibilityUsageChanged(pkg);
                break;
            }
            //화면 ON, OFF
            case Intent.ACTION_SCREEN_ON:
            case Intent.ACTION_SCREEN_OFF: {
                Logger.d("ACTION_SCREEN_ON_OFF");
                service.onScreenOfOffChanged(mScreen.isOn());
                break;
            }
        }
    }
}