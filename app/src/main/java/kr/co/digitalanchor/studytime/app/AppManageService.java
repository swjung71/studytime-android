package kr.co.digitalanchor.studytime.app;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import java.util.HashMap;
import java.util.List;

import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.AllPackage;
import kr.co.digitalanchor.studytime.model.AllPackageResult;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.AndroidUtils;
import kr.co.digitalanchor.utils.MD5;

/**
 * Created by Thomas on 2015-07-30.
 */
public class AppManageService extends Service {

    DBHelper dbHelper;

    RequestQueue requestQueue;

    boolean isFirst;

    @Override
    public void onCreate() {

        super.onCreate();

        dbHelper = new DBHelper(getApplicationContext());

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        isFirst = (dbHelper.getPackageListSize() < 1);

        List<PackageModel> packageList;

        if (isFirst) {

            packageList = getAppListFromDevice();

        } else {

            packageList = getAbsenceAppList();
        }

        dbHelper.addApplications(packageList);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {

            default:
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 처음 한번 호출
     *
     * @return
     */
    private List<PackageModel> getAppListFromDevice() {

        PackageManager manager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        List<PackageModel> packageModels = new ArrayList<>();

        for (ResolveInfo r : apps) {

            PackageInfo packageInfo = null;

            try {

                packageInfo = manager.getPackageInfo(r.activityInfo.packageName, 0);

            } catch (PackageManager.NameNotFoundException e) {

                continue;
            }

            if (packageInfo == null) {

                continue;
            }

            PackageModel model = new PackageModel();

            model.setPackageName(packageInfo.packageName);
            model.setHash(MD5.getHash(packageInfo.packageName));
            model.setLabelName(packageInfo.applicationInfo.loadLabel(manager).toString());
            model.setPackageVersion(packageInfo.versionName);
            model.setIsExceptionApp(0);
            model.setIconHash(MD5.getHash(packageInfo.packageName + packageInfo.versionName));

            model.setTimestamp(AndroidUtils.convertTimeStamp4Chat(packageInfo.firstInstallTime));

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                model.setIsDefaultApp(1);

            } else {

                model.setIsDefaultApp(0);
            }

            packageModels.add(model);
        }

        return packageModels;
    }

    /**
     * 업데이트 내용
     *
     * @return
     */
    private List<PackageModel> getAbsenceAppList() {

        PackageManager manager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        List<PackageModel> packageModels = new ArrayList<>();

        HashMap<String, PackageModel> hashMap = dbHelper.getPackageStateList();

        for (ResolveInfo r : apps) {

            PackageInfo packageInfo = null;

            try {

                packageInfo = manager.getPackageInfo(r.activityInfo.packageName, 0);

            } catch (PackageManager.NameNotFoundException e) {

                continue;
            }

            if (packageInfo == null) {

                continue;
            }

            if (hashMap.containsKey(packageInfo.packageName)) {

                PackageModel model = hashMap.get(packageInfo.packageName);

                if (model.getPackageName().equals(packageInfo.versionName)) {

                    hashMap.remove(packageInfo.packageName);

                    continue;

                } else {

                    PackageModel packageModel = new PackageModel();

                    packageModel.setPackageName(packageInfo.packageName);
                    packageModel.setHash(MD5.getHash(packageInfo.packageName));
                    packageModel.setLabelName(packageInfo.applicationInfo.loadLabel(manager).toString());
                    packageModel.setPackageVersion(packageInfo.versionName);
                    packageModel.setIsExceptionApp(model.getIsExceptionApp());
                    packageModel.setIconHash(MD5.getHash(packageInfo.packageName + packageInfo.versionName));
                    packageModel.setTimestamp(AndroidUtils.convertTimeStamp4Chat(packageInfo.lastUpdateTime));
                    packageModel.setIsDefaultApp(model.getIsDefaultApp());

                    packageModels.add(packageModel);
                }
            }
        }

        for (String key : hashMap.keySet()) {

            PackageModel model = hashMap.get(key);

            if (model.getState() == 1) {

                continue;
            }

            model.setState(1);
            model.setChanged(1);

            packageModels.add(model);
        }

        return packageModels;
    }

    /**
     * Package 목록 보내기
     */
    private void requestAddApps() {

        Account account = dbHelper.getAccountInfo();

        List<PackageModel> packages = dbHelper.getPackageList();

        AllPackage model = new AllPackage();

        model.setPackages(packages);
        model.setChildId(account.getID());
        model.setParentId(account.getParentId());

        SimpleXmlRequest request = HttpHelper.getAddAppList(model,
                new Response.Listener<AllPackageResult>() {
                    @Override
                    public void onResponse(AllPackageResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                // TODO local db update
                                updateLocalDB(response.getPackages());

                                break;

                            default:

                                Logger.e(response.getResultMessage());

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

            requestQueue.add(request);
        }
    }

    private void requestUpdateApps() {

        Account account = dbHelper.getAccountInfo();

        List<PackageModel> packages = dbHelper.getPackageList();

        AllPackage model = new AllPackage();

        model.setPackages(packages);
        model.setChildId(account.getID());
        model.setParentId(account.getParentId());

        SimpleXmlRequest request = HttpHelper.getUpdateAppList(model,
                new Response.Listener<AllPackageResult>() {
                    @Override
                    public void onResponse(AllPackageResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                updateLocalDB(response.getPackages());

                                break;

                            default:

                                Logger.e(response.getResultMessage());

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

            requestQueue.add(request);
        }
    }

    private void updateLocalDB(List<PackageModel> packages) {

        for (PackageModel model : packages) {

            dbHelper.updateApplicationAfterReg(model.getPackageName(), model.getPackageId(),
                    model.getHasIconDB());
        }
    }
}
