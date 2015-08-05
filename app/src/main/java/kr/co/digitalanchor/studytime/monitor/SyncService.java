package kr.co.digitalanchor.studytime.monitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.CheckPackageResult;
import kr.co.digitalanchor.studytime.model.ExceptionAppResult;
import kr.co.digitalanchor.studytime.model.LoginModel;
import kr.co.digitalanchor.studytime.model.PackageIDs;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-07-28.
 */
public class SyncService extends Service {

    DBHelper dbHelper;

    @Override
    public void onCreate() {

        super.onCreate();

        Logger.d("onCreate");

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

        Logger.d("requestSync");

        LoginModel model = new LoginModel();

        final Account account = dbHelper.getAccountInfo();

        model.setChildId(account.getID());
        model.setParentId(account.getParentId());

        Request request = HttpHelper.getSyncChildData(model, new Response.Listener<CheckPackageResult>() {

            @Override
            public void onResponse(CheckPackageResult response) {

                Logger.d(response.toString());

                switch (response.getResultCode()) {

                    case HttpHelper.SUCCESS:

                        int isOff = response.getIsOff();

                        dbHelper.updateOnOff(isOff);

                        requestUpdateApp(account.getParentId(), account.getID());

                        sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));

                        // TODO package sync

                    default:

                        stopSelf();

                        break;
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Logger.e(error.toString());

                stopSelf();
            }
        });

        if (request != null) {

            RequestQueue queue = Volley.newRequestQueue(STApplication.applicationContext);

            queue.add(request);
        }
    }

    private void requestUpdateApp(String parentId, String childId) {

        LoginModel model = new LoginModel();

        model.setParentId(parentId);
        model.setChildId(childId);

        SimpleXmlRequest request = HttpHelper.getExceptionApp(model,
                new Response.Listener<ExceptionAppResult>() {
                    @Override
                    public void onResponse(ExceptionAppResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                List<PackageIDs> list = response.getPackages();

                                HashMap<String, Integer> map = new HashMap<String, Integer>();

                                if (list != null && list.size() > 0) {

                                    for (PackageIDs key : list) {

                                        map.put(key.getPackageId(), 1);
                                    }
                                }

                                List<PackageModel> packages = dbHelper.getPackageListExcept();

                                for (PackageModel model : packages) {

                                    if (map.containsKey(model.getPackageId())) {

                                        model.setIsExceptionApp(1);

                                    } else {

                                        model.setIsExceptionApp(0);
                                    }
                                }

                                dbHelper.setExceptPackages(packages);

                                break;

                            default:

                                stopSelf();

                                break;
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.e(error.toString());

                        stopSelf();
                    }
                });

        if (request != null) {

            RequestQueue queue = Volley.newRequestQueue(STApplication.applicationContext);

            queue.add(request);
        }
    }
}
