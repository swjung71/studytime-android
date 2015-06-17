package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "OnOff")
public class ParentOnOff {

    @Attribute(name = "xmlns")
    final String tag;

    @Element(name = "ParentID")
    String parentID;

    @Element(name = "ChildID")
    String childID;

    /**
     * 1 : teacher, 0 : parent
     */
    @Element(name = "Is_teacher")
    String isTeacher;

    /**
     * 1:lock, 0:unlock
     */
    @Element(name = "Is_OFF")
    String isOff;

    public ParentOnOff() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";

        isTeacher = "0";
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(String isTeacher) {
        this.isTeacher = isTeacher;
    }

    public String getIsOff() {
        return isOff;
    }

    public void setIsOff(String isOff) {
        this.isOff = isOff;
    }
}
