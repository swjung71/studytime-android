package kr.co.digitalanchor.studytime.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-06-29.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    /**
     * The Constant REPEAT_TIME.
     */
    private static final long REPEAT_TIME = 1000 * 3; // 3s

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            //DBHelper helper = new DBHelper(context);
            DBHelper helper = DBHelper.getInstance(context);

            Account account = helper.getAccountInfo();

            Logger.d("isChild " + account.getIsChild() + "  OnOff " + helper.getOnOff());

            if (account.getIsChild() == 0) {

                context.startService(new Intent(context, B.class));
                context.startService(new Intent(context, LockService.class));
/*
                Intent MonitorServiceIntent = new Intent(context, MonitorService.class);

                AlarmManager service = (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);

                PendingIntent pending = PendingIntent.getService(context, 0,
                        MonitorServiceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                Calendar cal = Calendar.getInstance();
                // Start 5 seconds after boot completed
                cal.add(Calendar.SECOND, 5);

                // Fetch every x seconds
                // InexactRepeating allows Android to optimize the energy
                // consumption
                service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(), REPEAT_TIME, pending);
*/
            }

        } else {

            Logger.d(intent.toString());
        }
    }
}
