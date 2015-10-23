package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-10-23.
 */
@Root(name = "ChangePass", strict = false)
public class ChangePassModel {

  @Attribute(name = "xmlns")
  String tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";

  @Element(name = "ParentID", required = false, data = true)
  String parentId;

  @Element(name = "Email", required = false, data = true)
  String email;

  @Element(name = "OldPass", required = false, data = true)
  String oldPass;

  @Element(name = "NewPass", required = false, data = true)
  String newPass;

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOldPass() {
    return oldPass;
  }

  public void setOldPass(String oldPass) {
    this.oldPass = oldPass;
  }

  public String getNewPass() {
    return newPass;
  }

  public void setNewPass(String newPass) {
    this.newPass = newPass;
  }
}
