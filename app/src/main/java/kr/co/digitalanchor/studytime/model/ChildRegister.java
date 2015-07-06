package kr.co.digitalanchor.studytime.model;

import android.os.Build;
import android.text.TextUtils;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ChildRegister")
public class ChildRegister implements Cloneable {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID")
    String parentID;

    @Element(name = "Phone_number", required = false)
    String phoneNumber;

    @Element(name = "Name", required = false, data = true)
    String name;

    @Element(name = "National_Code", required = false)
    String nationalCode;

    @Element(name = "Is_android")
    String isAndroid;

    @Element(name = "GCM")
    String gcm;

    /**
     * 0:male, 1:female
     */
    @Element(name = "Sex", required = false)
    String sex;

    @Element(name = "Birthday", required = false)
    String birthday;

    @Element(name = "App_version")
    String appVersion;

    @Element(name = "OS_version")
    String osVersion;

    @Element(name = "Dev_model")
    String devModel;

    @Element(name = "Dev_num")
    String devNum;

    @Element(name = "MAC")
    String mac;

    @Element(name = "Lang")
    String lang;

    public ChildRegister() {

        tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";

        osVersion = Build.VERSION.RELEASE;

        devModel = Build.MODEL;

        isAndroid = "1";
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

    public String getName() {
        return TextUtils.isEmpty(name) ? " " : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDevNum() {
        return devNum;
    }

    public void setDevNum(String devNum) {
        this.devNum = devNum;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public ChildRegister clone() throws CloneNotSupportedException {

        try {

            return (ChildRegister) super.clone();

        } catch (CloneNotSupportedException e) {

            throw new AssertionError();
        }
    }
}
