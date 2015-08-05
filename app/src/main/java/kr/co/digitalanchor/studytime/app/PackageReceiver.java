package kr.co.digitalanchor.studytime.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.StaticValues;

/**
 * Created by Thomas on 2015-07-30.
 */
public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String packageName = intent.getData().getSchemeSpecificPart();

        if (context.getPackageName().equalsIgnoreCase(packageName)) {

            return;
        }

        Logger.d(intent.getAction() + " package :" + packageName );

        Intent broadcastIntent = new Intent();

        switch (intent.getAction()) {

            case Intent.ACTION_PACKAGE_REMOVED:

                broadcastIntent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_REMOVED);

                break;

            case Intent.ACTION_PACKAGE_ADDED:
                // install

                broadcastIntent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_ADDED);

                break;

            case Intent.ACTION_PACKAGE_REPLACED:
                // update

                broadcastIntent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_REPLACED);

                break;
        }

        if (broadcastIntent != null) {

            broadcastIntent.putExtra(StaticValues.PACKAGE_NAME, packageName);
            broadcastIntent.setClass(context, AppManageService.class);

            context.startService(broadcastIntent);
        }
    }
}
