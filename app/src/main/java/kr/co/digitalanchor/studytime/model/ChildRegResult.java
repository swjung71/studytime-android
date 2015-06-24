package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ChildRegResult")
public class ChildRegResult {

    @Element(name = "ResultCode")
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @Element(name = "ParentID", required = false)
    String parentID;

    @Element(name = "ChildID", required = false)
    String childID;

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

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }
}
