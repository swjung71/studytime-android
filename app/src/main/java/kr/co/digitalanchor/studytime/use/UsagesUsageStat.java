package kr.co.digitalanchor.studytime.use;

import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UsageStats 이 사용 가능할 때, UsageStats 을 이용하여 실행 앱을 조회할 수 있습니다.
 */
class UsagesUsageStat implements Usages {

    @SuppressWarnings("WeakerAccess")
    final long mHourMillis, mDayMillis;

    final UsageStatsManager mUsm;

    private String mPrev;
    private int mLevel;
    private final long[] mIntervals;

    @SuppressWarnings("WrongConstant")
    UsagesUsageStat(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUsm = (UsageStatsManager) context.getSystemService("usagestats");
        } else {
            mUsm = null;
        }
        mHourMillis = TimeUnit.HOURS.toMillis(1);
        mDayMillis = TimeUnit.DAYS.toMillis(1);

        mIntervals = new long[]{
                TimeUnit.SECONDS.toMillis(30),
                TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(10),
                TimeUnit.MINUTES.toMillis(30), mHourMillis, TimeUnit.HOURS.toMillis(2),
                TimeUnit.HOURS.toMillis(3), mDayMillis
        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String pkg() {
        long end = System.currentTimeMillis();
        if (mLevel >= mIntervals.length) mLevel = 0;
        for (int i = mLevel; i < mIntervals.length; ++i) {
            String pkg = getForeGroundPackage(mUsm.queryEvents(end - mIntervals[i], end));
            if (pkg == null) {
                ++mLevel;
            } else {
                if (!pkg.equals(mPrev)) {
                    mPrev = pkg;
                    mLevel = 0;
                }
                return pkg;
            }
        }

        List<UsageStats> statsList;
        statsList = mUsm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, end - mHourMillis, end);
        if (statsList == null || statsList.size() == 0) {
            statsList = mUsm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, end - mDayMillis, end);
        }

        if (statsList != null && statsList.size() > 0) {
            UsageStats maxStats = null;
            long maxLastTimeUsed = 0;
            for (UsageStats stats : statsList) {
                long lastTimeUsed = stats.getLastTimeUsed();
                if (maxLastTimeUsed < lastTimeUsed) {
                    maxLastTimeUsed = lastTimeUsed;
                    maxStats = stats;
                }
            }
            if (maxStats != null) {
                return maxStats.getPackageName();
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getForeGroundPackage(UsageEvents usageEvents) {
        String packageName = null;
        long highestMoveToForegroundTimeStamp = 0;
        UsageEvents.Event event = new UsageEvents.Event();
        while (usageEvents.getNextEvent(event)) {
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                long timeStamp = event.getTimeStamp();
                if (timeStamp > highestMoveToForegroundTimeStamp) {
                    packageName = event.getPackageName();
                    highestMoveToForegroundTimeStamp = timeStamp;
                }
            }
        }
        return packageName;
    }
}
