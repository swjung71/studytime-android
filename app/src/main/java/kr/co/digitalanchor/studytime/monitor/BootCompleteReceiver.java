package kr.co.digitalanchor.studytime.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.block.BlockActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-06-29.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            DBHelper helper = new DBHelper(context);

            Account account = helper.getAccountInfo();

            Logger.d("isChild " + account.getIsChild()  + "  OnOff " + helper.getOnOff());

            if (account.getIsChild() == 0) {

                context.startService(new Intent(context, MonitorService.class));
            }

        } else {

            Logger.d(intent.toString());
        }
    }
}
