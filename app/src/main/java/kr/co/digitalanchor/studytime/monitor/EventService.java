package kr.co.digitalanchor.studytime.monitor;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.Base64;
import kr.co.digitalanchor.utils.Base64DecoderException;

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

    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();


        mHelper = new DBHelper(getApplicationContext());

        setURL();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
/*
        Logger.d("package name " + event.getPackageName().toString()
                + "\nclass name " + event.getClassName().toString()
                + "\nevent type " + event.getEventType());
*/

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

            String url = "";

            if (event.getClassName().toString().equals("android.widget.EditText")
                    && event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {


                AccessibilityNodeInfo source = event.getSource();
                CharSequence typedURL = source.getText();

                String toString = typedURL.toString();


                if (url.equals(toString)) {

                    isSame = true;
                    Logger.i("typeURL and url is same");

                    //performGlobalAction(GLOBAL_ACTION_BACK);
                } else {
                    isSame = false;
                }
            } else if (event.getClassName().toString().equals("android.widget.ScrollView")) {

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

    }
}
