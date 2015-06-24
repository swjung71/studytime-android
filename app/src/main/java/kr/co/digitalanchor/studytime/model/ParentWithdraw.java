package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ParentWithdraw")
public class ParentWithdraw {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID")
    String parentID;

    @Element(name = "Password")
    String password;

    public ParentWithdraw() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
    }

    public ParentWithdraw(String parentID, String password) {

        this();

        this.parentID = parentID;

        this.password = password;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
