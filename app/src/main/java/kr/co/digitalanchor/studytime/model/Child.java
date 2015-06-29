package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "Child")
public class Child {

    @Element(name = "ChildID", required = false)
    String childID;

    @Element(name = "Name", required = false, data = true)
    String name;

    @Element(name = "MsgCount", required = false)
    String msgCount;

    public Child() {

    }

    public Child(String childID, String name) {

        this.childID = childID;

        this.name = name;
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
}
