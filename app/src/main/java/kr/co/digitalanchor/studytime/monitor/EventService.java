package kr.co.digitalanchor.studytime.monitor;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.database.AdultDBHelper;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.Base64;
import kr.co.digitalanchor.utils.Base64DecoderException;
import kr.co.digitalanchor.utils.MD5ForAdultURL;

/**
 * Created by Thomas on 2015-09-15.
 */
public class EventService extends AccessibilityService {

    private final static String facebook = "Y3BvLmpmaWxqeHl2LmJmcnE=";
    private final static String chrome = "Y3BvLmVzanl3cm4ub3VmZGN2";
    private final static String lge = "bGhn";

    private static String facebookD;
    private static String chromeD;
    private static String lgeD;

    private boolean isSame = false;

    DBHelper mHelper;

    AdultDBHelper mAdultDBHelper;

    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();


        mHelper = new DBHelper(getApplicationContext());

        mAdultDBHelper = new AdultDBHelper(getApplicationContext());

        setURL();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        /*Logger.d("package name " + event.getPackageName().toString()
                + "\nclass name " + event.getClassName().toString()
                + "\nevent type " + event.getEventType());*/


        Account account = mHelper.getAccountInfo();

        if (account.getIsChild() != 0 || mHelper.getOnOff() != 1) {

            return;
        }

        String packageName = event.getPackageName().toString();

        if (facebookD.equals(packageName) && !mHelper.isExcepted(packageName)) {

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    performGlobalAction(GLOBAL_ACTION_HOME);

                }
            }

        } else if (chromeD.equalsIgnoreCase(packageName)) {

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

                    Logger.e("data is null");

                    return;
                }

                if (mAdultDBHelper.isAdultURL(new MD5ForAdultURL().toDigest(data[0]), data[1])) {

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//
//                        performGlobalAction(GLOBAL_ACTION_BACK);
//
//                    }

                    isSame = true;
                    Logger.i("typeURL and url is same");


                } else {
                    isSame = false;
                }



            } else if (event.getClassName().toString().equals("android.widget.ScrollView")
                    && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

                if (isSame) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        performGlobalAction(GLOBAL_ACTION_BACK);

                    }

                    isSame = false;
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

            Logger.i("Decoded : " + tmpString);

            byte[] value = Base64.decrypt(decoded);

            facebookD = new String(value);

            Logger.i("Decrypt :" + facebookD);

        } catch (Base64DecoderException e) {

            Logger.e(e.getMessage());


        } catch (Exception e) {

            Logger.e(e.toString());
        }

        chromeD = "";

        try {

            byte[] decoded = Base64.decode(chrome);

            String tmpString = new String(decoded);

            Logger.i("Decoded : " + tmpString);

            byte[] value = Base64.decrypt(decoded);

            chromeD = new String(value);

            Logger.i("Decrypt :" + chromeD);

        } catch (Base64DecoderException e) {

            Logger.e(e.getMessage());


        } catch (Exception e) {

            Logger.e(e.toString());
        }

        lgeD = "";

        try {

            byte[] decoded = Base64.decode(lge);

            String tmpString = new String(decoded);

            Logger.i("Decoded : " + tmpString);

            byte[] value = Base64.decrypt(decoded);

            lgeD = new String(value);

            Logger.i("Decrypt :" + lgeD);

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
}
