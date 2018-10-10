package kr.co.digitalanchor.studytime.use;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

/**
 * 현재 실행중인 테스크를 이용하여 현재 실행중인 앱이 무엇인지 확인할 수 있습니다.
 * 안드로이드 {@link Build.VERSION_CODES#LOLLIPOP} 미만 버전에서만 사용할 수 있습니다.
 */
class UsagesLegacy implements Usages {

    private ActivityManager mActivityManager;

    UsagesLegacy(Context context) {
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String pkg() {
        try {
            return mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        } catch (Exception e) {
            return null;
        }
    }

}
