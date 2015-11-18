package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-07-28.
 */
@Root(name = "Login", strict = false)
public class LoginModel {

  @Attribute(name = "xmlns")
  String tag;

  @Element(name = "ParentID", required = false)
  String parentId;

  @Element(name = "ChildID", required = false)
  String childId;

  @Element(name = "DevNum", required = false)
  String devNum;

  public LoginModel() {

    tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getChildId() {
    return childId;
  }

  public void setChildId(String childId) {
    this.childId = childId;
  }

  public String getDevNum() {
    return devNum;
  }

  public void setDevNum(String devNum) {
    this.devNum = devNum;
  }
}
