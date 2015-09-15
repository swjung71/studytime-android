package kr.co.digitalanchor.studytime.monitor;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-09-15.
 */
public class EventService extends AccessibilityService {

    DBHelper mHelper;

    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();


        mHelper = new DBHelper(getApplicationContext());
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Logger.d("package name " + event.getPackageName().toString()
                + "\nclass name " + event.getClassName().toString()
                + "\nevent type " + event.getEventType());


        Account account = mHelper.getAccountInfo();

        if (account.getIsChild() != 0 || mHelper.getOnOff() != 1) {

            return;
        }

        String packageName = event.getPackageName().toString();

        if ("com.facebook.orca".equals(packageName) && !mHelper.isExcepted(packageName)
                && ("android.widget.FrameLayout".equals(event.getClassName())
                || "android.view.ViewGroup".equals(event.getClassName()))) {


//        if ("com.facebook.orca".equals(packageName) && !mHelper.isExcepted(packageName)) {

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    performGlobalAction(GLOBAL_ACTION_HOME);

                }
            }
        }
    }


    @Override
    public void onInterrupt() {

    }
}
