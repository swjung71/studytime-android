package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ChatReadResult")
public class ChatReadResult {

    @Element(name = "ResultCode")
    String resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @Element(name = "Message", required = false)
    String message;

    /**
     * 0:message, 1:image
     */
    @Element(name = "Msg_type", required = false)
    String msgType;

    @Element(name = "Counter", required = false)
    String counter;

    @Element(name = "Time", required = false)
    String time;

    public ChatReadResult() {

        msgType = "0";
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
