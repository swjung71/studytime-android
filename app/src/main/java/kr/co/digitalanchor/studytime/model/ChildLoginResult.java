package kr.co.digitalanchor.studytime.model;

import android.text.TextUtils;
import org.simpleframework.xml.Element;

/**
 * Created by Thomas on 2015-06-17.
 */
public class ChildLoginResult {

  @Element(name = "ResultCode")
  int resultCode;

  @Element(name = "ResultMessage", required = false)
  String resultMessage;

  @Element(name = "ParentID", required = false)
  String parentID;

  @Element(name = "ChildYN", required = false)
  String childYn;

  public int getResultCode() {
    return resultCode;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public String getResultMessage() {
    return resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

  public String getParentID() {
    return parentID;
  }

  public void setParentID(String parentID) {
    this.parentID = parentID;
  }

  public boolean getChildYn() {

    if (TextUtils.isEmpty(childYn)) {

      return false;

    } else {

      if (childYn.equalsIgnoreCase("Y")) {

        return true;
      } else {

        return false;
      }
    }
  }

  public void setChildYn(String childYn) {
    this.childYn = childYn;
  }
}
