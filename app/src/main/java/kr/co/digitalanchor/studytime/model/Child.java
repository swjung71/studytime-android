package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "Child", strict = false)
public class Child {

  @Element(name = "ChildID", required = false)
  String childID;

  @Element(name = "Name", required = false, data = true)
  String name;

  @Element(name = "Is_off", required = false)
  String isOff;

  @Element(name = "MsgCount", required = false)
  String msgCount;

  @Element(name = "IsChanged", required = false)
  String IsChanged;

  @Element(name = "ExpirationDate", required = false)
  String expirationDate;

  @Element(name = "ExpirationYN", required = false)
  String expirationYN;

  @Element(name = "DevModel", required = false)
  String deviceModel;

  @Element(name = "RemainingDays", required = false)
  int remainingDays;

  public Child() {

  }

  public Child(String childID, String name) {

    this.childID = childID;

    this.name = name;

    this.isOff = "0";
  }

  public String getChildID() {
    return childID;
  }

  public void setChildID(String childID) {
    this.childID = childID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMsgCount() {
    return msgCount;
  }

  public void setMsgCount(String msgCount) {
    this.msgCount = msgCount;
  }

  public String getIsOff() {
    return isOff;
  }

  public void setIsOff(String isOff) {
    this.isOff = isOff;
  }

  public String getIsChanged() {
    return IsChanged;
  }

  public void setIsChanged(String isChanged) {
    IsChanged = isChanged;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getExpirationYN() {
    return expirationYN;
  }

  public void setExpirationYN(String expirationYN) {
    this.expirationYN = expirationYN;
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
}
