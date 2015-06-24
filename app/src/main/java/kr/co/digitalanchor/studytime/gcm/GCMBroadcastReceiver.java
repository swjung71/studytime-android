package kr.co.digitalanchor.studytime.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.igaworks.adpopcorn.activity.popup.C;

/**
 * Created by Thomas on 2015-06-16.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());

        startWakefulService(context, intent.setComponent(comp));

        setResultCode(Activity.RESULT_OK);

    }
}
