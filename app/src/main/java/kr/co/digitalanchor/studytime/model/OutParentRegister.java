package kr.co.digitalanchor.studytime.model;

import android.os.Build;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-10-21.
 */
@Root(name = "OutParentRegister", strict = false)
public class OutParentRegister {

  @Attribute(name = "xmlns", required = false)
  String xmlns;

  @Element(name = "Email", required = false)
  String email;

  @Element(name = "Password", required = false)
  String password;

  @Element(name = "Name", required = false, data = true)
  String name;

  @Element(name = "Sex", required = false)
  String sex;

  @Element(name = "Birthday", required = false)
  String birthday;

  @Element(name = "Phone_number", required = false)
  String phoneNumber;

  @Element(name = "National_code", required = false)
  String nationalCode;

  @Element(name = "Is_android", required = false)
  String isAndroid;

  @Element(name = "GCM", required = false)
  String gcm;

  @Element(name = "App_version", required = false)
  String appVersion;

  @Element(name = "OS_version", required = false)
  String osVersion;

  @Element(name = "Dev_model", required = false)
  String devModel;

  @Element(name = "Is_teacher", required = false)
  String isTeacher;

  @Element(name = "Lang", required = false)
  String lang;

  @Element(name = "OutCompanyName", required = false)
  String outCompanyName;

  @Element(name = "Age", required = false)
  String age;

  public OutParentRegister() {

    xmlns = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";

    isAndroid = "1";

    isTeacher = "0";

    devModel = Build.MODEL;

    osVersion = Build.VERSION.RELEASE;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getNationalCode() {
    return nationalCode;
  }

  public void setNationalCode(String nationalCode) {
    this.nationalCode = nationalCode;
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

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getDevModel() {
    return devModel;
  }

  public void setDevModel(String devModel) {
    this.devModel = devModel;
  }

  public String getIsTeacher() {
    return isTeacher;
  }

  public void setIsTeacher(String isTeacher) {
    this.isTeacher = isTeacher;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getOutCompanyName() {
    return outCompanyName;
  }

  public void setOutCompanyName(String outCompanyName) {
    this.outCompanyName = outCompanyName;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }
}
