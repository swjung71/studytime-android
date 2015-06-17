package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;

/**
 * Created by Thomas on 2015-06-17.
 */
public class ChildLoginResult {

    @Element(name = "ResultCode")
    String resultCode;

    @Element(name = "ResultMessage")
    String resultMessage;

    @Element(name = "ParentID")
    String parentID;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
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
}
