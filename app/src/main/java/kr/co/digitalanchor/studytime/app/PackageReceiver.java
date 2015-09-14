package kr.co.digitalanchor.studytime.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.monitor.MonitorService;

/**
 * Created by Thomas on 2015-07-30.
 */
public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DBHelper helper = new DBHelper(context);

        Account account = helper.getAccountInfo();

        Logger.d("isChild " + account.getIsChild()  + "  OnOff " + helper.getOnOff());

        if (TextUtils.isEmpty(account.getID())) {

            return;
        }

        if (account.getIsChild() == 1) {

            return;
        }

        String packageName = intent.getData().getSchemeSpecificPart();

        Logger.d(intent.getAction() + " package :" + packageName);

        Intent broadcastIntent = new Intent();

        switch (intent.getAction()) {

            case Intent.ACTION_PACKAGE_REMOVED:

                if (context.getPackageName().equalsIgnoreCase(packageName)) {

                    return;
                }

                broadcastIntent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_REMOVED);

                break;

            case Intent.ACTION_PACKAGE_ADDED:
                // install

                if (context.getPackageName().equalsIgnoreCase(packageName)) {

                    return;
                }

                broadcastIntent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_ADDED);

                break;

            case Intent.ACTION_PACKAGE_REPLACED:
                // update

//                if (context.getPackageName().equalsIgnoreCase(packageName)) {
//
//                    context.startService(new Intent(context, MonitorService.class));
//
//                    broadcastIntent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_SYNC);
//
//                } else {
//
//                    broadcastIntent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_REPLACED);
//                }

                break;
        }

        if (broadcastIntent != null) {

            broadcastIntent.putExtra(StaticValues.PACKAGE_NAME, packageName);
            broadcastIntent.setClass(context, AppManageService.class);

            context.startService(broadcastIntent);
        }
    }
}
