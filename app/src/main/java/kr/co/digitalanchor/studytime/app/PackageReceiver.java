package kr.co.digitalanchor.studytime.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

        switch (intent.getAction()) {

            case Intent.ACTION_PACKAGE_REMOVED:
                // delete

                break;

            case Intent.ACTION_PACKAGE_ADDED:
                // install

                break;

            case Intent.ACTION_PACKAGE_REPLACED:
                // update

                break;

            default:

                break;
        }
    }
}
