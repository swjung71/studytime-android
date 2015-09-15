package kr.co.digitalanchor.studytime.model;

/**
 * Created by Thomas on 2015-09-15.
 */

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "NewNoticesResult", strict = false)
public class NewNoticeResult {

    @Element(name = "ResultCode", required = false)
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @Element(name = "hasNewNotices", required = false)
    String hasNewNotice;

    @ElementList(name = "Notice", required = false, inline = true)
    List<Notice> notices;

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

    public String getHasNewNotice() {
        return hasNewNotice;
    }

    public void setHasNewNotice(String hasNewNotice) {
        this.hasNewNotice = hasNewNotice;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }
}
