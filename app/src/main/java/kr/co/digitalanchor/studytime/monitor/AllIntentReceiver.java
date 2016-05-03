package kr.co.digitalanchor.studytime.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-06-29.
 * B 서비스가 죽었을 때 부르는 클래스
 */
public class AllIntentReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    try {
      DBHelper helper = new DBHelper(STApplication.applicationContext);

      Account account = helper.getAccountInfo();

      if (account.getIsChild() == 0) {

        context.startService(new Intent(STApplication.applicationContext, B.class));
      }

    } catch (Exception e) {

      Logger.e(e.getMessage());
    }
  }
}
