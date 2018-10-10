package kr.co.digitalanchor.studytime.view;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.control.AppBlockGridAdapter;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.PackageElementForC;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.use.ScreenOnOff;
import kr.co.digitalanchor.studytime.use.ScreenOnOffFactory;
import kr.co.digitalanchor.utils.AndroidUtils;
import kr.co.digitalanchor.utils.IntentFilteredData;
import kr.co.digitalanchor.utils.MethodCall;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

import static android.content.Context.WINDOW_SERVICE;

/**
 * 잠금 화면을 부착합니다.
 * 잠금 화면은 {@link WindowManager}를 이용하여 전면에 부착되며,
 * 부착시 다른 화면을 덮어서 가리고 터치를 제한할 수 있습니다.
 */
public class LockView extends FrameLayout  {

    private static final String PARENT_NAME = "android.view.ViewRootImpl";
    private static final String DIE = "die";

    private static final int FLAGS = flags();

    private static final int W = WindowManager.LayoutParams.MATCH_PARENT;
    private static final int H = WindowManager.LayoutParams.MATCH_PARENT;
    private static final int TYPE = WindowManager.LayoutParams.TYPE_PHONE;
    private static final int FORMAT = PixelFormat.TRANSLUCENT;

    protected TextView mDisplayDay;

    private boolean mAttached;
    private int isAllow;

    @NonNull
    private Subscription mRefreshSubscription = Subscriptions.unsubscribed();
    private Subscription mUnlockSubscription = Subscriptions.unsubscribed();

    static DBHelper mDBHelper;

    GridView mGridView;
    static List<PackageElementForC> packages;
    static AppBlockGridAdapter mAdapter;

    public LockView(Context context) {
        super(context);
        inflate(context, R.layout.child_main_block, this);
        //mDBHelper = new DBHelper(context);
        mDBHelper = DBHelper.getInstance(context);

        init();
    }

    private void init() {

        mDisplayDay = (TextView) findViewById(R.id.lock_display_day);

        mGridView = (GridView) findViewById(R.id.gridView);
        //mGridView.setOnItemClickListener(this);

        packages = new ArrayList<PackageElementForC>();

        PackageElementForC packElem = new PackageElementForC();
        packElem.setName(STApplication.applicationContext.getString(R.string.app_name));
        packElem.setPackageName(STApplication.applicationContext.getPackageName());
        PackageManager packageManager = STApplication.applicationContext.getPackageManager();
        try {
            Logger.i("Init LockView %s", packElem.getPackageName());
            PackageInfo info = packageManager.getPackageInfo(STApplication.applicationContext.getPackageName(), 0);
            packElem.setIconUrl(info.applicationInfo.loadIcon(packageManager));
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        packages.add(packElem);

        PackageElementForC packElem2 = new PackageElementForC();
        packElem2.setName("전화");
        packElem2.setPackageName("call");
        packElem2.setIconUrl(STApplication.applicationContext.getResources().getDrawable(R.drawable.lock_btn_call));

        packages.add(packElem2);

        PackageElementForC packElem3 = new PackageElementForC();
        packElem3.setName("메시지");
        packElem3.setPackageName("sms");
        packElem3.setIconUrl(STApplication.applicationContext.getResources().getDrawable(R.drawable.lock_btn_letter));

        packages.add(packElem3);

        if (IntentFilteredData.packages != null) {
            packages.addAll(IntentFilteredData.packages);
            Logger.i("IntentFilteredData.packages add all");
        }

        List<PackageModel> packageModel = mDBHelper.getExceptionApp();

        for (PackageModel pack : packageModel) {
            PackageElementForC packElem1 = new PackageElementForC();
            packElem1.setName(pack.getLabelName());
            packElem1.setPackageName(pack.getPackageName());

            Logger.i("LockView package name " + pack.getPackageName());

            Drawable drawable = null;
            try {
                PackageInfo info = packageManager.getPackageInfo(pack.getPackageName(), 0);
                drawable = info.applicationInfo.loadIcon(packageManager);
                packElem1.setIconUrl(drawable);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }

            packages.add(packElem1);
        }

        Logger.d("it works");

        mAdapter = new AppBlockGridAdapter(STApplication.applicationContext,
                R.layout.layout_item_app_block, packages);

        mGridView.setAdapter(mAdapter);

    }

    public void setPackages(ArrayList<PackageElementForC> pack) {
        packages.addAll(pack);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 잠금 화면 부착시 버전별 호환 설정된 Flag 를 지정하기 위해 사용됩니다.
     *
     * @return Flags.
     */
    private static int flags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
        } else {
            return WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
    }

    private void displayTime() {
        long current = System.currentTimeMillis();
        mDisplayDay.setText(AndroidUtils.getCurrentTime4Chat());
        if (IntentFilteredData.packages != null) {
            packages.addAll(IntentFilteredData.packages);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        Logger.d("onScreenStateChanged: %s", screenState);

        if (View.SCREEN_STATE_ON == screenState) {
            displayTime();
            Calendar calendar = Calendar.getInstance();
            int millis = calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
            int delay = 60100 - millis;
            int period = 60000;
            mRefreshSubscription = Observable.interval(delay, period, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                                   @Override
                                   public void call(Long aLong) {
                                       if (mDBHelper.ONOFF == 1) {
                                           Logger.d("mDBHelper.ONOFF == 1 so attach");
                                           attach();
                                       } else {
                                           Logger.d("mDBHelper.ONOFF != 1 so dettach");
                                           detach();
                                           displayTime();
                                       }
                                   }
                               }
                    /*.subscribe(count -> {
                        if (mDBHelper.ONOFF == 1) {
                            Logger.d("mDBHelper.ONOFF == 1 so attach");
                            attach();
                        } else {
                            Logger.d("mDBHelper.ONOFF != 1 so dettach");
                            detach();
                            displayTime();
                        }
                    }*/);
        } else {
            mRefreshSubscription.unsubscribe();
            Logger.d("Unsubscribe");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Logger.d("onAttachedToWindow: ");
        mAttached = true;
        ScreenOnOff screen = ScreenOnOffFactory.create();
        onScreenStateChanged(screen.isOn() ? View.SCREEN_STATE_ON : View.SCREEN_STATE_OFF);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.d("onDetachedFromWindow: ");
        mAttached = false;
        mRefreshSubscription.unsubscribe();
        mUnlockSubscription.unsubscribe();
    }


    /**
     * 잠금 화면을 부착합니다. 잠금 정보에 따라 화면을 표시합니다.
     */
    public void attach() {

        if (!mAttached) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(W,
                    H,
                    TYPE,
                    FLAGS,
                    FORMAT);
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
            params.packageName = getContext().getPackageName();
            WindowManager wm = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
            try {
                wm.addView(this, params);
            } catch (IllegalStateException e) {
                detach();
                try {
                    wm.addView(this, params);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 잠금 화면을 제거합니다.
     */
    public void detach() {
        if (mAttached) {
            try {
                Context ctx = getContext();
                WindowManager wm = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);
                wm.removeView(this);
            } catch (Exception e) {
                try {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        Class<?> viewRootImpl = Class.forName(PARENT_NAME);
                        if (viewRootImpl != null && viewRootImpl.isInstance(parent)) {
                            MethodCall.call(parent, DIE, new Class<?>[]{boolean.class}, new Object[]{true});
                            return;
                        } else if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeView(this);
                            return;
                        }
                    }
                    e.printStackTrace();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    //@Override
    /*public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Logger.d("onItemClick");
        PackageElementForC item = packages.get(position);

        try {

            if(item.getPackageName().equals("sms")){
                onSms();
            }else if(item.getPackageName().equals("call")){
                onCall();
            }else {

                PackageManager pm = STApplication.applicationContext.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(item.getPackageName());
                int flags = PendingIntent.FLAG_UPDATE_CURRENT;
                Logger.i("packageName it clicked : " + item.getPackageName());
                PendingIntent.getActivity(STApplication.applicationContext, 0, intent, flags).send();

            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 전화 앱을 실행합니다.
     */

    public void onCall() {
        try {
            Context context = getContext();
            IntentFilteredData intentFilteredData = new IntentFilteredData();
            Intent intent = intentFilteredData.INTENT_DIAL;
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            Logger.i("onCall");
            PendingIntent.getActivity(context, 0, intent, flags).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SMS 앱을 실행합니다.
    */
    public void onSms() {
        try {
            Logger.i("onSMS");
            Context context = getContext();
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String pkg = Telephony.Sms.getDefaultSmsPackage(context);
                intent = new Intent(IntentFilteredData.INTENT_SMS_KITKET_HIGH);
                if (pkg != null) {
                    intent.setPackage(pkg);
                }
            } else {
                intent = IntentFilteredData.INTENT_SMS_JB_LOW;
            }
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent.getActivity(context, 0, intent, flags).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lock_call:
                onCall();
                break;
            case R.id.lock_sms:
                onSms();
                break;
            default:
                break;
        }
    }*/

    public static void changeExceptionApp(){

        packages.clear();

        PackageElementForC packElem = new PackageElementForC();
        packElem.setName(STApplication.applicationContext.getString(R.string.app_name));
        packElem.setPackageName(STApplication.applicationContext.getPackageName());
        PackageManager packageManager = STApplication.applicationContext.getPackageManager();
        try {
            Logger.i("Init LockView %s", packElem.getPackageName());
            PackageInfo info = packageManager.getPackageInfo(STApplication.applicationContext.getPackageName(), 0);
            packElem.setIconUrl(info.applicationInfo.loadIcon(packageManager));
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        packages.add(packElem);

        PackageElementForC packElem2 = new PackageElementForC();
        packElem2.setName("전화");
        packElem2.setPackageName("call");
        packElem2.setIconUrl(STApplication.applicationContext.getResources().getDrawable(R.drawable.lock_btn_call));

        packages.add(packElem2);

        PackageElementForC packElem3 = new PackageElementForC();
        packElem3.setName("메시지");
        packElem3.setPackageName("sms");
        packElem3.setIconUrl(STApplication.applicationContext.getResources().getDrawable(R.drawable.lock_btn_letter));

        packages.add(packElem3);

        if (IntentFilteredData.packages != null) {
            packages.addAll(IntentFilteredData.packages);
            Logger.i("IntentFilteredData.packages add all");
        }

        List<PackageModel> packageModel = mDBHelper.getExceptionApp();

        for (PackageModel pack : packageModel) {
            PackageElementForC packElem1 = new PackageElementForC();
            packElem1.setName(pack.getLabelName());
            packElem1.setPackageName(pack.getPackageName());

            Logger.i("LockView package name " + pack.getPackageName());

            Drawable drawable = null;
            try {
                PackageInfo info = packageManager.getPackageInfo(pack.getPackageName(), 0);
                drawable = info.applicationInfo.loadIcon(packageManager);
                packElem1.setIconUrl(drawable);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }

            packages.add(packElem1);
        }

        mAdapter.notifyDataSetChanged();
    }
}
