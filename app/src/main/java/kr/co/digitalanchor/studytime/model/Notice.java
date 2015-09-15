package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-25.
 */
@Root(name = "Notice", strict = false)
public class Notice {

    @Element(name = "Title", required = false)
    String title;

    @Element(name = "Content", required = false)
    String content;

    @Element(name = "Date", required = false)
    String date;

    @Element(name = "NoticeID", required = false)
    String noticeId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }
}
