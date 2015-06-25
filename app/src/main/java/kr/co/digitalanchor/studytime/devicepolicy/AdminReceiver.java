package kr.co.digitalanchor.studytime.devicepolicy;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Thomas on 2015-06-24.
 */
public class AdminReceiver extends DeviceAdminReceiver {

    static final String TAG = "DemoDeviceAdminReceiver";

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

        return super.onDisableRequested(context, intent);
    }


}
