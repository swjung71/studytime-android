package kr.co.digitalanchor.studytime.model;

import android.os.Build;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-18.
 */
@Root(name = "ParentPhoneInfo")
public class ParentPhoneInfo {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID")
    String parentID;

    @Element(name = "Phone_number")
    String phoneNumber;

    @Element(name = "Nation_code")
    String nationCode;

    @Element(name = "Is_Android")
    String isAndroid;

    @Element(name = "GCM")
    String gcm;

    @Element(name = "App_version")
    String appVersion;

    @Element(name = "OS_version")
    String osVersion;

    @Element(name = "Dev_model")
    String devModel;

    @Element(name = "Lang")
    String lang;

    public ParentPhoneInfo() {

        tag ="http://studytime.digitalanchor.co.kr/ParentRequestDataModel";

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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
