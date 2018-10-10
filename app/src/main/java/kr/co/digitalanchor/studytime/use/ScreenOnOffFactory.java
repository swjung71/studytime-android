package kr.co.digitalanchor.studytime.use;

import android.os.Build;


/**
 * 안드로이드 운영체제 버전에 맞는 화면 켜짐 상태 확인을 위한 기능을 생성할 수 있습니다.
 */
public final class ScreenOnOffFactory {

    private ScreenOnOffFactory() {
    }

    /**
     * 화면 켜짐 상태 확인을 위한 기능을 생성합니다.
     *
     * @return {@link ScreenOnOff}
     */
    public static ScreenOnOff create() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return new ScreenOnOffKitkatWatch();
        } else {
            return new ScreenOnOffLegacy();
        }
    }
}
