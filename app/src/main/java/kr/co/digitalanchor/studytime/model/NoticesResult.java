package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Thomas on 2015-06-25.
 */
@Root(name = "NoticesResult")
public class NoticesResult {

    @Element(name = "ResultCode", required = false)
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @ElementList(name = "Notice", required = false, inline = true)
    ArrayList<Notice> Notices;

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

    public ArrayList<Notice> getNotices() {
        return Notices;
    }

    public void setNotices(ArrayList<Notice> notices) {
        Notices = notices;
    }
}
