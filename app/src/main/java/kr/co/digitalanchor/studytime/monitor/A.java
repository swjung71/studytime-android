package kr.co.digitalanchor.studytime.monitor;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
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
import kr.co.digitalanchor.utils.MD5ForAdultURL;

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

    ActivityManager mActivityManager;

    WindowManager mWindowManager;

    WindowManager.LayoutParams mLayoutParams;


    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();

        mHelper = new DBHelper(getApplicationContext());

        mAdultDBHelper = new AdultDBHelper(getApplicationContext());

        mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);


        setURL();

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
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Logger.d("package name " + event.getPackageName().toString()
                + "\nclass name " + event.getClassName().toString()
                + "\nevent type " + event.getEventType());

        Account account = mHelper.getAccountInfo();

        if (account.getIsChild() != 0) {

            return;
        }

        String packageName = event.getPackageName().toString();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (mHelper.isAllow() == 1 && STApplication.getBoolean(StaticValues.SHOW_ADMIN, false)
                    && !packageName.equals("com.android.settings.DeviceAdminAdd")
                    && !isKeyboard(packageName)
                    && !getApplicationContext().getPackageName().equals(packageName)) {

                showBlockView();

                return;

            } else {

                hideBlockView();

            }
        }

        if (chromeD.equalsIgnoreCase(packageName)) {

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (mHelper.getOnOff() == 1
                    && !mHelper.isExcepted(packageName)
                    && !packageName.equals("com.android.systemui")
                    && !isKeyboard(packageName)) {

                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        performGlobalAction(GLOBAL_ACTION_HOME);

                        showBlockToast();

                    }
                }
            }
        } else {

            if (mHelper.getOnOff() == 1
                    && facebookD.equals(packageName)
                    && !mHelper.isExcepted(packageName)) {

                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        performGlobalAction(GLOBAL_ACTION_HOME);

                        showBlockToast();

                    }
                }
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
}
