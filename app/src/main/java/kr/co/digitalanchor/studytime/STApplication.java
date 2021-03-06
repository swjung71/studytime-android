package kr.co.digitalanchor.studytime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.captechconsulting.captechbuzz.model.images.ImageCacheManager;
import com.captechconsulting.captechbuzz.model.images.RequestManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nhn.android.naverlogin.OAuthLogin;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.intro.IntroActivity;

import static kr.co.digitalanchor.studytime.StaticValues.PREF;

/**
 * Created by Thomas on 2015-06-10.
 */
public class STApplication extends MultiDexApplication {

    /**
     * xxh-dpi 으로 제작됨
     */
    public static volatile Context applicationContext;

    public static volatile Handler applicationHandler;

    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();

    private static final List<BaseActivity> activities = new ArrayList<>();

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public static Toast toast;

    private static final int DEFAULT_CACHE_SIZE = 10485760;
    private static final long DEFAULT_MAX_AGE = 60L;

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided

    static Pattern URL_PATTERN;

    public static boolean isFirstTimeAccessbilityTurnOn = false;

    @Override
    public void onCreate() {

        super.onCreate();

        applicationContext = getApplicationContext();

        applicationHandler = new Handler(applicationContext.getMainLooper());

        // TODO Google analystics initialize

        /*analytics = GoogleAnalytics.getInstance(this);
        analytics.setDryRun(false);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-63663050-2");
        //tracker = analytics.newTracker(R.xml.global_tracker);
        tracker.setAppName("Studytime");
        tracker.setAppVersion(getAppVersionName());
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.setSessionTimeout(1800);*/

        /**
         * Log Setting
         * */
        Logger.init("StudyTime").setLogLevel(LogLevel.NONE).hideThreadInfo();

        RequestManager.init(this);
        createImageCache();

        URL_PATTERN = Pattern.compile("^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        // TODO 언어 설정이 변경되면!!!
    }

    private void createImageCache() {
        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                , DISK_IMAGECACHE_SIZE
                , DISK_IMAGECACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);
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

    public static boolean isUpdate(String currentVersionName) {

        boolean isUpdate = false;

        try {

            PackageInfo info = applicationContext.getPackageManager()
                    .getPackageInfo(applicationContext.getPackageName(), 0);

            String[] appVersionTokens = info.versionName.split("\\.");

            String[] curVersionTokens = currentVersionName.split("\\.");

            for (int i = 0; appVersionTokens.length > i; i++) {

                try {

                    if (Integer.parseInt(appVersionTokens[i]) < Integer.parseInt(curVersionTokens[i])) {

                        isUpdate = true;

                        break;

                    } else if (Integer.parseInt(appVersionTokens[i]) > Integer.parseInt(curVersionTokens[i])) {

                        isUpdate = false;

                        break;
                    }

                } catch (NumberFormatException e) {

                    isUpdate = false;

                    break;
                }
            }

        } catch (Exception e) {

            isUpdate = false;
        }

        return isUpdate;
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

        TelephonyManager tm = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

        String id = tm.getDeviceId();

        if (TextUtils.isEmpty(id)) {

            id = getDeviceID();
        }

        return id;
    }

    public static String getDeviceID() {

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

        return uuid.toString().replaceAll("-", "");

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

    public static int getInt(String key, int defaultValue) {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        return pref.getInt(key, defaultValue);
    }

    public static boolean putInt(String key, int value) {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);

        return editor.commit();
    }

    public static long getLong(String key, long defaultValue) {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        return pref.getLong(key, defaultValue);
    }

    public static boolean putLong(String key, long value) {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(key, value);

        return editor.commit();
    }


    public static boolean getBoolean(String key, boolean defaultValue) {

        try {

            SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

            return pref.getBoolean(key, defaultValue);

        } catch (Exception e) {

            return defaultValue;
        }
    }

    public static boolean putBoolean(String key, boolean value) {

        SharedPreferences pref = applicationContext.getSharedPreferences(PREF, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);

        return editor.commit();
    }

    public static boolean putRegistrationId(String version) {

        return putString(StaticValues.GCM_REG_ID, version);
    }

    public static String getRegistrationId() {

        return getString(StaticValues.GCM_REG_ID, "");
    }

    public static boolean putRegisteredVersion(int version) {

        return putInt("property_app_version", version);
    }

    public static int getRegisteredVersion() {

        return getInt("property_app_version", Integer.MIN_VALUE);
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

    public static void addActivity(BaseActivity acitivity) {

        activities.add(acitivity);
    }

    public static void removeActivity(BaseActivity activity) {

        activities.remove(activity);

    }

    public static void stopAllActivity() {

        for (BaseActivity activity : activities) {

            activity.finish();
        }
    }

    public static void resetApplication() {

        //DBHelper helper = new DBHelper(applicationContext);
        DBHelper helper = DBHelper.getInstance(applicationContext);

        helper.clearAll();

        try {
            OAuthLogin oAuthLoginInstance = OAuthLogin.getInstance();

            oAuthLoginInstance.logout(applicationContext);

            LoginManager.getInstance().logOut();

        } catch (Exception e) {

            Logger.e(e.toString());
        }

        clear();

        for (BaseActivity activity : activities) {

            activity.finish();
        }

        Intent intent = new Intent(applicationContext, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        applicationContext.startActivity(intent);
    }

    public static Pattern getUrlPattern() {

        return URL_PATTERN;
    }

    //return studytime이 세팅되어 있지 않으면 false를 return
    public static boolean isAccessibilityEnabled() {

        int accessibilityEnabled = 0;

        boolean accessibilityFound = false;

        try {
            accessibilityEnabled = Settings.Secure.getInt(applicationContext.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);

            //Logger.d("Accessibility: " + accessibilityEnabled);

        } catch (Settings.SettingNotFoundException e) {
            Logger.d("Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {

            //Logger.d("***Accessibility IS ENABLED***: ");


            String settingValue = Settings.Secure.getString(applicationContext.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            //Logger.d("Setting: " + settingValue);

            if (settingValue != null) {

                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);

                while (splitter.hasNext()) {

                    String accessabilityService = splitter.next();

                    //Logger.d("Setting: " + accessabilityService);

                    if (accessabilityService.equalsIgnoreCase("kr.co.digitalanchor.studytime/kr.co.digitalanchor.studytime.monitor.A")) {

                        //Logger.d("We've found the correct setting - accessibility is switched on!");

                        isFirstTimeAccessbilityTurnOn = true;

                        return true;
                    }
                }
            }

//            Logger.d("***END***");

        } else {

            Logger.d("***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;


    }
}
