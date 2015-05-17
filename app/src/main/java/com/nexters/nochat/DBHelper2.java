package com.nexters.nochat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper2 extends SQLiteOpenHelper {

    private final String TAG = "DBHelper2";

    public DBHelper2(Context context) {
        super(context, "nochat2.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQLiteOpenHelper가 최초 실행 되었을 때
        Log.i(TAG, "In onCreate2");

        db.execSQL("create table phoneId(_id integer primary key autoincrement, phoneNumberI text, userI text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "In onUpgrade2");
        db.execSQL("DROP TABLE IF EXISTS phoneId");
        onCreate(db);
    }
}
