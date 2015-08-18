package kr.co.digitalanchor.studytime.app;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

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
import kr.co.digitalanchor.utils.MD5;

/**
 * Created by Thomas on 2015-07-31.
 */
public class AppIconUploader extends Service {

    private final int REQUEST_UPLOAD_IMAGE = 50001;
    private final int REQUEST_MAKE_IMAGE = 50002;

    DBHelper dbHelper;

    RequestQueue requestQueue;

    List<PackageModel> list;

    PackageManager packageManager;

    Handler mHandler;

    File file;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d("onCreate");

        dbHelper = new DBHelper(getApplicationContext());

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        packageManager = getPackageManager();

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {

                    case REQUEST_UPLOAD_IMAGE:

                        requestUploadIcon();

                        break;

                    case REQUEST_MAKE_IMAGE:

                        requestMakeImage();

                        break;
                }
            }
        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        list = dbHelper.getPackageListNoIcon();

        mHandler.sendEmptyMessage(REQUEST_MAKE_IMAGE);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void requestMakeImage() {

        if (list.size() < 1) {

            return;
        }

        final PackageModel packageModel = list.get(0);

        new ImageTask().execute(packageModel.getPackageName());
    }

    private void requestUploadIcon() {

        if (list.size() < 1) {

            return;
        }

        final PackageModel packageModel = list.get(0);

        IconModel model = new IconModel();

        model.setPackageVersion(packageModel.getPackageVersion());
        model.setHash(MD5.getHash(packageModel.getPackageName()));
        model.setIconHash(packageModel.getHash());
        model.setIsUpdate(1);

        file = new File(getFileStreamPath(model.getIconHash()).getPath());

        MultipartRequest request = HttpHelper.getUploadIcon(model, file, new Response.Listener() {

            @Override
            public void onResponse(Object response) {

                list.remove(packageModel);

                mHandler.sendEmptyMessage(REQUEST_MAKE_IMAGE);

                if (file != null) {

                    file.delete();

                    file = null;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                list.remove(packageModel);

                mHandler.sendEmptyMessage(REQUEST_MAKE_IMAGE);

                if (file != null) {

                    file.delete();

                    file = null;
                }
            }
        });

        requestQueue.add(request);
    }

    class ImageTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            if (params.length < 1) {

                return null;
            }

            Drawable drawable = null;

            try {

                PackageInfo info = packageManager.getPackageInfo(params[0], 0);

                drawable = info.applicationInfo.loadIcon(packageManager);

            } catch (PackageManager.NameNotFoundException e) {

                return null;
            }

            String path = ImageUtils.saveBitmap(getApplicationContext(),
                    MD5.getHash(params[0]), drawable);

            if (path == null) {

                return null;
            }

            file = new File(path);

            if (!file.exists()) {

                return null;
            }

            return path;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            if (TextUtils.isEmpty(s)) {

                return;

            } else {

                mHandler.sendEmptyMessage(REQUEST_UPLOAD_IMAGE);
            }
        }
    }
}
