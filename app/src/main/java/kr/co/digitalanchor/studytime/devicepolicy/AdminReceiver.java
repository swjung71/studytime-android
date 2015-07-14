package kr.co.digitalanchor.studytime.devicepolicy;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.block.BlockActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;

/**
 * Created by Thomas on 2015-06-24.
 */
public class AdminReceiver extends DeviceAdminReceiver {

    static final String TAG = "DemoDeviceAdminReceiver";

    @Override
    public void onEnabled(Context context, Intent intent) {

        super.onEnabled(context, intent);

        STApplication.putBoolean(StaticValues.SHOW_ADMIN, false);

        DBHelper helper = new DBHelper(context);
        helper.updateAllow(1);

    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);

        STApplication.putBoolean(StaticValues.SHOW_ADMIN, true);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

        return  "스타트 타임을 사용할 수 없게 됩니다.";
    }

}
