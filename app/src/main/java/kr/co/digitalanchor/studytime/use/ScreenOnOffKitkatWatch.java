package kr.co.digitalanchor.studytime.use;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import java.lang.ref.WeakReference;

import kr.co.digitalanchor.studytime.STApplication;


/**
 * 안드로이드 {@link Build.VERSION_CODES#KITKAT_WATCH} 이상 버전에서 화면 켜진 여부를 확인할 수 있습니다.
 */
class ScreenOnOffKitkatWatch implements ScreenOnOff {


    private WeakReference<PowerManager> mPowerManager;

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public boolean isOn() {
        if (mPowerManager == null || mPowerManager.get() == null) {
            Context context = STApplication.applicationContext;
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mPowerManager = new WeakReference<>(pm);
        }
        return mPowerManager.get().isInteractive();
    }
}
