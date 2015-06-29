package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ParentPrivacyInfo")
public class ParentInfoChange {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID", required = false)
    String parentID;

    @Element(name = "OldPass", required = false)
    String oldPwd;

    @Element(name = "NewPass", required = false)
    String newPwd;

    @Element(name = "Name", required = false, data = true)
    String name;

    @Element(name = "Sex", required = false)
    String sex;

    @Element(name = "Birthday", required = false)
    String birthday;

    @Element(name = "Email", data = true)
    String email;

    public ParentInfoChange() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
