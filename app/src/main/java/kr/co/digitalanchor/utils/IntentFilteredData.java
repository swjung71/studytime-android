package kr.co.digitalanchor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.model.PackageElementForC;

/**
 * 정책 잠금 중 전화, 문자앱을 실행할 때 어떤 앱이 포함되는 앱인지 필터링하기 위한 기능입니다.
 */
public final class IntentFilteredData {

    public static final Intent INTENT_GO_HOME = new Intent(Intent.ACTION_MAIN);
    public static final Intent INTENT_HOME_APP_FILTER = new Intent(Intent.ACTION_MAIN);
    public static final Intent INTENT_LAUNCHABLE = new Intent(Intent.ACTION_MAIN);
    public static final Intent INTENT_SMS_KITKET_HIGH = new Intent(Intent.ACTION_SENDTO,
                                                                   Uri.parse("smsto:"));
    public static final Intent INTENT_SMS_JB_LOW = new Intent(Intent.ACTION_VIEW);
    public static final Intent INTENT_DIAL = new Intent(Intent.ACTION_DIAL);
    public static final Intent INTENT_CALL = new Intent(Intent.ACTION_CALL);

    public static ArrayList<PackageElementForC> packages;

    static {
        INTENT_GO_HOME.addCategory(Intent.CATEGORY_HOME);
        INTENT_GO_HOME.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        INTENT_HOME_APP_FILTER.addCategory(Intent.CATEGORY_HOME);
        INTENT_LAUNCHABLE.addCategory(Intent.CATEGORY_LAUNCHER);
        INTENT_SMS_JB_LOW.setType("vnd.android-dir/mms-sms");
        INTENT_CALL.setData(Uri.parse("tel:"));
    }

    /**
     * 전화, SMS 관련 앱 배열.
     */
    private String[] mCallApps;

    public IntentFilteredData() {
        initCallAndSmsApplications();
    }

    /**
     * 전화, SMS 관련 어플리케이션 목록을 갱신한다.
     */
    private void initCallAndSmsApplications() {
        Context context = STApplication.applicationContext;
        PackageManager pm = context.getPackageManager();
        Set<String> set = new HashSet<>();
        List<ResolveInfo> infoList = new ArrayList<>();
        try { //문자 키캣 이상.
            infoList.addAll(pm.queryIntentActivities(INTENT_SMS_KITKET_HIGH, 0));
        } catch (Exception ignore) {
        }
        try { //문자 젤리빈 이하
            infoList.addAll(pm.queryIntentActivities(INTENT_SMS_JB_LOW, 0));
        } catch (Exception ignore) {
        }
        try { //전화 다이얼.
            infoList.addAll(pm.queryIntentActivities(INTENT_DIAL, 0));
        } catch (Exception ignore) {
        }
        try { //전화
            infoList.addAll(pm.queryIntentActivities(INTENT_CALL, 0));
        } catch (Exception ignore) {
        }

        int size = infoList.size();
        String pkg;
        for (int i = 0; i < size; i++) {
            try {
                pkg = infoList.get(i).activityInfo.packageName;
            } catch (Exception e) {
                pkg = null;
            }
            if (pkg != null) {
                set.add(pkg);
               /* PackageElementForC packElem = new PackageElementForC();
                packElem.setName(infoList.get(i).nonLocalizedLabel.toString());
                packElem.setPackageName(infoList.get(i).activityInfo.packageName);

                Logger.i("intentFilteredData packages " + packElem.getPackageName());
                *//*if(packElem.getPackageName().contains("mms") || packElem.getPackageName().contains("sms")){
                    packElem.setIconUrl(STApplication.applicationContext.getResources().getDrawable(R.drawable.lock_btn_letter));
                }else{
                    packElem.setIconUrl(STApplication.applicationContext.getResources().getDrawable(R.drawable.lock_btn_call));
                }*//*
                packages.add(packElem);*/
            }
        }

        //set.add("android");     //셀렉터
        set.add("com.google.android.incallui");
        set.add("com.android.incallui");
        set.add("com.lge.ltecall");
        set.add("com.pantech.app.vt");
        mCallApps = set.toArray(new String[set.size()]);
        Arrays.sort(mCallApps);

        for(String s : mCallApps){
            Logger.i("isCall : " + s);
        }
    }

    /**
     * 전화 또는 SMS 앱인지 확인한다.
     */
    public boolean isCallOrSms(@NonNull String pkg) {
        try {
            return Arrays.binarySearch(mCallApps, pkg) >= 0
                    || pkg.contains("phone")
                    || pkg.contains("dialer");
        } catch (Exception e) {
            return false;
        }
    }
}
