package kr.co.digitalanchor.studytime.model.db;

/**
 * DB에서 chatting 정보를 읽어 올 때 사용하는 data model
 * Created by Seung Wook Jung on 2015-06-22.
 */
public class ChatModel {

    private String messagePK;
    private String messageID;
    private int isGroup;
    private String senderID;
    private String receiverID;
    private String senderName;
    private String msg;
    private String time;
    private int counter;
    private int isFail;
    private String failName;
    private int msgType;

    public String getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(String messagePK) {
        this.messagePK = messagePK;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public int isGroup() {

        return isGroup;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    public String getReceiverID() {

        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int isFail() {
        return isFail;
    }

    public void setIsFail(int isFail) {
        this.isFail = isFail;
    }

    public String getFailName() {
        return failName;
    }

    public void setFailName(String failName) {
        this.failName = failName;
    }

    public int isMsgType() {

        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

}
