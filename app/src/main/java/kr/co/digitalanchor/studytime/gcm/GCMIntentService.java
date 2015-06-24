package kr.co.digitalanchor.studytime.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Thomas on 2015-06-16.
 */
public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;

    final Handler handler;

    public GCMIntentService() {

        super("GCMIntentService");

        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)
                || GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

            GCMBroadcastReceiver.completeWakefulIntent(intent);

            return;
        }

        Bundle bundle = intent.getExtras();

        if (bundle.isEmpty()) {

            GCMBroadcastReceiver.completeWakefulIntent(intent);

            return;
        }
    }
}
