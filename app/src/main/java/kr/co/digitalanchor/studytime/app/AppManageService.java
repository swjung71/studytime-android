package kr.co.digitalanchor.studytime.app;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.AllPackage;
import kr.co.digitalanchor.studytime.model.AllPackageResult;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.model.PackageResult;
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

    PackageManager packageManager;

    private Handler mHandler;

    private boolean isRun = false;

    @Override
    public void onCreate() {

        super.onCreate();

        dbHelper = new DBHelper(getApplicationContext());

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        packageManager = getPackageManager();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String packageName = null;
        PackageModel model = null;
        List<PackageModel> list = null;

        String action = intent.getStringExtra(StaticValues.ACTION_NAME);

        Logger.d("onStartCommand()" + action);

        if (TextUtils.isEmpty(action)) {

            return START_NOT_STICKY;
        }

        if (!AndroidUtils.isNetworkAvailable(getApplicationContext())) {

            return START_NOT_STICKY;
        }

        switch (action) {

            case StaticValues.ACTION_PACKAGE_SYNC:

                list = getAbsenceAppList();

                requestUpdateApps(list);

                break;

            case StaticValues.ACTION_PACKAGE_ADDED:

                Logger.d(StaticValues.ACTION_PACKAGE_ADDED);

                packageName = intent.getStringExtra(StaticValues.PACKAGE_NAME);

                model = getPackageInfo(packageName);
                model.setState(0);

                dbHelper.addApplication(model.getPackageName(), model.getHash(), model.getLabelName(),
                        model.getPackageVersion(), model.getTimestamp(), model.getIsExceptionApp(),
                        model.getIsDefaultApp(), model.getIconHash(), 0, 0);

                list = new ArrayList<>();

                list.add(model);

                requestUpdateApps(list);

                break;

            case StaticValues.ACTION_PACKAGE_REMOVED:

                Logger.d(StaticValues.ACTION_PACKAGE_REMOVED);

                packageName = intent.getStringExtra(StaticValues.PACKAGE_NAME);

                model = dbHelper.getPackage(packageName);

                model.setState(1);

                dbHelper.deleteApplication(packageName);

                list = new ArrayList<>();

                list.add(model);

                requestUpdateApps(list);

                break;

            case StaticValues.ACTION_PACKAGE_REPLACED:

            /*
                Logger.d("inner switch");

                packageName = intent.getStringExtra(StaticValues.PACKAGE_NAME);

                model = getPackageInfo(packageName);

                dbHelper.addApplication(model.getPackageName(), model.getHash(), model.getLabelName(),
                        model.getPackageVersion(), model.getTimestamp(), model.getIsExceptionApp(),
                        model.getIsDefaultApp(), model.getIconHash(), 2, 0);

                list = new ArrayList<>();

                list.add(model);

                requestUpdateApps(list);
             */

                break;

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
     * 업데이트 내용
     *
     * @return
     */
    private List<PackageModel> getAbsenceAppList() {

        PackageManager manager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        List<PackageModel> packageModels = new ArrayList<>();

        HashMap<String, PackageModel> hashMap = dbHelper.getPackageStateList();

        Logger.d("Size " + apps.size() + " : " + hashMap.size());

        for (ResolveInfo r : apps) {

            PackageInfo packageInfo = null;

            try {

                packageInfo = manager.getPackageInfo(r.activityInfo.packageName, 0);

            } catch (PackageManager.NameNotFoundException e) {

                continue;
            }

            if (packageInfo == null) {

                continue;

            } else if (packageInfo.packageName.equals(getApplicationContext().getPackageName())
                    || packageInfo.packageName.contains(".mms")
                    || packageInfo.packageName.contains(".contacts")
                    || packageInfo.packageName.contains("com.android.phone")
                    || packageInfo.packageName.contains("com.android.settings")
                    || packageInfo.packageName.contains("com.android.dialer")) {

                continue;

            } else if (isLauncher(packageInfo.packageName)){

                continue;
            }

            if (!hashMap.containsKey(packageInfo.packageName)) {

                PackageModel packageModel = new PackageModel();

                packageModel.setPackageName(packageInfo.packageName);
                packageModel.setHash(MD5.getHash(packageInfo.packageName));
                packageModel.setLabelName(packageInfo.applicationInfo.loadLabel(manager).toString());
                packageModel.setPackageVersion(packageInfo.versionName);
                packageModel.setIsExceptionApp(0);
                packageModel.setIconHash(MD5.getHash(packageInfo.packageName + packageInfo.versionName));
                packageModel.setTimestamp(AndroidUtils.convertCurrentTime4Chat(packageInfo.lastUpdateTime));

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                    packageModel.setIsDefaultApp(1);

                } else {

                    packageModel.setIsDefaultApp(0);

                }

                packageModel.setState(0);

                packageModels.add(packageModel);

                continue;

            } else {

                PackageModel model = hashMap.get(packageInfo.packageName);

                if (model.getPackageVersion().equals(packageInfo.versionName)) {


                } else {

                    model.setPackageVersion(packageInfo.versionName);
                    model.setState(2);

                    Logger.d("updated " + model.getPackageName() + " " + model.getLabelName() + " v : "
                            + model.getPackageVersion() + (model.getIsDefaultApp() == 0 ? " " : " prelaod"));

                    packageModels.add(model);
                }

                hashMap.remove(packageInfo.packageName);

                continue;
            }
        }

        for (String key : hashMap.keySet()) {

            PackageModel model = hashMap.get(key);

            model.setState(1);

            Logger.d("deleted " + model.getPackageName() + " " + model.getLabelName() + " v : "
                    + model.getPackageVersion() + (model.getIsDefaultApp() == 0 ? " " : " prelaod"));

            packageModels.add(model);
        }

        return packageModels;
    }


    private void requestUpdateApps(final List<PackageModel> packages) {

        Logger.d("requestUpdateApps");

        if (packages.size() < 1) {

            return;
        }

        Account account = dbHelper.getAccountInfo();

        AllPackage model = new AllPackage();

        model.setPackages(packages);
        model.setChildId(account.getID());
        model.setParentId(account.getParentId());

        dbHelper.addApplications(packages);

        SimpleXmlRequest request = HttpHelper.getUpdateAppList(model,
                new Response.Listener<AllPackageResult>() {
                    @Override
                    public void onResponse(AllPackageResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                for (PackageModel model : packages) {

                                    if (model.getState() == 1) {

                                        PackageResult result = new PackageResult();

                                        result.setState(model.getState());
                                        result.setPackageId(model.getPackageId());
                                        result.setDoExistInDB(model.getHasIconDB());
                                        result.setPackageName(model.getPackageName());

                                        response.getPackages().add(result);
                                    }
                                }

                                updateLocalDB(response.getPackages());

                                startService(new Intent(getApplicationContext(), AppIconUploader.class));

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

    private void updateLocalDB(List<PackageResult> packages) {

        if (packages == null) {

            return;
        }

        for (PackageResult model : packages) {

            dbHelper.updateApplicationAfterReg(model.getPackageName(), model.getPackageId(),
                    model.getDoExistInDB(), model.getState());
        }
    }

    private PackageModel getPackageInfo(String packageName) {

        try {

            PackageInfo info = packageManager.getPackageInfo(packageName, 0);

            PackageModel model = new PackageModel();

            model.setIconHash(MD5.getHash(info.applicationInfo.loadIcon(packageManager)));
            model.setPackageVersion(info.versionName);
            model.setPackageName(info.packageName);
            model.setHash(MD5.getHash(info.packageName));
            model.setHasIcon(1);
            model.setLabelName(info.applicationInfo.loadLabel(packageManager).toString());
            model.setTimestamp(AndroidUtils.convertCurrentTime4Chat(info.lastUpdateTime));
            model.setIsDefaultApp(0);
            model.setIsExceptionApp(0);

            return model;

        } catch (PackageManager.NameNotFoundException e) {

            return null;
        }
    }

    private boolean isLauncher(String packageName) {

        ArrayList<String> names = getLauncherNames();

        for (int i = 0; ; i++) {

            if (i >= names.size()) {

                return false;
            }

            if (packageName.equalsIgnoreCase((String) names.get(i))) {

                return true;
            }
        }
    }

    private ArrayList<String> getLauncherNames() {

        ArrayList<String> names = new ArrayList<>();

        PackageManager manager = getPackageManager();

        Intent intent = new Intent("android.intent.action.MAIN", null);

        intent.addCategory("android.intent.category.HOME");

        Iterator iterator = manager.queryIntentActivities(intent, Intent.FLAG_ACTIVITY_NO_ANIMATION).iterator();

        while (iterator.hasNext()) {

            names.add(((ResolveInfo) iterator.next()).activityInfo.packageName);
        }

        return names;
    }
}
