package kr.co.digitalanchor.studytime.gcm;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Thomas on 2015-06-16.
 */
public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;

    public GCMIntentService() {

        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


    }
}
