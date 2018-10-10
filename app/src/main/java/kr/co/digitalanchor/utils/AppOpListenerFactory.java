package kr.co.digitalanchor.utils;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import kr.co.digitalanchor.studytime.STApplication;

/**
 * 현재 실행 앱을 조회할 때 일부 권한이 필요한 기능의 경우 권한이 부여된 후 재생성이 필요하여 권한 이벤트를 받는 기능입니다.
 */
public final class AppOpListenerFactory {

    private AppOpListenerFactory() {
    }

    @Nullable
    public static AppOpsManager.OnOpChangedListener add(@NonNull String op,
                                                        @NonNull AppOpChangedListener l) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Context context = STApplication.applicationContext;
                String pkg = context.getPackageName();

                AppOpsManager.OnOpChangedListener listener = (cbOp, cbPkg) -> {
                    if (op.equals(cbOp) && pkg.equals(cbPkg)) {
                        l.onChanged();
                    }
                };
                manager().startWatchingMode(op, pkg, listener);
                return listener;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void remove(@Nullable AppOpsManager.OnOpChangedListener l) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                if (l != null) manager().stopWatchingMode(l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static AppOpsManager manager() {
        Context context = STApplication.applicationContext;
        return (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
    }

    public interface AppOpChangedListener {
        void onChanged();
    }

}
