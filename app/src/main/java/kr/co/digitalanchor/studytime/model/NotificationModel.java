package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-08-25.
 */
@Root(name = "Notification", strict = false)
public class NotificationModel {

    @Element(name = "NotiID", required = false)
    String notiId;

    @Element(name = "messageID", required = false)
    String messageId;

    @Element(name = "receiverID", required = false)
    String receiverId;

    @Element(name = "senderID", required = false)
    String senderId;

    @Element(name = "isChild", required = false)
    String isChild;

    @Element(name = "isGroup", required = false)
    String isGroup;

    @Element(name = "msg", required = false)
    String msg;

    @Element(name = "code", required = false)
    String code;

    @Element(name = "time", required = false)
    String time;

    @Element(name = "name", required = false)
    String name;

    public String getNotiId() {
        return notiId;
    }

    public void setNotiId(String notiId) {
        this.notiId = notiId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getIsChild() {
        return isChild;
    }

    public void setIsChild(String isChild) {
        this.isChild = isChild;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
