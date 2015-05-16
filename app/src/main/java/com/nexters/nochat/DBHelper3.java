package com.nexters.nochat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper3 extends SQLiteOpenHelper {

    private final String TAG = "DBHelper3";

    public DBHelper3(Context context) {
        super(context, "nochat3.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQLiteOpenHelper가 최초 실행 되었을 때
        Log.i(TAG, "In onCreate3");

        db.execSQL("create table userInfo(_id integer primary key autoincrement, userId text, userName text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //지금은 기존의 데이터를 모두 지우고 다시 만든다
        Log.i(TAG, "In onUpgrade2");
        db.execSQL("DROP TABLE IF EXISTS userInfo");
        onCreate(db);
    }
}
