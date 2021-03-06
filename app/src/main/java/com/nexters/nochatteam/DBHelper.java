package com.nexters.nochatteam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, "nochat.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQLiteOpenHelper가 최초 실행 되었을 때
        Log.i(TAG, "In onCreate");
        String sql = "create table usrFriends(" +
                "_id integer primary key autoincrement," +
                "usr_phoneNumber text," +
                "usr_name text);" ;
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "In onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS usrFriends");
        onCreate(db);
    }
}
