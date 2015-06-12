package kr.co.digitalanchor.studytime;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Handler;

import java.util.Hashtable;

/**
 * Created by Thomas on 2015-06-10.
 */
public class STApplication extends Application {

    /**
     * xxh-dpi 으로 제작됨
     */

    public static volatile Context applicationContext;

    public static volatile Handler applicationHandler;

    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();

    @Override
    public void onCreate() {

        super.onCreate();

        // TODO Application initialize

        applicationContext = getApplicationContext();

        applicationHandler = new Handler(applicationContext.getMainLooper());

        getTypeface(StaticValues.FONT_NANUM_GOTHIC);

        getTypeface(StaticValues.Font_NANUM_GOTHIC_BOLD);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        // TODO 언어 설정이 변경되면!!!
    }


    /**
     * Utils function (Application Context needed)
     */

    /**
     * @return version code in manifest.xml
     */
    public static int getAppVersionCode() {

        try {

            PackageInfo info = applicationContext.getPackageManager()
                    .getPackageInfo(applicationContext.getPackageName(), 0);

            return info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * @return version name in manifest.xml
     */
    public static String getAppVersionName() {

        try {

            PackageInfo info = applicationContext.getPackageManager()
                    .getPackageInfo(applicationContext.getPackageName(), 0);

            return info.versionName;

        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * @param assetPath path of font
     * @return typeface
     */
    public static Typeface getTypeface(String assetPath) {

        synchronized (typefaceCache) {

            if (!typefaceCache.containsKey(assetPath)) {

                try {

                    Typeface t = Typeface.createFromAsset(applicationContext.getAssets(), assetPath);

                    typefaceCache.put(assetPath, t);

                } catch (Exception e) {

                    return null;

                }
            }

            return typefaceCache.get(assetPath);
        }
    }
}
