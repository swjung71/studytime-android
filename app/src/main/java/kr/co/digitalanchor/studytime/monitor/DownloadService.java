package kr.co.digitalanchor.studytime.monitor;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * Created by user on 2015-08-12.
 */
public class DownloadService extends Service {

    static final int REQUEST_FILE_LIST = 50001;
    static final int REQUEST_DB_FILE = 50002;
    static final int SELECT_FILE_LIST = 50003;

    int startId;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case REQUEST_FILE_LIST :

                break;

                case REQUEST_DB_FILE:

                break;

                case SELECT_FILE_LIST:

                break;

                default:

                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.startId = startId;

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
