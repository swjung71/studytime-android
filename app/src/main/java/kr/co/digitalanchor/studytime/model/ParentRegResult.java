package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-16.
 */
@Root
public class ParentRegResult {

    @Element(name = "ParentID", required = false)
    String ParentID;

    @Element(name = "Coin", required = false)
    String Coin;

    @Element(name = "ResultCode")
    int ResultCode;

    @Element(name = "ResultMessage")
    String ResultMessage;

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int resultCode) {
        ResultCode = resultCode;
    }

    public String getResultMessage() {
        return ResultMessage;
    }

    public void setResultMessage(String resultMessage) {
        ResultMessage = resultMessage;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getCoin() {
        return Coin;
    }

    public void setCoin(String coin) {
        Coin = coin;
    }
}
