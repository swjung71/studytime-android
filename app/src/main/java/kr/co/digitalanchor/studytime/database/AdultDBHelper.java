package kr.co.digitalanchor.studytime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Thomas on 2015-08-17.
 */
public class AdultDBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;

    private static final String DB_NAME = "adult.db";

    private static final String TABLE_ADULT_URL = "adult_url_table";

    private static final String ADULT_URL_ID = "adult_url_id";
    private static final String ADULT_URL_HASH = "hash";
    private static final String ADULT_URL_DIRECTORY = "directory";
    private static final String ADULT_URL_FILE = "directory";
    private static final String ADULT_URL_IS_SUB = "isSub";


    private static final String CREATE_TABLE_ADULT_URL = "CREATE TABLE IF NOT EXISTS " + TABLE_ADULT_URL + " ("
            + ADULT_URL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ADULT_URL_HASH + " TEXT, "
            + ADULT_URL_DIRECTORY + " TEXT, "
            + ADULT_URL_FILE + " TEXT, "
            + ADULT_URL_IS_SUB + " TEXT )";

    public AdultDBHelper(Context context) {

        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            db.execSQL(CREATE_TABLE_ADULT_URL);

        } catch (SQLException e) {

            Logger.e(e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADULT_URL);

        this.onCreate(db);
    }

    public boolean isAdultURL(String hash, String directory) {

        boolean result = false;

        if (TextUtils.isEmpty(hash)) {

            return result;
        }

        if (TextUtils.isEmpty(directory)) {

            directory = "/";
        }

        Logger.d("hash " + hash + ", directory " + directory);

        Cursor cursor = null;

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            String[] columns = new String[]{ADULT_URL_DIRECTORY, ADULT_URL_FILE, ADULT_URL_IS_SUB};

            String where = ADULT_URL_HASH + " =?";

            cursor = db.query(false, TABLE_ADULT_URL, columns, where, new String[]{hash},
                    null, null, null, null);

            if (cursor.moveToFirst()) {

                do {

                    String dir = cursor.getString(0);
                    String file = cursor.getString(1);
                    String isSub = cursor.getString(2);

                    if (isSub.equals("P")) {

                        result = true;

                        break;

                    } else if (isSub.equals("S") && directory.startsWith(dir)) {

                        result = true;

                        break;

                    } else if (!file.equals("/") && isSub.equals("S") && directory.endsWith(file)) {

                        result = true;

                        break;
                    }

                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {

            Logger.e(e.toString());

            return result;

        } finally {

            if (cursor != null) {

                cursor.close();

                cursor = null;
            }
        }

        return result;
    }

    public void setTableAdultUrl(BufferedReader br) {

        Logger.d("setTableAdultUrl");

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            //BufferedReader br = new BufferedReader(new FileReader(file));

            db.beginTransaction();

            String line;

            while ((line = br.readLine()) != null) {

                //int i = 0;

                String urlHash = new String();
                String directory = new String();
                String file = new String();

                String[] subsp = line.split(",");

                if (subsp.length != 12) {
                    continue;
                }

                urlHash = subsp[0];
                directory = subsp[2];
                file = subsp[3];

                int nud, sex, vio, lan;

                try {

                    nud = Integer.parseInt(subsp[4]);
                    sex = Integer.parseInt(subsp[5]);
                    vio = Integer.parseInt(subsp[6]);
                    lan = Integer.parseInt(subsp[7]);

                } catch (Exception e) {

                    Logger.e(e.toString());

                    continue;
                }

                //for high school
                if (nud > 2 || sex > 2 || vio > 3 || lan > 2) {

                    String isDelete = subsp[10];

                    if (isDelete.equalsIgnoreCase("D")) {

                        //삭제 코드
                        String whereClause = ADULT_URL_HASH + "=" + urlHash + " AND " +
                                ADULT_URL_DIRECTORY + "=" + directory + " AND " +
                                ADULT_URL_FILE + " = " + file;

                        db.delete(TABLE_ADULT_URL, whereClause, null);

                    } else {

                        //삽입 코드
                        ContentValues values = new ContentValues();

                        values.put(ADULT_URL_HASH, urlHash);
                        values.put(ADULT_URL_DIRECTORY, directory);
                        values.put(ADULT_URL_IS_SUB, subsp[11]);
                        values.put(ADULT_URL_FILE, file);

                        db.insert(TABLE_ADULT_URL, null, values);
                    }
                }
            }

            db.setTransactionSuccessful();

        } catch (FileNotFoundException e) {

            Logger.e(e.getMessage());

        } catch (IOException e) {

            Logger.e(e.getMessage());

        } finally {

            db.endTransaction();
        }
    }
}
