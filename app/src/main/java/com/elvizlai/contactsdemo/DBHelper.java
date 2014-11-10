package com.elvizlai.contactsdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Elvizlai on 14-11-10.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String dbName = "contacts.db";
    private static final int dbVersion = 1;
    public static final String nameColunm = "contacts";


    public DBHelper(Context context) {
        super(context, dbName, null, dbVersion);
        Log.d("TAG", "DBHelper(Context context)");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createName = "CREATE TABLE IF NOT EXISTS " + nameColunm + " (_id integer PRIMARY KEY AUTOINCREMENT," +
                "Name text," +
                "pinyinName text," +
                "tel text);";
        db.execSQL(createName);
        Log.d("TAG", "createName");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
