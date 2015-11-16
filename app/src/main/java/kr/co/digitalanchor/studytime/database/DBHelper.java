package kr.co.digitalanchor.studytime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import kr.co.digitalanchor.studytime.model.AddPackageElement;
import kr.co.digitalanchor.studytime.model.AdultFileResult;
import kr.co.digitalanchor.studytime.model.Files;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.ChatMessage;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.utils.AndroidUtils;
import kr.co.digitalanchor.utils.MD5;

/**
 * Created by Thomas on 2015-06-16.
 */
public class DBHelper extends SQLiteOpenHelper {

  private static final int VERSION = 3;

  private static final String DB_NAME = "local";

  //table names
  private static final String TABLE_ACCOUNT_INFO = "account_table";
  private static final String TABLE_CHILD = "child_table"; //부모 studytime이면 자녀 table, 자녀 studytime이면 부모 table
  private static final String TABLE_ON_OFF = "onOff_table";
  private static final String TABLE_MESSAGE = "message_table";
  private static final String TABLE_APPLICATION_FOR_CHILD = "application_table_for_child";
  private static final String TABLE_APPLICATION_FOR_PARENT = "application_table_for_parent";
  private static final String TABLE_ADULT_FILE = "adult_file_table";
  private static final String TABLE_ADULT_URL = "adult_url_table";
  private static final String TRIGGER_NEW_MESSAGE = "new_message_trigger";


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
  private static final String NEW_NOTICE = "notice"; // 새로운 공지

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
  private static final String IS_ALLOW = "isAllow";//  0이면 삭제 허락, 1이면 삭제 못함
  private static final String EXPIRATION_DATE = "expirationDate"; // 사용 기간 만료일
  private static final String IS_EXPIRED = "isExpired"; // 만료 여부
  private static final String DEVICE_MODEL = "deviceModel"; // 자녀 모델명
  private static final String REMAINING_DAYS = "remainingDays"; // 잔여기간
  private static final String ONOFF_PK = "1234";
  private static final String NEW_MESSAGE_COUNT = "newMessageCount";

  // Application table for child
  private static final String PACKAGE_ID = "id"; // 서버에서 부여받는 ID
  private static final String PACKAGE_HASH = "hash"; // 패키지 네임을 MD5로 인코딩
  private static final String PACKAGE_NAME = "package"; // 패키지 네임
  private static final String LABEL_NAME = "label"; // 런처에 표시되는 이름
  private static final String PACKAGE_VERSION = "version"; // APP 버전
  //private static final String TIMESTAMP = "time"; // 최근 설치/삭제/업데이트 시간
  private static final String EXCEPTED = "excepted"; // 예외앱 1, 차단 앱 0
  private static final String IS_DEFAULT = "isDefault"; // 프리로드 앱 1, 반대는 0
  private static final String STATE = "state"; // 설치 0, 삭제 1. 업데이트 2
  private static final String HAS_ICON = "hasIcon"; // 아이콘을 가지고 있으면 1, 반대는 0
  private static final String HAS_ICON_IN_DB = "hasIconInDB"; // 서버에 아이콘이 있으면 1, 없으면 0
  private static final String ICON_HASH = "imageName"; // 아이콘 파일 이름
  private static final String CHANGED = "changed"; // 변경사항이 있으면 1, 아니면 0

  private static final String ADULT_FILE = "fileName";//차단 웹 사이트 목록 파일 이름
  private static final String ADULT_FILE_DATE = "date";// 차단 웹 사이트 목록 파일 날짜

  private static final String ADULT_URL_ID = "adult_url_id";
  private static final String ADULT_URL_HASH = "hash";
  private static final String ADULT_URL_DIRECTORY = "directory";
  private static final String ADULT_URL_IS_SUB = "isSub";

  private static final String CREATE_TABLE_APP_FOR_CHILD = "CREATE TABLE " + TABLE_APPLICATION_FOR_CHILD + " ("
      + PACKAGE_NAME + " TEXT PRIMARY KEY, "
      + PACKAGE_ID + " TEXT, "
      + PACKAGE_HASH + " TEXT NOT NULL, "
      + LABEL_NAME + " TEXT NOT NULL, "
      + PACKAGE_VERSION + " TEXT NOT NULL, "
      + EXCEPTED + " INTEGER DEFAULT 0, "
      + IS_DEFAULT + " INTEGER DEFAULT 0, "
      + TIMESTAMP + " TEXT NOT NULL, "
      + STATE + " INTEGER DEFAULT 0, "
      + HAS_ICON + " INTEGER DEFAULT 1, "
      + HAS_ICON_IN_DB + " INTEGER DEFAULT 0, "
      + ICON_HASH + " TEXT , "
      + CHANGED + " INTEGER DEFAULT 0 ) ";

  private static final String CREATE_TABLE_ADULT_FILE = "CREATE TABLE  " + TABLE_ADULT_FILE + " ("
      + ADULT_FILE + " TEXT, "
      + ADULT_FILE_DATE + " TEXT )";

  private static final String CREATE_TABLE_ADULT_URL = "CREATE TABLE  " + TABLE_ADULT_URL + " ("
      + ADULT_URL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + ADULT_URL_HASH + " TEXT, "
      + ADULT_URL_DIRECTORY + " TEXT, "
      + ADULT_URL_IS_SUB + " TEXT )";

  //IS_PARENT 가 0이면 CHILD_ID는 자녀 ID, 1이면 parentID, 2이면 teacherID(향후 버전), NAME은 자녀 이름, 부모인 경우 이름을 저장하지 않음 (향후 버전)
  private static final String CREATE_TABLE_CHILD = "CREATE TABLE " + TABLE_CHILD + "("
      + CHILDREN_ID + " INTEGER PRIMARY KEY,"
      + IS_PARENT + " INTEGER NOT NULL, "
      + NAME + " TEXT, "
      + IS_OFF + " TEXT, "
      + EXPIRATION_DATE + " TEXT, "
      + DEVICE_MODEL + " TEXT, "
      + REMAINING_DAYS + " INTEGER,"
      + IS_EXPIRED + " TEXT DEFAULT Y, "
      + NEW_MESSAGE_COUNT + " INTEGER DEFAULT 0)";

  private static final String CREATE_TABLE_ACCOUNT_INFO = "CREATE TABLE " + TABLE_ACCOUNT_INFO + "("
      + ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + ID + " INTEGER NOT NULL, "
      + IS_PARENT + " TEXT NOT NULL, "
      + NAME + " TEXT, "
      + PASSWORD + " TEXT NOT NULL, "
      + COIN + " TEXT NOT NULL, "
      + EMAIL + " TEXT NOT NULL, "
      + PARENT_ID + " TEXT, "
      + IS_EXPIRED + " TEXT DEFAULT Y, "
      + NEW_NOTICE + " INTEGER DEFAULT 0 )";


  public DBHelper(Context context) {
    super(context, DB_NAME, null, VERSION);

  }

  @Override
  public void onCreate(SQLiteDatabase db) {

    String CREATE_TABLE_ONOFF = "CREATE TABLE " + TABLE_ON_OFF
        + "(" + ONOFF_KEY + " INTEGER PRIMARY KEY, " + IS_OFF
        + " INTEGER DEFAULT 0, " + IS_ALLOW + " INTEGER DEFAULT 1)";

    // TIMESTAMP 는 YYYY-MM-DD HH:MM:SS 형태로 저장
    String CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE + " ( "
        + CHAT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + ROOM_ID + " INTEGER, "
        + MSG_ID + " INTEGER, "
        + SENDER_ID + " INTEGER, "
        + GUEST_ID + " INTEGER, "
        + GUEST_NAME + " TEXT, "
        + TIMESTAMP + " INTEGER, "
        + MSG + " TEXT, "
        + UNREAD_COUNT + " INTEGER, "
        + IS_GROUP + " INTEGER, "
        + IS_FAIL + " INTEGER, "
        + MSG_TYPE + " INTEGER, "
        + FAIL_NAME + " TEXT )";


    String CREATE_TRIGGER_MESSAGE = "CREATE TRIGGER " + TRIGGER_NEW_MESSAGE
        + " AFTER INSERT ON " + TABLE_MESSAGE
        + " BEGIN UPDATE " + TABLE_CHILD
        + " SET " + NEW_MESSAGE_COUNT + " = " + NEW_MESSAGE_COUNT + " + 1 "
        + " WHERE " + CHILDREN_ID + " = new." + SENDER_ID + "; END";

    db.execSQL(CREATE_TABLE_ACCOUNT_INFO);
    db.execSQL(CREATE_TABLE_CHILD);
    db.execSQL(CREATE_TABLE_MESSAGE);
    db.execSQL(CREATE_TABLE_ONOFF);
    db.execSQL(CREATE_TABLE_APP_FOR_CHILD);
    db.execSQL(CREATE_TABLE_ADULT_FILE);
    db.execSQL(CREATE_TABLE_ADULT_URL);
    db.execSQL(CREATE_TRIGGER_MESSAGE);

    ContentValues values = new ContentValues();
    values.put(ONOFF_KEY, ONOFF_PK);

    db.replace(TABLE_ON_OFF, null, values);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    switch (oldVersion) {

      case 1:

        db.execSQL(CREATE_TABLE_APP_FOR_CHILD);
        db.execSQL(CREATE_TABLE_ADULT_FILE);
        db.execSQL(CREATE_TABLE_ADULT_URL);

        break;


      case 2:

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_INFO);
        db.execSQL(CREATE_TABLE_ACCOUNT_INFO);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);
        db.execSQL(CREATE_TABLE_CHILD);

        break;
    }
  }

  public void clearAll() {

    SQLiteDatabase db = this.getWritableDatabase();

    db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATION_FOR_CHILD);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_INFO);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ON_OFF);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADULT_FILE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADULT_URL);

    onCreate(db);
  }

  /**
   * @param packages
   */
  public void addAppList(List<AddPackageElement> packages) {

    for (AddPackageElement element : packages) {

      Logger.d(element.getPackageName() + " " + element.getLabelName());

      addApp(element);
    }

  }

  public void addApp(AddPackageElement model) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    if (!TextUtils.isEmpty(model.getHash()))
      values.put(PACKAGE_HASH, model.getHash());

    if (!TextUtils.isEmpty(model.getPackageName()))
      values.put(PACKAGE_NAME, model.getPackageName());

    if (!TextUtils.isEmpty(model.getLabelName()))
      values.put(LABEL_NAME, model.getLabelName());

    if (!TextUtils.isEmpty(model.getPackageVersion()))
      values.put(PACKAGE_VERSION, model.getPackageVersion());

    else
      values.put(PACKAGE_VERSION, "1.0");

    if (!TextUtils.isEmpty(model.getTimestamp()))
      values.put(TIMESTAMP, model.getTimestamp());

    if (model.getIsDefaultApp() > -1)
      values.put(IS_DEFAULT, model.getIsDefaultApp());

    if (model.getIsExceptionApp() > -1)
      values.put(IS_DEFAULT, model.getIsExceptionApp());

    if (model.getIsDefaultApp() > -1)
      values.put(IS_DEFAULT, model.getIsDefaultApp());

    db.replace(TABLE_APPLICATION_FOR_CHILD, null, values);
    db.close();
  }

  /**
   * App 정보 추가 자녀용
   *
   * @param packageName 패키지 네임
   * @param hash 패키지 네임을 MD5로 인코딩
   * @param label 런처에 표시되는 앱 이름
   * @param timestamp 최근 설치/삭제/업데이트 시간
   * @param excepted 예외앱 1, 차단 앱 0
   * @param isDefault 프리로드 앱 1, 반대는 0
   * @param iconHash 아이콘 파일 이름
   */
  public void addApplication(String packageName, String hash, String label, String version,
                             String timestamp, int excepted, int isDefault, String iconHash,
                             int state, int changed) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    if (!TextUtils.isEmpty(packageName))
      values.put(PACKAGE_NAME, packageName);

    if (TextUtils.isEmpty(hash)) {

      hash = MD5.getHash(packageName);
    }

    values.put(PACKAGE_HASH, hash);

    if (!TextUtils.isEmpty(label))
      values.put(LABEL_NAME, label);

    if (!TextUtils.isEmpty(version))
      values.put(PACKAGE_VERSION, version);

    else
      values.put(PACKAGE_VERSION, "1.0");

    if (!TextUtils.isEmpty(timestamp))
      values.put(TIMESTAMP, timestamp);

    if (excepted > -1)
      values.put(EXCEPTED, excepted);

    if (isDefault > -1)
      values.put(IS_DEFAULT, isDefault);

    if (!TextUtils.isEmpty(iconHash))
      values.put(ICON_HASH, iconHash);

    if (state > -1)
      values.put(STATE, state);

    if (changed > -1)
      values.put(CHANGED, changed);

    db.replace(TABLE_APPLICATION_FOR_CHILD, null, values);
    db.close();
  }

  /**
   * App 정보 추가 자녀용
   */
  public void addApplications(List<PackageModel> packages) {

    if (packages == null) {

      return;
    }

    for (PackageModel model : packages) {

      addApplication(model.getPackageName(), model.getHash(), model.getLabelName(),
          model.getPackageVersion(), model.getTimestamp(), model.getIsExceptionApp(),
          model.getIsDefaultApp(), model.getIconHash(), model.getState(), model.getChanged());
    }
  }

  /**
   * App을 삭제 했을때 (자녀용)
   */
  public void deleteApplication(String packageName) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(STATE, 1);
    values.put(CHANGED, 1);

    db.update(TABLE_APPLICATION_FOR_CHILD, values, PACKAGE_NAME + "=?", new String[]{packageName});
    db.close();

  }

  public List<PackageModel> getDeletedApps() {

    SQLiteDatabase db = this.getWritableDatabase();

    List<PackageModel> results = new ArrayList<>();

    String[] result_columns = new String[]{PACKAGE_NAME, PACKAGE_ID, PACKAGE_HASH,
        LABEL_NAME, PACKAGE_VERSION, EXCEPTED, IS_DEFAULT, TIMESTAMP, STATE,
        HAS_ICON, HAS_ICON_IN_DB, ICON_HASH, CHANGED};


    Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, STATE +
            "=? AND " + CHANGED + "=?",
        new String[]{"1", "1"}, null, null, null, null);

    if (cursor.moveToFirst()) {

      do {

        PackageModel model = new PackageModel();

        model.setPackageName(cursor.getString(0));
        model.setPackageId(cursor.getString(1));
        model.setHash(cursor.getString(2));
        model.setLabelName(cursor.getString(3));
        model.setPackageVersion(cursor.getString(4));
        model.setIsExceptionApp(cursor.getInt(5));
        model.setIsDefaultApp(cursor.getInt(6));
        model.setTimestamp(cursor.getString(7));
        model.setState(cursor.getInt(8));
        model.setHasIcon(cursor.getInt(9));
        model.setHasIconDB(cursor.getInt(10));
        model.setIconHash(cursor.getString(11));
        model.setChanged(cursor.getInt(12));

        results.add(model);

      } while (cursor.moveToNext());
    }

    if (cursor != null) {

      cursor.close();
    }

    return results;
  }


  /**
   * App 정보를 서버에 등록 후 실행 (자녀용)
   */
  public void updateApplicationAfterReg(String packageName, String packageId,
                                        int hasIconServer, int state, int changed) {

    Logger.d(packageName + " " + packageId + " " + hasIconServer + " " + state + " " + changed);

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(PACKAGE_ID, packageId);

    if (hasIconServer > -1)
      values.put(HAS_ICON_IN_DB, hasIconServer);

    if (state > -1)
      values.put(STATE, state);

    if (changed > -1)
      values.put(CHANGED, changed);

    Logger.d(values.toString());

    db.update(TABLE_APPLICATION_FOR_CHILD, values, PACKAGE_NAME + "=?", new String[]{packageName});

    db.close();
  }


  /**
   * @param packageName
   * @return
   */
  public boolean isExcepted(String packageName) {

    boolean result = true;

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{EXCEPTED};

    Cursor cursor = null;

    try {

      cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, PACKAGE_NAME + "=?",
          new String[]{packageName}, null, null, null, null);

      if (cursor.moveToFirst()) {

        do {

          result = (cursor.getInt(0) == 0) ? false : true;

//                    Logger.d(packageName + " " + result);

        } while (cursor.moveToNext());
      }

    } catch (Exception e) {


    } finally {

      if (cursor != null)
        cursor.close();

      cursor = null;

    }

    return result;
  }

  public boolean isExceptedForService(String packageName) {

    boolean result = true;

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{EXCEPTED};

    Cursor cursor = null;

    try {

      cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, PACKAGE_NAME + "=?",
          new String[]{packageName}, null, null, null, null);

      if (cursor.moveToFirst()) {

        do {

          result = (cursor.getInt(0) == 0) ? false : true;

          Logger.d(packageName + " " + result);

        } while (cursor.moveToNext());
      }

    } catch (Exception e) {


    } finally {

      if (cursor != null)
        cursor.close();

      cursor = null;

    }

    return result;
  }

  public HashMap<String, PackageModel> getPackageStateList() {

    HashMap<String, PackageModel> hash = new HashMap<>();

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{PACKAGE_NAME, PACKAGE_ID, PACKAGE_HASH,
        LABEL_NAME, PACKAGE_VERSION, EXCEPTED, IS_DEFAULT, TIMESTAMP, STATE,
        HAS_ICON, HAS_ICON_IN_DB, ICON_HASH, CHANGED};

//        Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, STATE + " != ?",
//                new String[]{"1"}, null, null, null, null);

    Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, null,
        null, null, null, null, null);

    if (cursor.moveToFirst()) {

      do {

        PackageModel model = new PackageModel();

        model.setPackageName(cursor.getString(0));
        model.setPackageId(cursor.getString(1));
        model.setHash(cursor.getString(2));
        model.setLabelName(cursor.getString(3));
        model.setPackageVersion(cursor.getString(4));
        model.setIsExceptionApp(cursor.getInt(5));
        model.setIsDefaultApp(cursor.getInt(6));
        model.setTimestamp(cursor.getString(7));
        model.setState(cursor.getInt(8));
        model.setHasIcon(cursor.getInt(9));
        model.setHasIconDB(cursor.getInt(10));
        model.setIconHash(cursor.getString(11));
        model.setChanged(cursor.getInt(12));

        hash.put(model.getPackageName(), model);

      } while (cursor.moveToNext());
    }

    if (cursor != null) {

      cursor.close();
    }

    return hash;
  }

  public List<AddPackageElement> getAddPackageList() {

    List<AddPackageElement> list = new ArrayList<>();

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{PACKAGE_NAME, PACKAGE_HASH,
        LABEL_NAME, PACKAGE_VERSION, EXCEPTED, IS_DEFAULT, TIMESTAMP,
        HAS_ICON};

    Cursor cursor = null;

    try {

      cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, null,
          null, null, null, LABEL_NAME + " ASC", null);

      if (cursor.moveToFirst()) {

        do {

          AddPackageElement model = new AddPackageElement();

          model.setPackageName(cursor.getString(0));
          model.setHash(cursor.getString(1));
          model.setLabelName(cursor.getString(2));
          model.setPackageVersion(cursor.getString(3));
          model.setIsExceptionApp(cursor.getInt(4));
          model.setIsDefaultApp(cursor.getInt(5));
          model.setTimestamp(cursor.getString(6));
          model.setHasIcon(cursor.getInt(7));

          list.add(model);

        } while (cursor.moveToNext());

      }
    } catch (Exception e) {


    } finally {

      if (cursor != null) {

        cursor.close();
      }

      cursor = null;

    }

    return list;
  }

  /**
   * DB에 등록된 앱
   */
  public List<PackageModel> getPackageList() {

    List<PackageModel> list = new ArrayList<>();

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{PACKAGE_NAME, PACKAGE_ID, PACKAGE_HASH,
        LABEL_NAME, PACKAGE_VERSION, EXCEPTED, IS_DEFAULT, TIMESTAMP, STATE,
        HAS_ICON, HAS_ICON_IN_DB, ICON_HASH};

    Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, null,
        null, null, null, LABEL_NAME + " ASC", null);

    Logger.d(cursor.getCount() + "count");

    if (cursor.moveToFirst()) {

      do {

        PackageModel model = new PackageModel();

        model.setPackageName(cursor.getString(0));
        model.setPackageId(cursor.getString(1));
        model.setHash(cursor.getString(2));
        model.setLabelName(cursor.getString(3));
        model.setPackageVersion(cursor.getString(4));
        model.setIsExceptionApp(cursor.getInt(5));
        model.setIsDefaultApp(cursor.getInt(6));
        model.setTimestamp(cursor.getString(7));
        model.setState(cursor.getInt(8));
        model.setHasIcon(cursor.getInt(9));
        model.setHasIconDB(cursor.getInt(10));
        model.setIconHash(cursor.getString(11));

        list.add(model);

      } while (cursor.moveToNext());
    }

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

    return list;
  }

  public PackageModel getPackage(String packageName) {

    PackageModel model = null;

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{PACKAGE_NAME, PACKAGE_ID, PACKAGE_HASH,
        LABEL_NAME, PACKAGE_VERSION, EXCEPTED, IS_DEFAULT, TIMESTAMP, STATE,
        HAS_ICON, HAS_ICON_IN_DB, ICON_HASH};

    Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, PACKAGE_NAME + "=?",
        new String[]{packageName}, null, null, null, null);

    if (cursor.moveToFirst()) {

      model = new PackageModel();

      model.setPackageName(cursor.getString(0));
      model.setPackageId(cursor.getString(1));
      model.setHash(cursor.getString(2));
      model.setLabelName(cursor.getString(3));
      model.setPackageVersion(cursor.getString(4));
      model.setIsExceptionApp(cursor.getInt(5));
      model.setIsDefaultApp(cursor.getInt(6));
      model.setTimestamp(cursor.getString(7));
      model.setState(cursor.getInt(8));
      model.setHasIcon(cursor.getInt(9));
      model.setHasIconDB(cursor.getInt(10));
      model.setIconHash(cursor.getString(11));
    }

    return model;
  }

  public int getPackageListSize() {

    int size = 0;

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{PACKAGE_NAME};

    Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, result_columns, null,
        null, null, null, null, null);

    size = cursor.getCount();

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

    return size;
  }

  public void setExceptPackages(List<PackageModel> packages) {

    for (PackageModel model : packages) {

      setExceptPackage(model);
    }
  }

  public void setExceptPackage(PackageModel packageModel) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(EXCEPTED, packageModel.getIsExceptionApp());

    db.update(TABLE_APPLICATION_FOR_CHILD, values, PACKAGE_NAME + "=?",
        new String[]{packageModel.getPackageName()});

    if (db != null) {

      db.close();

      db = null;
    }

  }

  public List<PackageModel> getPackageListExcept() {

    List<PackageModel> packages = new ArrayList<>();

    SQLiteDatabase db = this.getReadableDatabase();

    String[] columns = new String[]{PACKAGE_NAME, PACKAGE_ID, EXCEPTED};

    Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, columns,
        null, null, null, null, null, null);

    if (cursor.moveToFirst()) {

      do {

        PackageModel model = new PackageModel();

        model.setPackageName(cursor.getString(0));
        model.setPackageId(cursor.getString(1));
        model.setIsExceptionApp(cursor.getInt(2));

        packages.add(model);

      } while (cursor.moveToNext());
    }

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

    return packages;
  }

  public List<PackageModel> getPackageListNoIcon() {

    List<PackageModel> packages = new ArrayList<>();

    SQLiteDatabase db = this.getReadableDatabase();

    String[] columns = new String[]{PACKAGE_NAME, PACKAGE_HASH, ICON_HASH,
        PACKAGE_VERSION, CHANGED};

    String[] params = new String[]{"0"};

    Cursor cursor = db.query(true, TABLE_APPLICATION_FOR_CHILD, columns,
        HAS_ICON_IN_DB + "=?", params,
        null, null, null, null);

    if (cursor.moveToFirst()) {

      do {

        PackageModel model = new PackageModel();

        model.setPackageName(cursor.getString(0));
        model.setHash(cursor.getString(1));
        model.setIconHash(cursor.getString(2));
        model.setPackageVersion(cursor.getString(3));
        model.setChanged(cursor.getInt(4));

        packages.add(model);

      } while (cursor.moveToNext());
    }

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

    return packages;
  }

  /**
   * 부모용
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

    if (db != null) {

      db.close();

      db = null;
    }
  }

  /**
   * 자녀용
   */
  public void insertAccount(String id, String name, String parentId, @Nullable String isExpired) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(ID, id);
    values.put(IS_PARENT, 0);
    values.put(NAME, TextUtils.isEmpty(name) ? "" : name);
    values.put(PARENT_ID, parentId);
    values.put(IS_EXPIRED, isExpired);
    values.put(PASSWORD, "");
    values.put(COIN, "");
    values.put(EMAIL, "");

    db.insert(TABLE_ACCOUNT_INFO, null, values);


    if (db != null) {

      db.close();

      db = null;
    }
  }

  public void updateAccount(String id, int isChild, String name, int coin, String email) {

    Logger.d(name + " " + coin);

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(IS_PARENT, isChild);
    values.put(NAME, TextUtils.isEmpty(name) ? "" : name);
    values.put(PASSWORD, "");
    values.put(COIN, coin);
    values.put(EMAIL, TextUtils.isEmpty(email) ? "" : email);

    db.update(TABLE_ACCOUNT_INFO, values, ID + "=?", new String[]{id});

    if (db != null) {

      db.close();

      db = null;
    }
  }

  public void updateCoin(String id, int coin) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(COIN, coin);

    db.update(TABLE_ACCOUNT_INFO, values, ID + "=?", new String[]{id});

    if (db != null) {

      db.close();

      db = null;
    }
  }


  public Account getAccountInfo() {

    Account account = new Account();

    Cursor cursor = null;

    try {

      SQLiteDatabase db = this.getReadableDatabase();

      String[] result_columns = new String[]{ID, IS_PARENT, NAME, PASSWORD, COIN, EMAIL,
          PARENT_ID, NEW_NOTICE, IS_EXPIRED};

      cursor = db.query(true, TABLE_ACCOUNT_INFO, result_columns, null, null, null, null,
          null, null);

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

        account.setNotice(cursor.getInt(7));
      }

    } catch (Exception e) {

      Logger.e(e.toString());

    } finally {

      if (cursor != null) {

        cursor.close();
      }

      cursor = null;

    }

    return account;
  }

  public void insertChild(String id, String name) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(CHILDREN_ID, id);
    values.put(IS_PARENT, 0);

    if (name != null) {
      values.put(NAME, name);
    }

    db.replace(TABLE_CHILD, null, values);

    if (db != null) {

      db.close();

      db = null;
    }

  }

  public void insertChild(String id, int isChild, String name, String isOff,
                          String expirationDate, String isExpired, String deviceModel) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(CHILDREN_ID, id);
    values.put(IS_PARENT, isChild);

    if (name != null) {
      values.put(NAME, AndroidUtils.convertFromUTF8(name));
    }

    if (TextUtils.isEmpty(isOff)) {

      values.put(IS_OFF, "0");

    } else {

      values.put(IS_OFF, isOff);
    }

    if (!TextUtils.isEmpty(expirationDate)) {

      values.put(EXPIRATION_DATE, expirationDate);
    }

    if (TextUtils.isEmpty(isExpired)) {

      values.put(IS_EXPIRED, "Y");

    } else {

      values.put(IS_EXPIRED, isExpired);

    }

    if (!TextUtils.isEmpty(deviceModel)) {

      values.put(DEVICE_MODEL, deviceModel);
    }

    db.replace(TABLE_CHILD, null, values);

    if (db != null) {

      db.close();

      db = null;
    }

  }

  public void insertChildren(List<kr.co.digitalanchor.studytime.model.Child> children) {

    if (children == null)
      return;

    for (kr.co.digitalanchor.studytime.model.Child child : children) {

      insertChild(child.getChildID(), 0, child.getName(), child.getIsOff(),
          child.getExpirationDate(), child.getExpirationYN(), child.getDeviceModel());
    }
  }

  public ArrayList<Child> getChildren() {

    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Child> children = new ArrayList<Child>();
    String[] result_columns = new String[]{CHILDREN_ID, IS_PARENT, NAME, IS_OFF, NEW_MESSAGE_COUNT,
        EXPIRATION_DATE, IS_EXPIRED, DEVICE_MODEL, REMAINING_DAYS};

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

        child.setExpirationDate(cursor.getString(5));

        child.setIsExpired(cursor.getString(6));

        child.setDeviceModel(cursor.getString(7));

        child.setRemainingDays(cursor.getInt(8));

        children.add(child);

      } while (cursor.moveToNext());
    }

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

    return children;

  }

  public Child getChild(String childId) {

    Child child = null;

    SQLiteDatabase db = this.getReadableDatabase();

    child = new Child();

    String[] result_columns = new String[]{CHILDREN_ID, IS_PARENT, NAME, IS_OFF,
        NEW_MESSAGE_COUNT, EXPIRATION_DATE, IS_EXPIRED, DEVICE_MODEL, REMAINING_DAYS};

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

        child.setExpirationDate(cursor.getString(5));

        child.setIsExpired(cursor.getString(6));

        child.setDeviceModel(cursor.getString(7));

        child.setRemainingDays(cursor.getInt(8));

      } while (cursor.moveToNext());
    }

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

    return child;
  }

  /**
   * 자녀의 on/off 상태를 업데이트 한다. 0 : on, 1 : off
   */
  public void updateChildToggle(String childId, int isOff) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(IS_OFF, isOff);

    db.update(TABLE_CHILD, values, CHILDREN_ID + "=?", new String[]{childId});

    if (db != null) {

      db.close();

      db = null;
    }
  }

  /**
   * 자녀에게서 온 새메시지 갯수를 0 으로 초기화한다.
   */
  public void initMessageCount(String childId) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put(CHILDREN_ID, childId);
    values.put(NEW_MESSAGE_COUNT, 0);

    db.update(TABLE_CHILD, values, CHILDREN_ID + "=?", new String[]{childId});

    if (db != null) {

      db.close();

      db = null;
    }
  }

  /**
   * 처음 메시지를 보내긴 전에
   *
   * @param guestId : 상대방 ID
   * @param guestName : 상대방 이름
   * @param senderId : 메시지 보낸 사람
   * @param msg : 메시지
   * @param time : 보내는 시간 YYYY-MM-DD HH:MM:SS 형식
   * @param unreadCnt :안 읽은 사람 수 (현재는 1 부터)
   * @param isFail : 성공 여부 (처음은 0, 실패 시 1)
   * @param msgType : 메시지 타입 (현재는 0)
   * @param isGroup : 그룹 채팅방인지
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
   * @param isFail : 네트워크가 안좋아서 문자를 못보내거나, 전송은 하였으나 서버로부터 error를 받은 경우 1, 처음 보낼 때 0
   * @param failName : 문자를 못받은 사람의 리스트, ;로 구별 ex) 정승욱;남상미;정재욱;유정효
   */
  public void updateMessageAfterSend(String messagePK, String messageID,
                                     int isFail, String failName) {

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

    if (db != null) {

      db.close();

      db = null;
    }
  }

  /**
   * GCM message를 받고 DB에 초기값을 저장하는 함수
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

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

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

    if (cursor != null) {

      cursor.close();
    }

    cursor = null;

    return messages;
  }

  public void setAdultFile(AdultFileResult model) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    if (model.getFileName().size() != 0) {

      ArrayList<Files> files = model.getFileName();

      for (Files file : files) {

        values.put(ADULT_FILE, file.getFileName());
        values.put(ADULT_FILE_DATE, AndroidUtils.getCurrentTime4Chat());

        db.insert(TABLE_ADULT_FILE, null, values);
      }
    }
  }

  public String getAdultFile() {

    String result = null;

    SQLiteDatabase db = null;

    Cursor rows = null;

    try {

      db = this.getReadableDatabase();

      String sql = "SELECT " + ADULT_FILE_DATE + " FROM " + TABLE_ADULT_FILE + " ORDER BY "
          + ADULT_FILE_DATE + " DESC LIMIT 1;";


      rows = db.rawQuery(sql, null);

      if (rows.moveToFirst()) {

        do {
          result = rows.getString(0);

        } while (rows.moveToNext());
      }

    } finally {

      if (rows != null) {

        rows.close();
      }
    }

    return result;
  }

  public boolean selectAdultURL() {

    boolean result = false;

    SQLiteDatabase db = this.getReadableDatabase();

    String[] result_columns = new String[]{ADULT_URL_HASH, ADULT_URL_DIRECTORY, ADULT_URL_IS_SUB};

    Cursor cursor = db.query(true, TABLE_ADULT_URL, result_columns, null, null,
        null, null, null, null);

    String isSub = null;
    String dir = null;

    if (cursor.moveToFirst()) {

      do {

        Logger.d(cursor.getString(0) + " " + cursor.getString(1));

      } while (cursor.moveToNext());

    }

    if (cursor != null) {

      cursor.close();

      cursor = null;
    }

    return result;
  }

  public void updateExpired(String isExpired) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(ONOFF_KEY, ONOFF_PK);
    values.put(IS_EXPIRED, isExpired);

    db.replace(TABLE_ON_OFF, null, values);

    if (db != null) {

      db.close();

      db = null;
    }
  }

  /**
   * 처음으로 onOFF 정보를 넣거나 update할 때 사용
   */
  public void updateOnOff(int isOff) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(ONOFF_KEY, ONOFF_PK);
    values.put(IS_OFF, isOff);
    values.put(IS_ALLOW, "1");

    db.replace(TABLE_ON_OFF, null, values);

    if (db != null) {

      db.close();

      db = null;
    }
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

    SQLiteDatabase db = null;

    try {

      db = this.getReadableDatabase();

      String[] columns = new String[]{IS_OFF};

      cursor = db.query(true, TABLE_ON_OFF, columns, ONOFF_KEY + "=?",
          new String[]{ONOFF_PK}, null, null, null, null);

      if (cursor.moveToFirst()) {
        return cursor.getInt(0);
      }

      return 0;


    } catch (Exception e) {

      return 0;

    } finally {

      if (cursor != null) {

        cursor.close();
      }

      cursor = null;

    }
  }

  public void updateAllow(int w) {

    Logger.d("test");

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(ONOFF_KEY, ONOFF_PK);
    values.put(IS_OFF, getOnOff());
    values.put(IS_ALLOW, w);

    db.replace(TABLE_ON_OFF, null, values);

    if (db != null) {

      db.close();

      db = null;
    }
  }

  /**
   * 삭제 가능한지
   *
   * @return 0 삭제 가능, 1 삭제 불가
   */
  public int isAllow() {

    Cursor cursor = null;

    SQLiteDatabase db = null;

    try {

      db = this.getReadableDatabase();

      String[] columns = new String[]{IS_ALLOW};

      cursor = db.query(true, TABLE_ON_OFF, columns, ONOFF_KEY + "=?",
          new String[]{ONOFF_PK}, null, null, null, null);

      if (cursor.moveToFirst()) {
        return cursor.getInt(0);
      }

      return -1;

    } catch (Exception e) {

      return -1;

    } finally {

      if (cursor != null) {

        cursor.close();
      }

      cursor = null;

    }
  }

  public void initNoticeCount(String id) {

    SQLiteDatabase db = null;

    try {

      db = getWritableDatabase();

      db.execSQL("UPDATE " + TABLE_ACCOUNT_INFO + " SET " + NEW_NOTICE + " = 0 "
          + "WHERE " + TABLE_ACCOUNT_INFO + "." + ID + " = " + id);

    } catch (Exception e) {


    } finally {

      if (db != null) {

        db.close();

        db = null;
      }
    }
  }

  public void addNewNotice(String id) {

    SQLiteDatabase db = null;

    try {

      db = getWritableDatabase();

      db.execSQL("UPDATE " + TABLE_ACCOUNT_INFO + " SET " + NEW_NOTICE + " = " + NEW_NOTICE + " + 1 "
          + "WHERE " + TABLE_ACCOUNT_INFO + "." + ID + " = " + id);

    } catch (Exception e) {

      Logger.e(e.toString());
    } finally {

      if (db != null) {

        db.close();

        db = null;
      }
    }
  }
}
