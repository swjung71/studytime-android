package kr.co.digitalanchor.studytime;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;

import static kr.co.digitalanchor.studytime.StaticValues.PREF;

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

    public static SharedPreferences getPreference() {

        return applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

    }

    private boolean saveSharedPreferenceToFile(File dest) {

        boolean res = false;

        ObjectOutputStream output = null;

        try {

            output = new ObjectOutputStream(new FileOutputStream(dest));

            SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);

            output.writeObject(pref.getAll());

            res = true;

        } catch (Exception e) {

            // TODO Exception

            res = true;

        } finally {

            try {

                if (output != null) {

                    output.flush();
                    output.close();
                }

            } catch (IOException e) {

                output = null;
            }
        }

        return res;
    }

    private boolean loadSharedPreferencesFromFile(File src) {

        boolean res = false;

        ObjectInputStream input = null;

        try {

            input = new ObjectInputStream(new FileInputStream(src));

            SharedPreferences.Editor editor = getSharedPreferences(PREF, MODE_PRIVATE).edit();

            editor.clear();

            Map<String, ?> entries = (Map<String, ?>) input.readObject();

            for (Map.Entry<String, ?> entry : entries.entrySet()) {

                Object value = entry.getValue();

                String key = entry.getKey();

                if (value instanceof Boolean) {

                    editor.putBoolean(key, ((Boolean) value).booleanValue());

                } else if (value instanceof Float) {

                    editor.putFloat(key, ((Float) value).floatValue());

                } else if (value instanceof Integer) {

                    editor.putInt(key, ((Integer) value).intValue());

                } else if (value instanceof Long) {

                    editor.putLong(key, ((Long) value).longValue());

                } else if (value instanceof String) {

                    editor.putString(key, (String) value);
                }
            }

            editor.commit();

            res = true;

        } catch (Exception e) {

            // TODO Exception

            res = false;

        } finally {

            try {

                if (input != null) {

                    input.close();
                }

            } catch (IOException e) {

                input = null;
            }
        }

        return res;
    }
}
