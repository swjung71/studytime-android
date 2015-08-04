package kr.co.digitalanchor.studytime.monitor;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

import java.util.TimerTask;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.block.BlockPasswordLayout;
import kr.co.digitalanchor.studytime.database.DBHelper;

/**
 * Created by Thomas on 2015-07-01.
 */
public class TimerTaskPreventUncheckDeviceAdmin extends TimerTask {

    Context context;

    ActivityManager mActivityManager;

    WindowManager windowManager;

    WindowManager.LayoutParams layoutParams;

    DBHelper helper;

    Object object;

    View blockView = null;

    public TimerTaskPreventUncheckDeviceAdmin(Context context) {

        this.context = context;

        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        helper = new DBHelper(context);

        // additional
        object = new Object();

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

    }

    @Override
    public void run() {


        if (helper.isAllow() == 1
                && STApplication.getBoolean(StaticValues.SHOW_ADMIN, false)
                && isblock()) {

                showBlockView();

        } else {

            hideBlockView();
        }
    }

    private void hideBlockView() {

        synchronized (object) {

            STApplication.applicationHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (blockView == null) {

                        return;
                    }

                    windowManager.removeView(blockView);

                    blockView = null;
                }
            });
        }


    }

    private void showBlockView() {

        synchronized (object) {

            STApplication.applicationHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (blockView != null) {

                        return;
                    }

                    blockView = new BlockPasswordLayout(context);

                    windowManager.addView(blockView, layoutParams);
                }
            });
        }
    }


    private boolean isblock() {

        String packageName = null;

        boolean isAdmin = false;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            packageName = mActivityManager.getRunningAppProcesses().get(0).processName;

        } else {

            ComponentName info = mActivityManager.getRunningTasks(1).get(0).topActivity;

            packageName = info.getPackageName();

            Logger.d(info.getClassName());

            isAdmin = info.getClassName().equalsIgnoreCase("com.android.settings.DeviceAdminAdd");

        }

        return !(context.getPackageName().equals(packageName) || isAdmin);
    }

}
