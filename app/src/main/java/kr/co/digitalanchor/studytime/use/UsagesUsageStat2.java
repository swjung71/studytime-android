package kr.co.digitalanchor.studytime.use;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.List;

/**
 * UsageStats 이 사용 가능할 때, UsageStats 을 이용하여 실행 앱을 조회할 수 있습니다.
 * {@link UsagesUsageStat} 보다 속도가 빠르나, 일부 기기에서 동작하지 않을 수 있습니다.
 * 이 때는 UsagesUsageStat 의 기능을 사용합니다.
 */
class UsagesUsageStat2 extends UsagesUsageStat {

    private final Field mLastEvent;

    UsagesUsageStat2(Context context, Field lastEvent) {
        super(context);
        mLastEvent = lastEvent;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String pkg() {
        long end = System.currentTimeMillis();
        List<UsageStats> statsList;
        statsList = mUsm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, end - mHourMillis, end);
        if (statsList == null || statsList.size() == 0) {
            statsList = mUsm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, end - mDayMillis, end);
        }
        try {
            UsageStats last = null;
            if (statsList != null && statsList.size() > 0) {
                for (UsageStats stats : statsList) {
                    if (mLastEvent.getInt(stats) == 1) {
                        if (last == null || last.getLastTimeUsed() < stats.getLastTimeUsed()) {
                            last = stats;
                        }
                    }
                }
            }
            if (last != null) {
                return last.getPackageName();
            }
        } catch (Exception ignore) {
        }
        return super.pkg();
    }

}
