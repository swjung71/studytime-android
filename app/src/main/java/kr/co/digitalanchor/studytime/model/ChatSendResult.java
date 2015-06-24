package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ChatSendResult")
public class ChatSendResult {

    @Element(name = "ResultCode")
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @Element(name = "Is_fail", required = false)
    String isFail;

    @Element(name = "messageID", required = false)
    String messageID;

    @Element(name = "messagePK", required = false)
    String messagePK;

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

    public String getIsFail() {
        return isFail;
    }

    public void setIsFail(String isFail) {
        this.isFail = isFail;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(String messagePK) {
        this.messagePK = messagePK;
    }
}
