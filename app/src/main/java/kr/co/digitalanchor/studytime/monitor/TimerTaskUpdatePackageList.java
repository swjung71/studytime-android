package kr.co.digitalanchor.studytime.monitor;

import android.content.Context;
import android.content.Intent;

import java.util.TimerTask;

import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.app.AppManageService;

/**
 * Created by Thomas on 2015-08-04.
 */
public class TimerTaskUpdatePackageList extends TimerTask {

    Context context;

    public TimerTaskUpdatePackageList(Context context) {

        this.context = context;
    }

    @Override
    public void run() {

        Intent intent = new Intent(context, AppManageService.class);
        intent.putExtra(StaticValues.ACTION_NAME, StaticValues.ACTION_PACKAGE_SYNC);

        context.startService(intent);
    }
}
