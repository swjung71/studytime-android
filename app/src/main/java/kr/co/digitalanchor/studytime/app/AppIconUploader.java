package kr.co.digitalanchor.studytime.app;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.MultipartRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.IconModel;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.utils.ImageUtils;

/**
 * Created by Thomas on 2015-07-31.
 */
public class AppIconUploader extends Service {

    private final int REQUEST_UPLOAD_IMAGE = 50001;

    DBHelper dbHelper;

    RequestQueue requestQueue;

    List<PackageModel> list;

    PackageManager packageManager;

    Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d("onCreate");

        dbHelper = new DBHelper(getApplicationContext());

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        list = dbHelper.getPackageListNoIcon();

        packageManager = getPackageManager();

        requestUploadIcon();

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {

                    case REQUEST_UPLOAD_IMAGE:

                        requestUploadIcon();

                        break;
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void requestUploadIcon() {

        if (list.size() < 1) {

            stopSelf();
        }

        final PackageModel packageModel = list.get(0);

        IconModel model = new IconModel();

        model.setIconHash(packageModel.getHash());
        model.setPackageVersion(packageModel.getPackageVersion());
        model.setHash(packageModel.getHash());
        model.setIsUpdate(1);

        Drawable drawable = null;

        try {

            drawable = packageManager.getApplicationIcon(packageModel.getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        String path = ImageUtils.saveBitmap(getApplicationContext(), model.getIconHash(), drawable);

        if (path == null) {

            return;
        }

        File file = new File(path);

        if (!file.exists()) {

            return;
        }

        MultipartRequest request = HttpHelper.getUploadIcon(model, file, new Response.Listener() {

            @Override
            public void onResponse(Object response) {

                list.remove(packageModel);

                mHandler.sendEmptyMessage(REQUEST_UPLOAD_IMAGE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }
}
