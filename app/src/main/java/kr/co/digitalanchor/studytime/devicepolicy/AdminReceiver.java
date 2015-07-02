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

        Logger.d(TAG + "onEnabled");

        Toast.makeText(context, TAG + " onEnabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Intent block = new Intent(context, BlockActivity.class);
        block.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(block);
        Logger.d(TAG + "onReceive");
        Toast.makeText(context, TAG + " onReceive", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
        super.onLockTaskModeEntering(context, intent, pkg);
        Logger.d(TAG + "onLockTaskModeEntering");
        Toast.makeText(context, TAG + " onLockTaskModeEntering", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Logger.d(TAG + "onPasswordChanged");
        Toast.makeText(context, TAG + " onPasswordChanged", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Logger.d(TAG + "onPasswordFailed");
        Toast.makeText(context, TAG + " onPasswordFailed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Logger.d(TAG + "onDisabled");
        Toast.makeText(context, TAG + " onDisabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        super.onPasswordExpiring(context, intent);
        Logger.d(TAG + "onPasswordExpiring");
        Toast.makeText(context, TAG + " onPasswordExpiring", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        super.onLockTaskModeExiting(context, intent);
        Logger.d(TAG + "onDisableRequested");
        Toast.makeText(context, TAG + " onLockTaskModeExiting", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Logger.d(TAG + "onPasswordSucceeded");
        Toast.makeText(context, TAG + " onPasswordSucceeded", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        super.onProfileProvisioningComplete(context, intent);

        Logger.d(TAG + "onProfileProvisioningComplete");

        Toast.makeText(context, TAG + " onProfileProvisioningComplete", Toast.LENGTH_LONG).show();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {


        return "안돼요";
    }

}
