package kr.co.digitalanchor.studytime.monitor;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.orhanobut.logger.Logger;

/**
 * Created by Thomas on 2015-09-15.
 */
public class EventService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Logger.d("package name " + event.getPackageName().toString()
                + "\nclass name " + event.getClassName().toString()
                + "\nevent type " + event.getEventType());

    }

    @Override
    public void onInterrupt() {

    }
}
