package kr.co.digitalanchor.studytime.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Thomas on 2015-07-31.
 */
public class AppIconUploader extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
