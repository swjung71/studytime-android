package kr.co.digitalanchor.studytime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.ChatModel;
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
    private static final String TABLE_CHAT = "chat_table";
    private static final String TABLE_ON_OFF = "onOff_table";

    //Key value
    private static final String CHAT_KEY = "messagePK";
    private static final String ACCOUNT_ID = "accountPK";
    private static final String CHILD_ID = "childPK";
    private static final String ONOFF_KEY = "onOffPK";

    //column for account
    private static final String ID = "ID"; // 부모이면 parentID, 자녀면 childID, 선생님이면 parentID
    private static final String NAME = "name";
    private static final String IS_PARENT = "isParent"; // 0 child 1 parent 2 teacher
    private static final String PASSWORD = "password"; // 부모이면 부모 password, 자녀도 삭제를 위해서 부모 password
    private static final String COIN = "coin"; // 부모가 가진 코인 (하트)
    private static final String EMAIL = "email"; // 부모의 Email

    //column for child table
    private static final String CHILDREN_ID = "childID"; // 부모이면 parentID, 자녀면 childID, 선생님이면 parentID

    //column for chat
    private static final String MSG_ID = "messageID"; // 서버에 저장되는 message primary key
    private static final String IS_GROUP = "is_Group";
    private static final String SENDER_ID = "senderID";
    private static final String RECEIVER_ID = "receiverID";
    private static final String SENDER_NAME = "sender_name";
    private static final String MSG = "message";
    private static final String TIMESTAMP = "time";
    private static final String COUNTER = "counter";
    private static final String IS_FAIL = "is_fail";
    private static final String FAIL_NAME = "fail_name";// ;로 구별됨
    private static final String MSG_TYPE = "msg_type";// 0이면 문자, 1이면 이미지

    private static final String IS_OFF = "isOff";// 0이면 on 1이면 off

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_ACCOUNT_INFO = "CREATE TABLE " + TABLE_ACCOUNT_INFO
                + "(" + ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID
                + " INTEGER NOT NULL, " + IS_PARENT + " TEXT NOT NULL, " + NAME + " TEXT NOT NULL, "
                + PASSWORD + " TEXT NOT NULL, " + COIN + " TEXT NOT NULL, " + EMAIL + " TEXT NOT NULL )";

        //IS_PARENT 가 0이면 CHILD_ID는 자녀 ID, 1이면 parentID, 2이면 teacherID(향후 버전), NAME은 자녀 이름, 부모인 경우 이름을 저장하지 않음 (향후 버전)
        String CREATE_TABLE_CHILD = "CREATE TABLE " + TABLE_CHILD
                + "(" + CHILD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CHILDREN_ID
                + " INTEGER NOT NULL," + IS_PARENT + " INTEGER NOT NULL, " + NAME + " TEXT)";

        //TIMESTAMP 는 YYYY-MM-DD HH:MM:SS 형태로 저장
        String CREATE_TABLE_CHAT = "CREATE TABLE " + TABLE_CHAT
                + "(" + CHAT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MSG_ID
                + " INTEGER , " + IS_GROUP + " INTEGER, " + SENDER_ID
                + " INTEGER, " + RECEIVER_ID + " INTEGER," + SENDER_NAME + " TEXT, "
                + MSG + " TEXT, " + TIMESTAMP + " TEXT, " + COUNTER + " INTEGER, " + MSG_TYPE
                + " INTEGER, " + IS_FAIL + " INTEGER, " + FAIL_NAME + " TEXT )";

        String CREATE_TABLE_ONOFF = "CREATE TABLE " + TABLE_ON_OFF
                + "(" + ONOFF_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + IS_OFF
                + " INTEGER NOT NULL )";

        db.execSQL(CREATE_TABLE_ACCOUNT_INFO);
        db.execSQL(CREATE_TABLE_CHILD);
        db.execSQL(CREATE_TABLE_CHAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("DBHelper", "Upgrading from version " + oldVersion + " to " + newVersion + "which will destory all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);
    }

    /**
     * all CRUD(Create, Read, Update, Delete) Operations
     */
    public void insertAccount(String id, int isChild, String name, String password, String coin, String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(IS_PARENT, isChild);
        values.put(NAME, TextUtils.isEmpty(name) ? "" : name);
        values.put(PASSWORD, TextUtils.isEmpty(password) ? "" : password);
        values.put(COIN, TextUtils.isEmpty(coin) ? "" : coin);
        values.put(EMAIL, TextUtils.isEmpty(email) ? "" : email);
        //insert(table, nullColumnHack, values) : nullColumnHack은 만약 값이 없때 강제적으로 NULL문자를 넣을 column name
        db.insert(TABLE_ACCOUNT_INFO, null, values);
    }

    public void insertAccount(String id, int isChild, String name, String coin, String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ID, id);
        values.put(IS_PARENT, isChild);
        values.put(NAME, TextUtils.isEmpty(name) ? "" : AndroidUtils.convertFromUTF8(name));
        values.put(PASSWORD, "");
        values.put(COIN, TextUtils.isEmpty(coin) ? "" : coin);
        values.put(EMAIL, TextUtils.isEmpty(email) ? "" : email);

        db.insert(TABLE_ACCOUNT_INFO, null, values);
    }

    public Account getAccountInfo() {

        Account account = new Account();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] result_columns = new String[]{ID, IS_PARENT, NAME, PASSWORD, COIN, EMAIL};

        Cursor cursor = db.query(true, TABLE_ACCOUNT_INFO, result_columns, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {

            account.setID(cursor.getString(0));
            account.setIsChild(cursor.getInt(1));
            account.setName(cursor.getString(2));

            if (cursor.getString(3) != null) {
                account.setPassword(cursor.getString(3));
            }

            if (cursor.getString(4) != null) {

                account.setCoin(cursor.getString(4));
            }

            if (cursor.getString(5) != null) {

                account.setEmail(cursor.getString(5));
            }
        }
        return account;
    }

    public void insertChild(String id, int isChild, String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CHILDREN_ID, id);
        values.put(IS_PARENT, isChild);

        if (name != null) {
            values.put(NAME, AndroidUtils.convertFromUTF8(name));
        }

        db.insert(TABLE_CHILD, null, values);
    }

    public void insertChildren(List<kr.co.digitalanchor.studytime.model.Child> children) {

        for (kr.co.digitalanchor.studytime.model.Child child : children) {

            insertChild(child.getChildID(), 0, child.getName());
        }
    }

    public ArrayList<Child> getChild() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Child> children = new ArrayList<Child>();
        String[] result_columns = new String[]{CHILDREN_ID, IS_PARENT, NAME};

        Cursor cursor = db.query(true, TABLE_CHILD, result_columns, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Child child = new Child();
                child.setChildID(cursor.getString(0));
                child.setIsChild(cursor.getInt(1));
                if (cursor.getString(2) != null) {
                    child.setName(cursor.getString(2));
                }
                children.add(child);
            } while (cursor.moveToNext());
        }
        return children;

    }

    /**
     * 처음 문자를 보냈을 때 사용
     *
     * @param isGroup    : group인지 표시, 현재버전은 0만
     * @param senderID   : 보내는 사람의 ID
     * @param receiverID : 받는 사람의 ID
     * @param senderName : 보내는 사람의 이름
     * @param msg        : 메시지
     * @param time       : 보내는 시간 YYYY-MM-DD HH:MM:SS 형식
     * @param counter    : 아직 문자를 읽지 않은 사람의 숫자
     * @param isFail     : 처음 보낼 때는 0
     * @param msgType
     * @return messagePK (local DB의 chat primary key
     */
    public long insertChat(int isGroup, String senderID, String receiverID, String senderName,
                           String msg, String time, int counter, int isFail, int msgType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(IS_GROUP, isGroup);
        values.put(SENDER_ID, senderID);
        values.put(RECEIVER_ID, receiverID);
        values.put(SENDER_NAME, senderName);
        values.put(MSG, msg);
        values.put(TIMESTAMP, time);
        values.put(COUNTER, counter);
        values.put(IS_FAIL, isFail);
        values.put(MSG_TYPE, msgType);

        return db.insert(TABLE_CHAT, null, values);
    }

    /**
     * 문자를 보내고 결과를 넣은 함수
     *
     * @param messagePK : local db에 있는 message primary key 값
     * @param messageID : 서버의 message primary key 값 보낼 때는 없음
     * @param isFail    네트워크가 안좋아서 문자를 못보내거나, 전송은 하였으나 서버로부터 error를 받은 경우 1, 처음 보낼 때 0
     * @param failName  문자를 못받은 사람의 리스트, ;로 구별 ex) 정승욱;남상미;정재욱;유정효
     */
    public void updateChat(String messagePK, String messageID, int isFail, String failName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MSG_ID, messageID);
        values.put(IS_FAIL, isFail);
        if (failName != null) {
            values.put(FAIL_NAME, failName);
        }

        String where = CHAT_KEY + " = " + messagePK;
        db.update(TABLE_CHAT, values, where, null);
    }

    /**
     * GCM message를 받고 DB에 초기값을 저장하는 함수
     *
     * @param messageID
     * @param senderName
     * @param counter
     * @param receiverID
     * @param senderID
     * @param isGroup
     * @param isChild
     * @return messagePK : local message primary key
     */
    public long insertChatWithGCM(String messageID, String senderName, String counter, String receiverID, String senderID, int isGroup, int isChild) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MSG_ID, messageID);
        values.put(SENDER_NAME, senderName);
        values.put(SENDER_ID, senderID);
        values.put(RECEIVER_ID, receiverID);
        values.put(COUNTER, counter);
        values.put(IS_GROUP, isGroup);
        values.put(IS_PARENT, isChild);

        return db.insert(TABLE_CHAT, null, values);
    }

    /**
     * 보낼 때 GCM을 받고 메시지를 서버에 요청하여 메시지를 얻어왔을 때 호출하는 함수
     *
     * @param messageID
     * @param msg
     * @param msgType
     * @param counter
     * @param time
     */
    public void updateChatWithMessage(String messageID, String msg, int msgType, int counter, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MSG, msg);
        values.put(MSG_TYPE, msgType);
        values.put(COUNTER, counter);
        values.put(TIMESTAMP, time);

        String where = MSG_ID + "=" + messageID;

        db.update(TABLE_CHAT, values, where, null);
    }

    /**
     * GCM으로 counter값을 보내온 경우 호출하는 함수
     *
     * @param messageID
     * @param counter
     */
    public void updateChatWithGCMCounter(String messageID, int counter) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COUNTER, counter);

        String where = MSG_ID + "=" + messageID;

        db.update(TABLE_CHAT, values, where, null);
    }

    //TODO read chat message

    /**
     * 메시지를 읽어 오는 함수
     *
     * @return 최근부터 100개 까지만 return함
     */
    public ArrayList<ChatModel> getChat() {
        ArrayList<ChatModel> chats = new ArrayList<ChatModel>();

        String[] result_columns = new String[]{CHAT_KEY, MSG_ID, IS_GROUP, SENDER_ID, RECEIVER_ID, SENDER_NAME, MSG, TIMESTAMP, COUNTER, MSG_TYPE, IS_FAIL, FAIL_NAME};

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_CHAT, result_columns, null, null, null, null, TIMESTAMP + " DESC", "100");
        if (cursor.moveToFirst()) {
            do {
                ChatModel model = new ChatModel();
                model.setMessagePK(cursor.getString(0));
                model.setMessageID(cursor.getString(1));
                model.setIsGroup(cursor.getInt(2));
                model.setSenderID(cursor.getString(3));
                model.setReceiverID(cursor.getString(4));
                model.setSenderName(cursor.getString(5));
                model.setMsg(cursor.getString(6));
                model.setTime(cursor.getString(7));
                model.setCounter(cursor.getInt(8));
                model.setMsgType(cursor.getInt(9));
                model.setIsFail(cursor.getInt(10));
                model.setFailName(cursor.getString(11));
                chats.add(model);
            } while (cursor.moveToNext());
        }
        return chats;
    }


    /**
     * messagePK로 요청한 message만 읽어옴
     *
     * @param messagePK 로컬 DB의 primary 값
     * @return
     */
    public ChatModel getChat(String messagePK) {

        String[] result_columns = new String[]{CHAT_KEY, MSG_ID, IS_GROUP, SENDER_ID, RECEIVER_ID, SENDER_NAME, MSG, TIMESTAMP, COUNTER, MSG_TYPE, IS_FAIL, FAIL_NAME};

        String whereClause = CHAT_KEY + " = ? ";
        String[] whereArgs = new String[]{messagePK};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_CHAT, result_columns, whereClause, whereArgs, null, null, null, null);
        ChatModel model = new ChatModel();
        if (cursor.moveToFirst()) {

            model.setMessagePK(cursor.getString(0));
            model.setMessageID(cursor.getString(1));
            model.setIsGroup(cursor.getInt(2));
            model.setSenderID(cursor.getString(3));
            model.setReceiverID(cursor.getString(4));
            model.setSenderName(cursor.getString(5));
            model.setMsg(cursor.getString(6));
            model.setTime(cursor.getString(7));
            model.setCounter(cursor.getInt(8));
            model.setMsgType(cursor.getInt(9));
            model.setIsFail(cursor.getInt(10));
            model.setFailName(cursor.getString(11));

        }
        return model;
    }

    /**
     * 서버에서 보내준 messageID를 가지고 해당 메시지를 읽는 함수
     *
     * @param messageID : 서버 DB의 message primary key값
     * @return
     */
    public ChatModel getChatWithMessageID(String messageID) {

        String[] result_columns = new String[]{CHAT_KEY, MSG_ID, IS_GROUP, SENDER_ID, RECEIVER_ID, SENDER_NAME, MSG, TIMESTAMP, COUNTER, MSG_TYPE, IS_FAIL, FAIL_NAME};

        String whereClause = MSG_ID + " = ? ";
        String[] whereArgs = new String[]{messageID};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_CHAT, result_columns, whereClause, whereArgs, null, null, null, null);
        ChatModel model = new ChatModel();
        if (cursor.moveToFirst()) {

            model.setMessagePK(cursor.getString(0));
            model.setMessageID(cursor.getString(1));
            model.setIsGroup(cursor.getInt(2));
            model.setSenderID(cursor.getString(3));
            model.setReceiverID(cursor.getString(4));
            model.setSenderName(cursor.getString(5));
            model.setMsg(cursor.getString(6));
            model.setTime(cursor.getString(7));
            model.setCounter(cursor.getInt(8));
            model.setMsgType(cursor.getInt(9));
            model.setIsFail(cursor.getInt(10));
            model.setFailName(cursor.getString(11));

        }
        return model;
    }

    /**
     * messagePK 이전에 주고 받은 메시지를 받아오는 함수 100개까지만 return
     *
     * @param messagePK : localDB의 primary key 값
     * @return
     */
    public ArrayList<ChatModel> getChatBefore(String messagePK) {

        ArrayList<ChatModel> chats = new ArrayList<ChatModel>();
        String[] result_columns = new String[]{CHAT_KEY, MSG_ID, IS_GROUP, SENDER_ID, RECEIVER_ID, SENDER_NAME, MSG, TIMESTAMP, COUNTER, MSG_TYPE, IS_FAIL, FAIL_NAME};

        String whereClause = CHAT_KEY + " < ? ";
        String[] whereArgs = new String[]{messagePK};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_CHAT, result_columns, whereClause, whereArgs, null, null, TIMESTAMP + " DESC", "100");

        if (cursor.moveToFirst()) {
            do {
                ChatModel model = new ChatModel();
                model.setMessagePK(cursor.getString(0));
                model.setMessageID(cursor.getString(1));
                model.setIsGroup(cursor.getInt(2));
                model.setSenderID(cursor.getString(3));
                model.setReceiverID(cursor.getString(4));
                model.setSenderName(cursor.getString(5));
                model.setMsg(cursor.getString(6));
                model.setTime(cursor.getString(7));
                model.setCounter(cursor.getInt(8));
                model.setMsgType(cursor.getInt(9));
                model.setIsFail(cursor.getInt(10));
                model.setFailName(cursor.getString(11));
                chats.add(model);
            } while (cursor.moveToNext());
        }
        return chats;
    }

    public int getUnreadMessage(String senderId) {

        int count = 0;

        String selectQuery = "SELECT messageID FROM " + TABLE_CHAT + " WHERE " + SENDER_ID +
                " = '" + senderId + "' AND " + COUNTER + " > 0";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        count = cursor.getCount();

        cursor.close();

        return count;

    }


    /**
     * 처음으로 onOFF 정보를 넣거나 update할 때 사용
     *
     * @param isOff
     */
    public void insertOnOff(int isOff) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT OR REPLACE INTO " + TABLE_ON_OFF + "(" + ONOFF_KEY + ", " + IS_OFF + ") values ((SELECT " +
                ONOFF_KEY + "FROM " + TABLE_ON_OFF + "), " + isOff + ")";
        db.rawQuery(query, null);
    }

    /**
     * on off state를 return함
     *
     * @return if 0, then on, if 1, then off, if -1, no date
     */
    public int getOnOff() {
        String selectQuery = "SELECT " + IS_OFF + " FROM " + TABLE_ON_OFF;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    public void clearAll() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);
    }
}
