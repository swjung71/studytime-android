package kr.co.digitalanchor.studytime.use;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.List;

import kr.co.digitalanchor.studytime.STApplication;

/**
 * 안드로이드 운영체제 버전에 맞는 실행 앱 확인을 위한 기능을 생성하고 버전별 권한 처리를 위한 상태를 조회할 수 있습니다.
 */
public final class UsagesFactory {

    /**
     * UsageStats 설정창을 엽니다.
     *
     * @param context 컨텍스트.
     */
    public static void setting(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * UsageStats 이 활성화 되어 있는지 여부를 확인합니다
     *
     * @return UsageStats 활성화 여부.
     */
    public static boolean isAllowUsageStats() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Context context = STApplication.applicationContext;

                String pkg = context.getPackageName();
                PackageManager pm = context.getPackageManager();
                ApplicationInfo applicationInfo = pm.getApplicationInfo(pkg, 0);
                int uid = applicationInfo.uid;
                String op = AppOpsManager.OPSTR_GET_USAGE_STATS;

                AppOpsManager am = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = am.checkOpNoThrow(op, uid, pkg);
                return mode == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * UsageStats 사용이 가능한지 확인합니다.
     *
     * @return UsageStats 사용 가능 여부.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("ResourceType")
    public static boolean isAbleUsageStats() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Context context = STApplication.applicationContext;
                UsageStatsManager um = (UsageStatsManager) context.getSystemService("usagestats");
                long current = System.currentTimeMillis();
                List<UsageStats> list = um.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, current);
                return !list.isEmpty();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
     * 실행 앱 확인을 위한 기능을 생성합니다.
     *
     * @return {@link Usages}
     */
    @NonNull
    public static Usages create() {
        Context context = STApplication.applicationContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isAllowUsageStats() && isAbleUsageStats()) {
                Field lastEvent;
                try {
                    lastEvent = UsageStats.class.getDeclaredField("mLastEvent");
                    if (lastEvent != null) lastEvent.setAccessible(true);
                } catch (Exception e) {
                    lastEvent = null;
                }
                if (lastEvent == null) {
                    return new UsagesUsageStat(context);
                } else {
                    return new UsagesUsageStat2(context, lastEvent);
                }
            }
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                Field processStateField = null;
                try {
                    processStateField = ActivityManager.RunningAppProcessInfo
                            .class
                            .getDeclaredField("processState");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (processStateField != null) {
                    processStateField.setAccessible(true);
                    return new UsagesLollipop(context, processStateField);
                }
            }
        }
        return new UsagesLegacy(context);
    }

}
