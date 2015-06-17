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
    final String tag;

    @Element(name = "SenderID")
    String senderID;

    @Element(name = "Is_group")
    String isGroup;

    @Element(name = "Is_child_sender")
    String isChildSender;

    @Element(name = "ReceiverID")
    String receiverID;

    @Element(name = "Is_child_recevier")
    String isChildReceiver;

    @Element(name = "Message")
    String message;

    @Element(name = "Msg_type")
    String msgType;

    @Element(name = "Time")
    String time;

    @Element(name = "Sender_name")
    String senderName;

    @Element(name = "MessagePK")
    String messagePk;

    public ChatSend() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequstDataModel";
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public String getIsChildSender() {
        return isChildSender;
    }

    public void setIsChildSender(String isChildSender) {
        this.isChildSender = isChildSender;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getIsChildReceiver() {
        return isChildReceiver;
    }

    public void setIsChildReceiver(String isChildReceiver) {
        this.isChildReceiver = isChildReceiver;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessagePk() {
        return messagePk;
    }

    public void setMessagePk(String messagePk) {
        this.messagePk = messagePk;
    }
}
