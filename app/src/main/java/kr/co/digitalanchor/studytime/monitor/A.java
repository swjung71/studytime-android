package kr.co.digitalanchor.studytime.monitor;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.block.BlockPasswordLayout;
import kr.co.digitalanchor.studytime.database.AdultDBHelper;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.Base64;
import kr.co.digitalanchor.utils.Base64DecoderException;
import kr.co.digitalanchor.utils.IntentFilteredData;

import static kr.co.digitalanchor.utils.Intents.ACTION_USAGE_CHANGED;
import static kr.co.digitalanchor.utils.Intents.ACTION_USAGE_SWITCH;
import static kr.co.digitalanchor.utils.Intents.EXTRA_PACKAGE;
import static kr.co.digitalanchor.utils.Intents.EXTRA_SWITCH;

/**
 * Created by Thomas on 2015-09-15.
 */
public class A extends AccessibilityService {

    private final static String facebook = "Y3BvLmpmaWxqeHl2LmJmcnE=";
    private final static String chrome = "Y3BvLmVzanl3cm4ub3VmZGN2";
    private final static String lge = "bGhn";

    private static String facebookD;
    private static String chromeD;
    private static String lgeD;

    private boolean isSame = false;

    DBHelper mHelper;

    AdultDBHelper mAdultDBHelper;

    KeyguardManager mKeyguardManager;

    Toast toast;

    View blockView = null;

    Object object;
    Object object1;
    ActivityManager mActivityManager;

    WindowManager mWindowManager;

    WindowManager.LayoutParams mLayoutParams;

    private String mLastAccessibilityPackage = null;
    private final Rect mBounds = new Rect();

    public static boolean isDummy = false;

    private static A.CheckThread thread;

    //private static final int REQUST_CHECK = 5001;

    Handler mHandler;
    IntentFilteredData mIntentFilteredData;

    @Override
    protected void onServiceConnected() {

        Logger.i("Accessbility is started");
        super.onServiceConnected();

        //mHelper = new DBHelper(getApplicationContext());
        mHelper = DBHelper.getInstance(getApplicationContext());

        mAdultDBHelper = new AdultDBHelper(getApplicationContext());

        mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        setURL();

        //toast에 layout 적용 LG폰의 guest모드 때문에 적용
        toast = Toast.makeText(getApplicationContext(), "차단", Toast.LENGTH_SHORT);

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View layout = inflater.inflate(R.layout.activity_block, null);

        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setMargin(0.0f, 0.0f);
        toast.setView(layout);

        mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        object = new Object();

        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //SWJ 2017-06-04
        sendBroadcast(new Intent(ACTION_USAGE_SWITCH).putExtra(EXTRA_SWITCH, true));

        thread = new CheckThread();
        mLastAccessibilityPackage = null;
        mIntentFilteredData = new IntentFilteredData();
    }

    //SWJ 2017-06-04
    @Override
    public void onDestroy() {
        super.onDestroy();
        thread = null;
        sendBroadcast(new Intent(ACTION_USAGE_SWITCH).putExtra(EXTRA_SWITCH, false));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Logger.d("swj package name " + event.getPackageName().toString()
                + "\nclass name " + event.getClassName().toString()
                + "\nevent type " + event.getEventType());
        Account account = mHelper.getAccountInfo();

        if (account.getIsChild() != 0) {
            Logger.d("swj is not child");
            return;
        }

        String packageName = null;
        if(event.getPackageName() == null) {
            return;
        }else {
            packageName = event.getPackageName().toString();
        }
        //Logger.d("swj pk [" + packageName + "]  version = " + Build.VERSION.SDK_INT);

        //앱 차단
        /*if (chromeD.equalsIgnoreCase(packageName)) {

            if (event.getClassName().toString().equals("android.widget.EditText")
                    && event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {

                AccessibilityNodeInfo source = event.getSource();
                CharSequence typedURL = source.getText();

                String toString = typedURL.toString();

                Logger.d(toString);

                if (!toString.startsWith("http")) {

                    toString = "http://" + toString;
                }

                if (!toString.endsWith("/")) {

                    toString = toString + "/";
                }

                Logger.d(toString);

                String[] data = extractUrlParts(toString);

                if (data == null || data.length < 1) {

                    return;
                }

                if (mAdultDBHelper.isAdultURL(new MD5ForAdultURL().toDigest(data[0]), data[1])) {

                    isSame = true;

                } else {
                    isSame = false;
                }


            } else if ((event.getClassName().toString().equals("android.widget.ScrollView")
                    || event.getClassName().toString().equals("android.view.ViewGroup"))
                    && (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) {

                if (isSame) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        performGlobalAction(GLOBAL_ACTION_BACK);

                    }

                    isSame = false;
                }
            }
        }
*/

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
///*
//            Logger.d("is over Lolipop packageName [" + packageName + "]");
//
//            if (mHelper.getOnOff() == 1
//                    && !mHelper.isExcepted(packageName)
//                    && !packageName.equals("com.android.systemui")
//                    && !isKeyboard(packageName)
//                    && !packageName.equals("com.lge.sizechangable.weather")
//                    && !getApplicationContext().getPackageName().equals(packageName)) {
//
//                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//
//                        performGlobalAction(GLOBAL_ACTION_HOME);
//
//                        Logger.d("swj accessiblity block " + packageName);
//                        showBlockToast();
//
//                        return;
//
//                    }
//                }
//
//            }
//*/
//        } else {

        //Logger.d("is under Lolipop packageName [" + packageName + "]");


        //SWJ 2017-06-04

        if (mHelper.ONOFF == 1) {

            if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                Logger.d("accessbility event type : " + event.getEventType());
                return;
            }

            /*if (TextUtils.equals(mLastAccessibilityPackage, event.getPackageName().toString())) {
                return;
            }*/

            try {
                if (event == null) {
                    Logger.i("event is null");
                    return;
                }
                //접근성 이벤트 필터링.

                CharSequence pkgCs = event.getPackageName();

                if (pkgCs == null || pkgCs.length() == 0) return;

                String pkg = pkgCs.toString();

                Logger.d("package name : " + pkg);

                //새로운 패키지인지 확인.
                if (!TextUtils.equals(mLastAccessibilityPackage, pkg)) {

                    //이벤트 유효성 검사.
                    if (event.isFullScreen()) {
                        if (pkg.startsWith("com.google.android.inputmethod")) {
                            return;
                        }
                        AccessibilityNodeInfo source = event.getSource();
                        if (source != null) {
                            source.getBoundsInParent(mBounds);
                            if (mBounds.top + mBounds.bottom <= 0 || mBounds.left + mBounds.right <= 0) {
                                return;
                            }
                            source.getBoundsInScreen(mBounds);
                            if (mBounds.top + mBounds.bottom <= 0 || mBounds.left + mBounds.right <= 0) {
                                return;
                            }
                        }
                    } else {
                        AccessibilityNodeInfo source = event.getSource();
                        if (source == null) {
                            return;
                        }
                        int count = source.getChildCount();
                        if (count == 0) {
                            return;
                        }
                        if ("com.android.systemui".equals(pkg)) {
                            return;
                        }
                        for (int i = 0; i < count; ++i) {
                            if (source.getChild(i) == null) {
                                return;
                            }
                        }
                    }
                }

                //앱 사용 정보 전달.
                //PendingIntent.getBroadcast(STApplication.applicationContext, 0, new Intent(ACTION_USAGE_CHANGED).putExtra(EXTRA_PACKAGE, pkg), 0).send();
                Logger.i("sendBroadcast start");
                sendBroadcast(new Intent(ACTION_USAGE_CHANGED).putExtra(EXTRA_PACKAGE, pkg));
                Logger.i("sendBroadcast end");
                mLastAccessibilityPackage = pkg;


                if(mIntentFilteredData.isCallOrSms(pkg)){
                    Logger.i("it is call or sms");
                    return;
                }
                /*if(pkg.contains("incallui") || pkg.contains("dialer")) {
                    Logger.i("it is incallui");
                    return;
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        /*if (mHelper.ONOFF == 1
                && facebookD.equals(packageName)
                && !mHelper.isExcepted(packageName))

        {

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    performGlobalAction(GLOBAL_ACTION_HOME);

                    Logger.d("swj facebookD block " + packageName);

                    showBlockToast();

                    return;

                }
            }
        }*/
        //}

        //SWJ 2016-01-08 디바이스 관리자
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)

        {

            // TODO: 16. 2. 12. text를 읽어서 더 정밀하게 디바이스 관리자 처리하기
            //swj 1이면 삭제 불가, off상태,
            //SHOW_ADMIN이면 비밀번호를 넣고 삭제 가능한 상태가 됨
            //isAllow == 0이면 삭제가능한 상태

            /*Logger.d("in A : " + packageName);
            Logger.d("in A : isAllow " + mHelper.isAllow());
            Logger.d("in A : STApplication.getBoolean(StaticValues.SHOW_ADMIN, false) " +
                    STApplication.getBoolean(StaticValues.SHOW_ADMIN, false));

            Logger.d("in A : isKeyboard " + isKeyboard(packageName));
            Logger.d("in A : className " + event.getClassName());
            
            List<CharSequence> list =  event.getText();
            for (CharSequence a: list) {
                Logger.d("in A : text " + a.toString());
                if(a.toString().contains("Studytime")){
                    Logger.d("in A : contain Studytime");
                }
            }*/

            //Logger.d("SWJ in A : packagename " + packageName);
            boolean isDeviceAdmin = false;
            List<CharSequence> list = event.getText();
            for (CharSequence a : list) {
                String temp = a.toString();
                if (temp.contains("디바이스 관리자")
                        || temp.contains("기기 관리자")
                        || temp.contains("휴대폰 관리자")
                        || temp.contains("장치 관리자")
                        || temp.contains("휴대폰 관리")) {
                    Logger.d("in A : contain Studytime");
                    isDeviceAdmin = true;
                }
            }

            /*Logger.d("SWJ isAllow : " + mHelper.isAllow());
            Logger.d("SWJ packageName : " + packageName);
            Logger.d("SWJ STApplication : " + STApplication.getBoolean(StaticValues.SHOW_ADMIN, false));
            Logger.d("SWJ isDeviceAdmin : " + isDeviceAdmin);
            Logger.d("SWJ isKeyboard : " + isKeyboard(packageName));*/

            if (mHelper.ISALLOW == 1
                    //true가 disable된 상태
                    && STApplication.getBoolean(StaticValues.SHOW_ADMIN, false)
                    && !packageName.equals("com.android.settings.DeviceAdminAdd")
                    && !isDeviceAdmin
                    //&& !packageName.equals("com.android.settings")
                    && !isKeyboard(packageName)
                    //swj 확인 필요
                    //&& !packageName.equals("com.android.systemui")
                    && !getApplicationContext().getPackageName().equals(packageName)) {
                showBlockView();
                Logger.d("swj password blcok in A");

                return;

            } else {

                hideBlockView();
                //Logger.d("isAllow : " + mHelper.isAllow());
                //Logger.d("STApplication.getBoolean(StaticValues.SHOW_ADMIN, false:" + STApplication.getBoolean(StaticValues.SHOW_ADMIN, false));
                //Logger.d("packageName.equals(com.android.settings.DeviceAdminAdd): " + packageName.equals("com.android.settings.DeviceAdminAdd"));
                //Logger.d("packageName : " + packageName);
                //Logger.d("swj password blcok hide in A");

                return;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private static void setURL() {

        facebookD = "";

        try {

            byte[] decoded = Base64.decode(facebook);

            String tmpString = new String(decoded);

            byte[] value = Base64.decrypt(decoded);

            facebookD = new String(value);

        } catch (Base64DecoderException e) {

            Logger.e(e.getMessage());


        } catch (Exception e) {

            Logger.e(e.toString());
        }

        chromeD = "";

        try {

            byte[] decoded = Base64.decode(chrome);

            String tmpString = new String(decoded);


            byte[] value = Base64.decrypt(decoded);

            chromeD = new String(value);


        } catch (Base64DecoderException e) {

            Logger.e(e.getMessage());


        } catch (Exception e) {

            Logger.e(e.toString());
        }

        lgeD = "";

        try {

            byte[] decoded = Base64.decode(lge);

            String tmpString = new String(decoded);


            byte[] value = Base64.decrypt(decoded);

            lgeD = new String(value);

        } catch (Base64DecoderException e) {

            Logger.e(e.getMessage());


        } catch (Exception e) {

            Logger.e(e.toString());
        }

    }

    private String[] extractUrlParts(String url) {

        String[] data = null;

        Pattern urlPattern = STApplication.getUrlPattern();

        Matcher mc = urlPattern.matcher(url);

        if (mc.matches()) {

            StringBuffer buffer = new StringBuffer();

            buffer.append(mc.group(1) + "://");

            if (mc.group(2).startsWith("wwww.")) {

                buffer.append(mc.group(2).replaceFirst("wwww.", ""));

            } else {

                buffer.append(mc.group(2));
            }

            if (TextUtils.isEmpty(mc.group(3))) {

                buffer.append(":80");

            } else {

                buffer.append(mc.group(3));
            }

            data = new String[]{buffer.toString(), mc.group(5)};
        }

        return data;
    }

    private void showBlockToast() {

        toast.show();

    }

    //최상위 view에 넣는 것, 디바이스 관리자 화면에서 차단 화면 뛰워 주기
    private void showBlockView() {

        synchronized (object) {

            STApplication.applicationHandler.post(new Runnable() {

                @Override
                public void run() {

                    try {

                        if (blockView != null) {

                            return;
                        }

                        blockView = new BlockPasswordLayout(getApplicationContext());

                        mWindowManager.addView(blockView, mLayoutParams);

                    } catch (Exception e) {

                        Logger.e(e.getMessage());
                    }
                }
            });
        }
    }

    private void hideBlockView() {

        STApplication.applicationHandler.post(new Runnable() {

            @Override
            public void run() {

                try {

                    if (blockView == null) {

                        return;
                    }

                    mWindowManager.removeView(blockView);

                    blockView = null;

                } catch (Exception e) {

                    Logger.e(e.getMessage());
                }
            }
        });
    }

    private boolean isKeyboard(String packageName) {

        boolean result = false;

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        List<InputMethodInfo> list = manager.getEnabledInputMethodList();

        for (InputMethodInfo info : list) {

            if (info.getPackageName().equals(packageName)) {

                result = true;

                break;
            }
        }

        return result;

    }

    class CheckThread extends Thread {

        private AccessibilityEvent event;

        public void setAccessibilityEvent(AccessibilityEvent pkg) {
            this.event = pkg;
        }

        public void run() {

            super.run();
            Logger.i("start CheckThread");
            synchronized (this) {
                try {
                    if (event == null) {
                        Logger.i("event is null");
                        return;
                    }
                    //접근성 이벤트 필터링.

                    CharSequence pkgCs = event.getPackageName();

                    if (pkgCs == null || pkgCs.length() == 0) return;

                    String pkg = pkgCs.toString();

                    Logger.d("package name : " + pkg);

                    //새로운 패키지인지 확인.
                    if (!TextUtils.equals(mLastAccessibilityPackage, pkg)) {

                        //이벤트 유효성 검사.
                        if (event.isFullScreen()) {
                            Logger.d("is full screen");
                            if (pkg.startsWith("com.google.android.inputmethod")) {
                                Logger.d("it start with inputmethod");
                                return;
                            }
                            AccessibilityNodeInfo source = event.getSource();
                            if (source != null) {
                                source.getBoundsInParent(mBounds);
                                if (mBounds.top + mBounds.bottom <= 0 || mBounds.left + mBounds.right <= 0) {
                                    Logger.d("bound 1");
                                    return;
                                }
                                source.getBoundsInScreen(mBounds);
                                if (mBounds.top + mBounds.bottom <= 0 || mBounds.left + mBounds.right <= 0) {
                                    Logger.d("bound 2");
                                    return;
                                }
                            }
                            Logger.d("source is null");
                        } else {
                            Logger.d("is not full screen");
                            AccessibilityNodeInfo source = event.getSource();
                            if (source == null) {
                                Logger.d("source is null");
                                return;
                            }
                            int count = source.getChildCount();
                            if (count == 0) {
                                Logger.d("source has no child");
                                return;
                            }
                            if ("com.android.systemui".equals(pkg)) {
                                Logger.d("systemui ");
                                return;
                            }
                            for (int i = 0; i < count; ++i) {
                                if (source.getChild(i) == null) {
                                    Logger.d("getChild null");
                                    return;
                                }
                            }
                        }

                    /*if (mHelper.ONOFF == 1 && (pkg.contains(".mms")
                            || pkg.contains(".contacts")
                            || pkg.contains("com.android.phone")
                            || pkg.contains("com.android.dialer")
                            || pkg.equals("com.android.systemui")
                            || pkg.equals("com.lge.bluetoothsetting")
                            || pkg.equals("com.skt.prod.phone")
                            || pkg.equals("com.sec.imsphone")
                            || pkg.equals("com.sec.ims")
                            || pkg.equals("com.sec.phone")
                            || pkg.equals("com.android.incallui"))) {*/

                    }

                    if (mHelper.ONOFF == 1 && pkg.equals("com.android.incallui")) {
                        LockService.onAccess(pkg);
                        mLastAccessibilityPackage = pkg;
                        return;
                    }
                    //앱 사용 정보 전달.
                    Logger.i("start sendbroadcast");
                    //PendingIntent.getBroadcast(STApplication.applicationContext, 0, new Intent(ACTION_USAGE_CHANGED).putExtra(EXTRA_PACKAGE, pkg), 0).send();
                    sendBroadcast(new Intent(ACTION_USAGE_CHANGED).putExtra(EXTRA_PACKAGE, pkg));
                    Logger.i("end sendbroadcast : " + pkg);

                    mLastAccessibilityPackage = pkg;
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //SWJ 2017-06-04
    private void checkChangePackage(AccessibilityEvent event) {

        synchronized (object1) {
            try {
                if (event == null) {
                    Logger.i("event is null");
                    return;
                }
                //접근성 이벤트 필터링.
                if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    Logger.d("accessbility event type : " + event.getEventType());
                    return;
                }

                CharSequence pkgCs = event.getPackageName();

                if (pkgCs == null || pkgCs.length() == 0) return;

                String pkg = pkgCs.toString();

                Logger.d("package name : " + pkg);

                //새로운 패키지인지 확인.
                if (!TextUtils.equals(mLastAccessibilityPackage, pkg)) {

                    //이벤트 유효성 검사.
                    if (event.isFullScreen()) {
                        Logger.d("is full screen");
                        if (pkg.startsWith("com.google.android.inputmethod")) {
                            Logger.d("it start with inputmethod");
                            return;
                        }
                        AccessibilityNodeInfo source = event.getSource();
                        if (source != null) {
                            source.getBoundsInParent(mBounds);
                            if (mBounds.top + mBounds.bottom <= 0 || mBounds.left + mBounds.right <= 0) {
                                return;
                            }
                            source.getBoundsInScreen(mBounds);
                            if (mBounds.top + mBounds.bottom <= 0 || mBounds.left + mBounds.right <= 0) {
                                return;
                            }
                        }
                    } else {
                        Logger.d("is not full screen");
                        AccessibilityNodeInfo source = event.getSource();
                        if (source == null) {
                            Logger.d("source is null");
                            return;
                        }
                        //int count = source.getChildCount();
                    /*if (count == 0) {
                        Logger.d("source has no child");
                        return;
                    }*/
                        if ("com.android.systemui".equals(pkg)) {
                            Logger.d("systemui ");
                            return;
                        }
                    /*for (int i = 0; i < count; ++i) {
                        if (source.getChild(i) == null) {
                            Logger.d("getChild null");
                            return;
                        }
                    }*/
                    }

                    //앱 사용 정보 전달.
                /*PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USAGE_CHANGED).putExtra(EXTRA_PACKAGE, pkg), 0).send();*/
                    Logger.d("start broadcast");
                    sendBroadcast(new Intent(ACTION_USAGE_CHANGED).putExtra(EXTRA_PACKAGE, pkg));
                    Logger.d("end broadcast");
                    mLastAccessibilityPackage = pkg;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
