package kr.co.digitalanchor.studytime.monitor;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.AdultFileResult;
import kr.co.digitalanchor.studytime.model.Files;
import kr.co.digitalanchor.studytime.model.GetAdultDB;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by user on 2015-08-12.
 */
public class DownloadService extends Service  {

    static final int REQUEST_FILE_LIST = 50001;
    static final int REQUEST_DB_FILE = 50002;
    static final int SELECT_FILE_LIST = 50003;

    int startId;

    DBHelper dbHelper;


    List<Files> list;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case REQUEST_FILE_LIST :

                    requestAdultFile();

                break;

                case REQUEST_DB_FILE:

                    downloadFile();

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

        dbHelper = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.startId = startId;

        handler.sendEmptyMessage(REQUEST_FILE_LIST);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void requestAdultFile() {

        GetAdultDB model = new GetAdultDB();

        String date = dbHelper.getAdultFile();

        if (date != null) {

            model.setDate(date);
        }

        SimpleXmlRequest request = HttpHelper.getAdultFileList(model,
                new Response.Listener<AdultFileResult>() {

                    @Override
                    public void onResponse(AdultFileResult response) {

                        Bundle data = null;

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                data = new Bundle();

                                ArrayList<Files> files = response.getFileName();

                                dbHelper.setAdultFile(response);

                                list = response.getFileName();
                                Logger.d(data.toString());

                                handler.sendEmptyMessage(REQUEST_DB_FILE);

                                break;

                            default:

                                break;
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Logger.e(error.toString());
                    }
                });

        if (request != null) {

            RequestQueue queue = Volley.newRequestQueue(STApplication.applicationContext);

            queue.add(request);
        }
    }

    public void downloadFile() {

        if (list == null || list.size() < 1) {

            return;
        }
    }
}
