package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ReadChat")
public class ChatRead {

    @Attribute(name = "xmlns")
    final String tag;

    @Element(name = "MessageID")
    String messageID;

    @Element(name = "ReaderID")
    String readerID;

    @Element(name = "Is_Child")
    String isChild;

    public ChatRead() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequstDataModel";
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getReaderID() {
        return readerID;
    }

    public void setReaderID(String readerID) {
        this.readerID = readerID;
    }

    public String getIsChild() {
        return isChild;
    }

    public void setIsChild(String isChild) {
        this.isChild = isChild;
    }
}
