package kr.co.digitalanchor.studytime.model.db;

/**
 * study time 부모용인 경우, 자녀에 관한 정보 study time 자녀용인 경우, 부모 정보 (단 부모 이름은 없음) Created by Seung Wook Jung
 * on 2015-06-22.
 */
public class Child2 {

  private String childID;
  private int isChild;
  private String name;
  private int isOFF;
  private int newMessageCount;
  private String expirationDate;
  private String isExpired;
  private String deviceModel;
  private int remainingDays;
  private String phoneNum;
  private int isAndroid;

  public Child2() {
    this.childID = "";
    this.isChild = 0;
    this.name = "";
    this.isOFF = 0;
    this.newMessageCount = 0;
  }

  public String getChildID() {
    return childID;
  }

  public void setChildID(String childID) {
    this.childID = childID;
  }

  public int isChild() {
    return isChild;
  }

  public void setIsChild(int isChild) {
    this.isChild = isChild;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getIsChild() {
    return isChild;
  }

  public int getIsOFF() {
    return isOFF;
  }

  public void setIsOFF(int isOFF) {
    this.isOFF = isOFF;
  }

  public int getNewMessageCount() {
    return newMessageCount;
  }

  public void setNewMessageCount(int newMessageCount) {
    this.newMessageCount = newMessageCount;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getIsExpired() {
    return isExpired;
  }

  public void setIsExpired(String isExpired) {
    this.isExpired = isExpired;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel) {
    this.deviceModel = deviceModel;
  }

  public int getRemainingDays() {
    return remainingDays;
  }

  public void setRemainingDays(int remainingDays) {
    this.remainingDays = remainingDays;
  }

  public int getIsAndroid(){
    return isAndroid;
  }

  public void setIsAndroid(int isAndroid){
    this.isAndroid = isAndroid;
  }

  public void setPhoneNum(String phoneNum){
    this.phoneNum = phoneNum;
  }

  public String getPhoneNum(){return this.phoneNum;}
}
