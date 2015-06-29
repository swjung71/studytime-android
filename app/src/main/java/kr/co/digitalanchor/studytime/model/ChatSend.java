package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "Chat")
public class ChatSend {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "SenderID")
    String senderID;

    @Element(name = "Is_group")
    int isGroup;

    @Element(name = "Is_child_sender")
    int isChildSender;

    @Element(name = "ReceiverID")
    String receiverID;

    @Element(name = "Is_child_receiver")
    int isChildReceiver;

    @Element(name = "Message", data = true)
    String message;

    @Element(name = "Msg_type")
    int msgType;

    @Element(name = "Time")
    long time;

    @Element(name = "Sender_name", data = true)
    String senderName;

    @Element(name = "MessagePK")
    long messagePk;

    public ChatSend() {

        tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    public int getIsChildSender() {
        return isChildSender;
    }

    public void setIsChildSender(int isChildSender) {
        this.isChildSender = isChildSender;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public int getIsChildReceiver() {
        return isChildReceiver;
    }

    public void setIsChildReceiver(int isChildReceiver) {
        this.isChildReceiver = isChildReceiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getMessagePk() {
        return messagePk;
    }

    public void setMessagePk(long messagePk) {
        this.messagePk = messagePk;
    }
}
