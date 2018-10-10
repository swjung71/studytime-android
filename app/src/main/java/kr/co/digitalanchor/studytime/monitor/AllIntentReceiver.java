package kr.co.digitalanchor.studytime.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.LocalServerSocket;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

import static kr.co.digitalanchor.studytime.StaticValues.ACTION_SERVICE_START;
import static kr.co.digitalanchor.utils.Intents.ACTION_ATTACH_ALARM;
import static kr.co.digitalanchor.utils.Intents.ACTION_DETACH_ALARM;

/**
 * Created by Thomas on 2015-06-29.
 * B 서비스가 죽었을 때 부르는 클래스
 */
public class AllIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            //DBHelper helper = new DBHelper(STApplication.applicationContext);
            DBHelper helper = DBHelper.getInstance(STApplication.applicationContext);

            Account account = helper.getAccountInfo();

            if (account.getIsChild() == 0) {

                if (intent.getAction().equals(ACTION_SERVICE_START)) {
                    Intent intent1 = new Intent(STApplication.applicationContext, B.class);
                    intent1.setAction(ACTION_ATTACH_ALARM);
                    context.startService(intent1);

                    Intent intent2 = new Intent(STApplication.applicationContext, LockService.class);
                    if(DBHelper.getInstance(context).getOnOff() == 1){
                        intent2.setAction(ACTION_ATTACH_ALARM);
                    }else {
                        intent2.setAction(ACTION_DETACH_ALARM);
                    }

                    context.startService(intent2);
                    Logger.i("AllIntentReceiver Start LocalService ");

                } else {
                    context.startService(new Intent(STApplication.applicationContext, B.class));
                    context.startService(new Intent(STApplication.applicationContext, LockService.class));
                    Logger.i("AllIntentReceiver Start LocalService ");
                }
            }

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }
}
