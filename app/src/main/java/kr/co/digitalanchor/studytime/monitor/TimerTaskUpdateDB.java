package kr.co.digitalanchor.studytime.monitor;

import android.content.Context;
import android.content.Intent;

import java.util.TimerTask;

/**
 * Created by user on 2015-08-12.
 */
public class TimerTaskUpdateDB extends TimerTask {

    Context context;

    public TimerTaskUpdateDB(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        context.startService(new Intent(context, DownloadService.class));
    }
}
