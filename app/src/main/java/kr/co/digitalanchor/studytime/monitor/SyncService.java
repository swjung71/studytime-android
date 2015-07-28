package kr.co.digitalanchor.studytime.monitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.CheckPackageResult;
import kr.co.digitalanchor.studytime.model.LoginModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-07-28.
 */
public class SyncService extends Service {

    DBHelper dbHelper;

    @Override
    public void onCreate() {

        super.onCreate();

        dbHelper = new DBHelper(STApplication.applicationContext);

        requestSync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void requestSync() {

        LoginModel model = new LoginModel();

        Account account = dbHelper.getAccountInfo();

        model.setChildId(account.getID());
        model.setParentId(account.getParentId());

        Request request = HttpHelper.getSyncChildData(model, new Response.Listener<CheckPackageResult>() {

            @Override
            public void onResponse(CheckPackageResult response) {

                switch (response.getResultCode()) {

                    case HttpHelper.SUCCESS:

                        int isOff = response.getIsOff();

                        dbHelper.updateOnOff(isOff);

                    default:

                        stopSelf();

                        break;
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                stopSelf();
            }
        });

    }
}
