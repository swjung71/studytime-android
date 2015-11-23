package kr.co.digitalanchor.studytime.monitor;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import kr.co.digitalanchor.studytime.model.GCMUpdate;
import kr.co.digitalanchor.studytime.model.GeneralResult;
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

  private final int REQUEST_SYNC = 50001;
  private final int REQUEST_SYNC_GCM = 50002;

  DBHelper dbHelper;

  Handler handler;

  @Override
  public void onCreate() {

    super.onCreate();

    Logger.d("onCreate");

    dbHelper = new DBHelper(STApplication.applicationContext);

    handler = new Handler() {

      @Override
      public void handleMessage(Message msg) {

        switch (msg.what) {

          case REQUEST_SYNC:

            requestSync();

            break;

          case REQUEST_SYNC_GCM:

            getUpdateGCM();

            break;
        }
      }
    };

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    handler.sendEmptyMessage(REQUEST_SYNC);

    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {

    startService(new Intent(getApplicationContext(), B.class));

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
    model.setDevNum(STApplication.getDeviceNumber());


    Request request = HttpHelper.getSyncChildData(model, new Response.Listener<CheckPackageResult>() {

      @Override
      public void onResponse(CheckPackageResult response) {

        Logger.d(response.toString());

        switch (response.getResultCode()) {

          case HttpHelper.SUCCESS:

            int isOff = response.getIsOff();

            switch (isOff) {

              case 3:

                dbHelper.updateExpired("Y");

                break;

              case 4:

                STApplication.resetApplication();

                break;

              default:

                dbHelper.updateExpired("N");
                dbHelper.updateOnOff(isOff);

                break;

            }

            requestUpdateApp(account.getParentId(), account.getID());

            sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));

            break;

          default:

            break;
        }

        handler.sendEmptyMessage(REQUEST_SYNC_GCM);
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

  private void getUpdateGCM() {

    Account account = dbHelper.getAccountInfo();

    GCMUpdate model = new GCMUpdate();

    model.setGCM(STApplication.getRegistrationId());
    model.setId(account.getID());
    model.setIsChild((account.getIsChild() == 0) ? 1 : 0);
    model.setVersion(STApplication.getAppVersionName());

    SimpleXmlRequest request = HttpHelper.getUpdate(model,
        new Response.Listener<GeneralResult>() {
          @Override
          public void onResponse(GeneralResult response) {

          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

          }
        });

    if (request != null) {

      RequestQueue queue = Volley.newRequestQueue(STApplication.applicationContext);

      queue.add(request);
    }
  }
}
