package kr.co.digitalanchor.studytime.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-06-29.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            DBHelper helper = new DBHelper(STApplication.applicationContext);

            Account account = helper.getAccountInfo();

            if (account.getIsChild() == 0 && helper.getOnOff() == 1) {

                context.stopService(new Intent(STApplication.applicationContext, MonitorService.class));
            }
        }
    }
}
