package kr.co.digitalanchor.studytime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Thomas on 2015-06-16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String NAME = "local";

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
