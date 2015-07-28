package kr.co.digitalanchor.studytime.monitor;

import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.util.TimerTask;

/**
 * Created by Thomas on 2015-07-28.
 */
public class TimerTaskSyncData extends TimerTask {

    Context context;

    public TimerTaskSyncData (Context context) {

        this.context = context;
    }

    @Override
    public void run() {

        Logger.d("TimerTaskSyncData");

        context.startService(new Intent(context, SyncService.class));

    }
}
