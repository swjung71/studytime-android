package kr.co.digitalanchor.studytime.model;

import android.os.Build;

import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-18.
 */
@Root(name = "ParentPhoneInfo")
public class ParentPhoneInfo {

    String parentID;

    String phoneNumber;

    String nationCode;

    String isAndroid;

    String gcm;

    String appVersion;

    String osVersion;

    String devModel;

    public ParentPhoneInfo() {

        isAndroid = "1";

        osVersion = Build.VERSION.RELEASE;

        devModel = Build.MODEL;
    }


    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationCode() {
        return nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }

    public String getIsAndroid() {
        return isAndroid;
    }

    public void setIsAndroid(String isAndroid) {
        this.isAndroid = isAndroid;
    }

    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

}
