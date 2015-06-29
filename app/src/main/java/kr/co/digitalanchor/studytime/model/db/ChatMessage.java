package kr.co.digitalanchor.studytime.model.db;

/**
 * Created by Thomas on 2015-06-27.
 */
public class ChatMessage {

    private String messagePK;

    private String roomID;

    private String messageID;

    private String senderID;

    private String guestID;

    private String guestName;

    private String message;

    private long timeStamp;

    private int unreadCount;

    private int isGroup;

    private int msgType;

    private int isFail;

    private String failName;

    public ChatMessage() {

        this.messagePK = "";
        this.roomID = "";
        this.messageID = "";
        this.guestID = "";
        this.guestName = "";
        this.timeStamp = 0L;
        this.unreadCount = -1;
        this.isGroup = -1;
        this.msgType = -1;
        this.isFail = -1;
        this.failName = "";
    }

    public String getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(String messagePK) {
        this.messagePK = messagePK;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getGuestID() {
        return guestID;
    }

    public void setGuestID(String guestID) {
        this.guestID = guestID;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    public int getIsFail() {
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

    public boolean isMine() {

        try {

            return !senderID.equals(guestID);

        } catch (NullPointerException e) {

            return false;
        }

    }
}
