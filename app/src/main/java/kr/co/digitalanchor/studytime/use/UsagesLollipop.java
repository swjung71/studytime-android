package kr.co.digitalanchor.studytime.use;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.List;


/**
 * 안드로이드 {@link Build.VERSION_CODES#LOLLIPOP} 이상 버전에서 사용할 수 있습니다.
 */
class UsagesLollipop implements Usages {

    private final Field mProcessStateField;
    private ActivityManager mActivityManager;

    UsagesLollipop(Context context, Field processStateField) {
        mProcessStateField = processStateField;
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public String pkg() {
        List<RunningAppProcessInfo> infoList = mActivityManager.getRunningAppProcesses();
        int size = infoList == null ? 0 : infoList.size();
        try {
            for (int i = 0; i < size; ++i) {
                RunningAppProcessInfo info = infoList.get(i);
                if (2 == mProcessStateField.getInt(info)) {
                    //찾은 값 반환.
                    return info.pkgList[info.pkgList.length - 1];
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
