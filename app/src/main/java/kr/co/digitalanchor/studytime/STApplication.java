package kr.co.digitalanchor.studytime;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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

        Logger.init("StudyTime").setLogLevel(LogLevel.FULL).hideThreadInfo();

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

    public static String getNationalCode() {

        /*
        String countryCode = null;

        try {

            TelephonyManager manager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

            countryCode = manager.getSimCountryIso();

        } catch (Exception e) {

            countryCode = null;
        }

        return TextUtils.isEmpty(countryCode) ? "" : countryCode;

        */

        return "kr";
    }

    public static String getLanguageCode() {

        Locale locale = applicationContext.getResources().getConfiguration().locale;

        return locale.getLanguage();
    }

    public static String getPhoneNumber() {

        String number = null;

        try {

            TelephonyManager manager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

            number = manager.getLine1Number();

            number = "0" + number.substring(number.length() - 10, number.length());

        } catch (Exception e) {

            number = null;
        }

        return TextUtils.isEmpty(number) ? "" : number;
    }

    public static String getDeviceNumber() {

        UUID uuid = null;


        final String androidId = Settings.Secure.getString(applicationContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        try {

            if (!"9774d56d682e549c".equals(androidId)) {

                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));

            } else {

                TelephonyManager manager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

                final String deviceId = manager.getDeviceId();

                uuid = deviceId != null ?
                        UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }

        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException(e);
        }

//        return uuid.toString().replaceAll("-", "");

        return "123456";
    }

    public static String getMAC() {


        return "";
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

    /**
     * 프리퍼런스에 저장
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean putString(String key, String value) {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);

        return editor.commit();
    }

    public static String getString(String key) {

        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        return pref.getString(key, defaultValue);
    }

    public static void clear() {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.clear();

        editor.commit();
    }

    public static String getAdpopcornDeviceId() {

        String adpopcornDeviceId = "";
        TelephonyManager tm = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

        if (tm != null) {

            adpopcornDeviceId = tm.getDeviceId();

        } else {

            // Wifi 전용 기기의 경우 Mac Address를 사용.
            WifiManager wm = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);

            if (wm != null) {

                WifiInfo wifiInfo = wm.getConnectionInfo();

                adpopcornDeviceId = wifiInfo.getMacAddress();

            }

        }

        return adpopcornDeviceId;
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

            res = false;

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
