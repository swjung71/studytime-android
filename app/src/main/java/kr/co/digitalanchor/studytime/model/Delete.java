package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-07-05.
 */
@Root(name = "Delete")
public class Delete {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID")
    String parantId;

    @Element(name = "ChildID")
    String childId;

    @Element(name = "Name", data = true)
    String name;

    @Element(name = "Password", data = true)
    String password;

    public Delete() {

        this.tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";

    }

    public String getParantId() {

        return parantId;
    }

    public void setParantId(String parantId) {
        this.parantId = parantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}
