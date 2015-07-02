package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.util.TimerTask;

import kr.co.digitalanchor.studytime.block.BlockPasswordActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-07-01.
 */
public class TimerTaskPreventUncheckDeviceAdmin extends TimerTask {

    Context context;

    ActivityManager manager;

    DBHelper helper;

    public TimerTaskPreventUncheckDeviceAdmin(Context context) {

        this.context = context;

        manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        helper = new DBHelper(context);

    }

    @Override
    public void run() {

        if (helper.isAllow() != 1) {

            return;
        }

        String topActivity = manager.getRunningTasks(1).get(0).topActivity.getClassName();

        Logger.d(topActivity);

        if (topActivity.equals("com.android.settings.DeviceAdminAdd")) {

            Intent intent = new Intent(context, BlockPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

}
