package kr.co.digitalanchor.studytime.devicepolicy;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.block.BlockActivity;

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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
        super.onLockTaskModeEntering(context, intent, pkg);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        super.onPasswordExpiring(context, intent);
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        super.onLockTaskModeExiting(context, intent);
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
    }

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        super.onProfileProvisioningComplete(context, intent);

    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

        return  "스타트 타임을 사용할 수 없게 됩니다.";
    }

}
