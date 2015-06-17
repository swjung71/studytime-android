package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Thomas on 2015-06-16.
 */
@Root(name = "ParentLoginResult")
public class ParentLoginResult {

    @Element(name = "ResultCode")
    int resultCode;

    @Element(name = "ResultMessage")
    String resultMessage;

    @Element(name = "ParentID", required = false)
    String parentID;

    @Element(name = "Coin", required = false)
    String coin;

    @ElementList(name = "Child", required = false)
    List<Child> children;

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

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
