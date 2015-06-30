package kr.co.digitalanchor.studytime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.ChatMessage;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.utils.AndroidUtils;

/**
 * Created by Thomas on 2015-06-16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DB_NAME = "local";

    //table names
    private static final String TABLE_ACCOUNT_INFO = "account_table";
    private static final String TABLE_CHILD = "child_table"; //부모 studytime이면 자녀 table, 자녀 studytime이면 부모 table
    private static final String TABLE_ON_OFF = "onOff_table";
    private static final String TABLE_MESSAGE = "message_table";

    //Key value
    private static final String CHAT_KEY = "messagePK";
    private static final String ACCOUNT_ID = "accountPK";
    private static final String ONOFF_KEY = "onOffPK";

    //column for account
    private static final String ID = "ID"; // 부모이면 parentID, 자녀면 childID, 선생님이면 parentID
    private static final String NAME = "name";
    private static final String IS_PARENT = "isParent"; // 0 child 1 parent 2 teacher
    private static final String PASSWORD = "password"; // 부모이면 부모 password, 자녀도 삭제를 위해서 부모 password
    private static final String COIN = "coin"; // 부모가 가진 코인 (하트)
    private static final String EMAIL = "email"; // 부모의 Email
    private static final String PARENT_ID = "parentID"; // 자식을 경우 부모아이디를 저장한다.

    //column for child table
    private static final String CHILDREN_ID = "childID"; // 부모이면 parentID, 자녀면 childID, 선생님이면 parentID

    //column for chat
    private static final String MSG_ID = "messageID"; // 서버에 저장되는 message primary key
    private static final String IS_GROUP = "is_Group";
    private static final String SENDER_ID = "senderID";
    private static final String MSG = "message";
    private static final String TIMESTAMP = "time";
    private static final String IS_FAIL = "is_fail";
    private static final String FAIL_NAME = "fail_name";// ;로 구별됨
    private static final String MSG_TYPE = "msg_type";// 0이면 문자, 1이면 이미지

    // column for chat 2
    private static final String ROOM_ID = "roomID";
    private static final String GUEST_ID = "guestID";
    private static final String GUEST_NAME = "guestName";
    private static final String UNREAD_COUNT = "unreadCount";

    private static final String IS_OFF = "isOff";// 0이면 on 1이면 off
    private static final String NEW_MESSAGE_COUNT = "newMessageCount";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_ACCOUNT_INFO = "CREATE TABLE " + TABLE_ACCOUNT_INFO
                + "(" + ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID
                + " INTEGER NOT NULL, " + IS_PARENT + " TEXT NOT NULL, " + NAME + " TEXT, "
                + PASSWORD + " TEXT NOT NULL, " + COIN + " TEXT NOT NULL, " + EMAIL + " TEXT NOT NULL, "
                + PARENT_ID + " TEXT )";

        //IS_PARENT 가 0이면 CHILD_ID는 자녀 ID, 1이면 parentID, 2이면 teacherID(향후 버전), NAME은 자녀 이름, 부모인 경우 이름을 저장하지 않음 (향후 버전)
        String CREATE_TABLE_CHILD = "CREATE TABLE " + TABLE_CHILD
                + "(" + CHILDREN_ID + " INTEGER PRIMARY KEY," + IS_PARENT +
                " INTEGER NOT NULL, " + NAME + " TEXT, " + IS_OFF + " TEXT, "
                + NEW_MESSAGE_COUNT + " INTEGER )";

/*
        TIMESTAMP 는 YYYY-MM-DD HH:MM:SS 형태로 저장

        String CREATE_TABLE_CHAT = "CREATE TABLE " + TABLE_CHAT
                + "(" + CHAT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MSG_ID
                + " INTEGER , " + IS_GROUP + " INTEGER, " + SENDER_ID
                + " INTEGER, " + RECEIVER_ID + " INTEGER," + SENDER_NAME + " TEXT, "
                + MSG + " TEXT, " + TIMESTAMP + " TEXT, " + COUNTER + " INTEGER, " + MSG_TYPE
                + " INTEGER, " + IS_FAIL + " INTEGER, " + FAIL_NAME + " TEXT )";
*/
        String CREATE_TABLE_ONOFF = "CREATE TABLE " + TABLE_ON_OFF
                + "(" + ONOFF_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + IS_OFF
                + " INTEGER NOT NULL )";

        // TIMESTAMP 는 YYYY-MM-DD HH:MM:SS 형태로 저장
        String CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE + " ( "
                + CHAT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ROOM_ID + " INTEGER, "
                + MSG_ID + " INTEGER, "
                + SENDER_ID + " INTEGER, "
                + GUEST_ID + " INTEGER, "
                + GUEST_NAME + " TEXT, "
                + TIMESTAMP + " INTEGER, "
                + MSG + " TEST, "
                + UNREAD_COUNT + " INTEGER, "
                + IS_GROUP + " INTEGER, "
                + IS_FAIL + " INTEGER, "
                + MSG_TYPE + " INTEGER, "
                + FAIL_NAME + " TEXT )";

        db.execSQL(CREATE_TABLE_ACCOUNT_INFO);
        db.execSQL(CREATE_TABLE_CHILD);
        db.execSQL(CREATE_TABLE_MESSAGE);
        db.execSQL(CREATE_TABLE_ONOFF);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("DBHelper", "Upgrading from version " + oldVersion + " to " + newVersion + "which will destory all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ON_OFF);
        onCreate(db);
    }

    /**
     * 부모용
     *
     * @param id
     * @param isChild
     * @param name
     * @param coin
     * @param email
     */
    public void insertAccount(String id, int isChild, String name, String coin, String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ID, id);
        values.put(IS_PARENT, 1);
        values.put(NAME, TextUtils.isEmpty(name) ? "" : AndroidUtils.convertFromUTF8(name));
        values.put(PASSWORD, "");
        values.put(COIN, TextUtils.isEmpty(coin) ? "" : coin);
        values.put(EMAIL, TextUtils.isEmpty(email) ? "" : email);

        db.insert(TABLE_ACCOUNT_INFO, null, values);
    }

    /**
     * 자녀용
     *
     * @param id
     * @param name
     * @param parentId
     */
    public void insertAccount(String id, String name, String parentId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ID, id);
        values.put(IS_PARENT, 0);
        values.put(NAME, TextUtils.isEmpty(name) ? "" : AndroidUtils.convertFromUTF8(name));
        values.put(PARENT_ID, parentId);
        values.put(PASSWORD, "");
        values.put(COIN, "");
        values.put(EMAIL, "");

        db.insert(TABLE_ACCOUNT_INFO, null, values);
    }

    public void updateAccount(String id, int isChild, String name, int coin, String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(IS_PARENT, isChild);
        values.put(NAME, TextUtils.isEmpty(name) ? "" : name);
        values.put(PASSWORD, "");
        values.put(COIN, coin);
        values.put(EMAIL, TextUtils.isEmpty(email) ? "" : email);

        db.update(TABLE_ACCOUNT_INFO, values, ID + "=?", new String[]{id});
    }

    public void updateCoin(String id, int coin) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COIN, coin);

        db.update(TABLE_ACCOUNT_INFO, values, ID + "=?", new String[]{id});
    }

    public Account getAccountInfo() {

        Account account = new Account();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] result_columns = new String[]{ID, IS_PARENT, NAME, PASSWORD, COIN, EMAIL, PARENT_ID};

        Cursor cursor = db.query(true, TABLE_ACCOUNT_INFO, result_columns, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            account.setID(cursor.getString(0));
            account.setIsChild(cursor.getInt(1));
            account.setName(cursor.getString(2));

            if (cursor.getString(3) != null) {
                account.setPassword(cursor.getString(3));
            }

            account.setCoin(cursor.getInt(4));

            if (cursor.getString(5) != null) {

                account.setEmail(cursor.getString(5));
            }

            if (cursor.getString(6) != null) {

                account.setParentId(cursor.getString(6));
            }
        }

        cursor.close();

        return account;
    }

    public void insertChild(String id,  String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CHILDREN_ID, id);
        values.put(IS_PARENT, 0);

        if (name != null) {
            values.put(NAME, name);
        }

        db.replace(TABLE_CHILD, null, values);

    }

    public void insertChild(String id, int isChild, String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CHILDREN_ID, id);
        values.put(IS_PARENT, isChild);

        if (name != null) {
            values.put(NAME, AndroidUtils.convertFromUTF8(name));
        }

        db.replace(TABLE_CHILD, null, values);

    }

    public void insertChildren(List<kr.co.digitalanchor.studytime.model.Child> children) {

        if (children == null)
            return;

        for (kr.co.digitalanchor.studytime.model.Child child : children) {

            insertChild(child.getChildID(), 0, child.getName());
        }
    }

    public ArrayList<Child> getChildren() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Child> children = new ArrayList<Child>();
        String[] result_columns = new String[]{CHILDREN_ID, IS_PARENT, NAME, IS_OFF, NEW_MESSAGE_COUNT};

        Cursor cursor = db.query(true, TABLE_CHILD, result_columns, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Child child = new Child();
                child.setChildID(cursor.getString(0));
                child.setIsChild(cursor.getInt(1));

                if (cursor.getString(2) != null) {
                    child.setName(cursor.getString(2));
                }

                child.setIsOFF(cursor.getInt(3));

                child.setNewMessageCount(cursor.getInt(4));

                children.add(child);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return children;

    }

    public Child getChild(String childId) {

        Child child = null;

        SQLiteDatabase db = this.getReadableDatabase();

        child = new Child();

        String[] result_columns = new String[]{CHILDREN_ID, IS_PARENT, NAME, IS_OFF, NEW_MESSAGE_COUNT};

        Cursor cursor = db.query(true, TABLE_CHILD, result_columns, CHILDREN_ID + "=?",
                new String[]{childId}, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {

                child.setChildID(cursor.getString(0));

                child.setIsChild(cursor.getInt(1));

                if (cursor.getString(2) != null) {
                    child.setName(cursor.getString(2));
                }

                child.setIsOFF(cursor.getInt(3));

                child.setNewMessageCount(cursor.getInt(4));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return child;
    }

    /**
     * 자녀의 상태를 업데이트 한다.
     * 0 : on, 1 : off
     *
     * @param childId
     * @param isOff
     */
    public void updateChildToggle(String childId, int isOff) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(IS_OFF, isOff);

        db.update(TABLE_CHILD, values, CHILDREN_ID + "=?", new String[]{childId});
    }

    /**
     * 처음 메시지를 보내긴 전에
     *
     * @param guestId   : 상대방 ID
     * @param guestName : 상대방 이름
     * @param senderId  : 메시지 보낸 사람
     * @param msg       : 메시지
     * @param time      : 보내는 시간 YYYY-MM-DD HH:MM:SS 형식
     * @param unreadCnt :안 읽은 사람 수 (현재는 1 부터)
     * @param isFail    : 성공 여부 (처음은 0, 실패 시 1)
     * @param msgType   : 메시지 타입 (현재는 0)
     * @param isGroup   : 그룹 채팅방인지
     * @return messagePK : (local DB의 chat primary key
     */
    public long insertMessageBeforeSend(String guestId, String guestName, String senderId,
                                        String msg, long time, int unreadCnt, int isFail,
                                        int msgType, int isGroup) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues values = new ContentValues();

            values.put(IS_GROUP, isGroup);
            values.put(ROOM_ID, guestId);
            values.put(GUEST_ID, guestId);
            values.put(GUEST_NAME, guestName);
            values.put(SENDER_ID, senderId);
            values.put(MSG, msg);
            values.put(TIMESTAMP, time);
            values.put(UNREAD_COUNT, unreadCnt);
            values.put(IS_FAIL, isFail);
            values.put(MSG_TYPE, msgType);

            return db.insert(TABLE_MESSAGE, null, values);

        } finally {

            if (db != null) {

                db.close();

            }
        }
    }

    /**
     * 메시지 전송 요청 이후, 결과 업데이트
     *
     * @param messagePK : local db에 있는 message primary key 값
     * @param messageID : 서버의 message primary key 값 보낼 때는 없음
     * @param isFail    : 네트워크가 안좋아서 문자를 못보내거나, 전송은 하였으나 서버로부터 error를 받은 경우 1, 처음 보낼 때 0
     * @param failName  : 문자를 못받은 사람의 리스트, ;로 구별 ex) 정승욱;남상미;정재욱;유정효
     */
    public void updateMessageAfterSend(String messagePK, String messageID, int isFail, String failName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(IS_FAIL, isFail);

        if (!TextUtils.isEmpty(messageID)) {

            values.put(MSG_ID, messageID);

        }

        if (!TextUtils.isEmpty(failName)) {

            values.put(FAIL_NAME, failName);
        }

        db.update(TABLE_MESSAGE, values, CHAT_KEY + "=?", new String[]{messagePK});
    }

    /**
     * GCM message를 받고 DB에 초기값을 저장하는 함수
     *
     * @param messageId
     * @param guestName
     * @param unreadCount
     * @param guestId
     * @param isGroup
     * @param isChild
     * @return
     */
    public long insertMessageFromGCM(String messageId, String roomId, String guestId, String guestName,
                                     String unreadCount, int isGroup, int isChild, String msg, long time, int msgType) {

        Logger.d(messageId + " " + roomId + " " + guestId + " ");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MSG_ID, messageId);
        values.put(ROOM_ID, roomId);
        values.put(GUEST_NAME, guestName);
        values.put(GUEST_ID, guestId);
        values.put(SENDER_ID, guestId);
        values.put(UNREAD_COUNT, unreadCount);
        values.put(IS_GROUP, isGroup);
        values.put(MSG, msg);
        values.put(TIMESTAMP, time);
        values.put(MSG_TYPE, msgType);

        return db.insert(TABLE_MESSAGE, null, values);
    }

    //TODO read chat message

    /**
     * 메시지 가져온다, 최대 100개
     *
     * @return 메시지 리스트
     */
    public List<ChatMessage> getMessages(String roomID) {

        List<ChatMessage> messages = new ArrayList<>();

        String[] columns = new String[]{CHAT_KEY, ROOM_ID, MSG_ID, IS_GROUP, GUEST_ID, GUEST_NAME,
                SENDER_ID, MSG, TIMESTAMP, UNREAD_COUNT, MSG_TYPE, IS_FAIL, FAIL_NAME};

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_MESSAGE, columns, ROOM_ID + "=?", new String[]{roomID},
                null, null, TIMESTAMP + " ASC", "100");

        if (cursor.moveToFirst()) {

            do {

                ChatMessage msg = new ChatMessage();

                msg.setMessagePK(cursor.getString(0));
                msg.setRoomID(cursor.getString(1));
                msg.setMessageID(cursor.getString(2));
                msg.setIsGroup(cursor.getInt(3));
                msg.setGuestID(cursor.getString(4));
                msg.setGuestName(cursor.getString(5));
                msg.setSenderID(cursor.getString(6));
                msg.setMessage(cursor.getString(7));
                msg.setTimeStamp(cursor.getLong(8));
                msg.setUnreadCount(cursor.getInt(9));
                msg.setMsgType(cursor.getInt(10));
                msg.setIsFail(cursor.getInt(11));
                msg.setFailName(cursor.getString(12));

                messages.add(msg);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return messages;
    }

    public List<ChatMessage> getMessages(String roomID, long timesStamp) {

        List<ChatMessage> messages = new ArrayList<>();

        String[] columns = new String[]{CHAT_KEY, ROOM_ID, MSG_ID, IS_GROUP, GUEST_ID, GUEST_NAME,
                SENDER_ID, MSG, TIMESTAMP, UNREAD_COUNT, MSG_TYPE, IS_FAIL, FAIL_NAME};

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_MESSAGE, columns, ROOM_ID + "=? AND " + TIMESTAMP + ">?",
                new String[]{roomID, String.valueOf(timesStamp)}, null, null, TIMESTAMP + " ASC", "100");

        if (cursor.moveToFirst()) {

            do {

                ChatMessage msg = new ChatMessage();

                msg.setMessagePK(cursor.getString(0));
                msg.setRoomID(cursor.getString(1));
                msg.setMessageID(cursor.getString(2));
                msg.setIsGroup(cursor.getInt(3));
                msg.setGuestID(cursor.getString(4));
                msg.setGuestName(cursor.getString(5));
                msg.setSenderID(cursor.getString(6));
                msg.setMessage(cursor.getString(7));
                msg.setTimeStamp(cursor.getLong(8));
                msg.setUnreadCount(cursor.getInt(9));
                msg.setMsgType(cursor.getInt(10));
                msg.setIsFail(cursor.getInt(11));
                msg.setFailName(cursor.getString(12));

                messages.add(msg);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return messages;
    }

    /**
     * 처음으로 onOFF 정보를 넣거나 update할 때 사용
     *
     * @param isOff
     */
    public void insertOnOff(int isOff) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT OR REPLACE INTO " + TABLE_ON_OFF + "(" + ONOFF_KEY + ", " + IS_OFF + ") values ((SELECT " +
                ONOFF_KEY + " FROM " + TABLE_ON_OFF + "), " + isOff + ")";

        db.rawQuery(query, null);
    }

    public void updateChildMessageCount(String childId, int count) {


    }

    /**
     * on off state를 return함
     *
     * @return if 0, then on, if 1, then off, if -1, no date
     */
    public int getOnOff() {

        Cursor cursor = null;

        try {

            String selectQuery = "SELECT " + IS_OFF + " FROM " + TABLE_ON_OFF;

            SQLiteDatabase db = this.getReadableDatabase();

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }

            return -1;

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }
    }

    public void clearAll() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ON_OFF);

        onCreate(db);
    }
}
