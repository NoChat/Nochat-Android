package com.nexters.nochat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DataManager3 {
    private final String TAG = "DataManager3";
    private Context context = null;

    private DBHelper3 helper3;
    private UserInfoVO userInfoVO;

    public DataManager3(Context context)
    {
        this.context = context;
        helper3 = new DBHelper3(this.context);
        Log.i("TAG", "디비 생성자 호출 테이블 만듬3");
    }

    // usrId(@phoneNumber,@userId) 해당

    // 저장
    public void insertUserInfo(String userId, String userName)
    {
        Log.i(TAG, "in insert userInfo");
        SQLiteDatabase db = helper3.getWritableDatabase();
        String sql = "insert into userInfo values(null, '" + userId + "', '"
                + userName + "');";
        db.execSQL(sql);
        helper3.close();
    }

    //업데이트
    public void updateUsrName(String userId, String userName)
    {
        Log.i(TAG, "in Update userInfo");
        SQLiteDatabase db = helper3.getWritableDatabase();
        String sql = "UPDATE userInfo SET userId = '"
                + userId + "', userName = '" + userName + "';";
        Log.i("DataManager", sql);
        db.execSQL(sql);
        helper3.close();
    }

    //삭제
    public void deleteAll3()
    {
        Log.i(TAG, "in delete userInfo");
        SQLiteDatabase db = helper3.getWritableDatabase();

        String sql = "delete from userInfo";
        db.execSQL(sql);
        helper3.close();
    }

    //화면에 뿌릴 UserName 조회
    public String getUserInfo(String userId)
    {
        String dbUserName = null;
        Log.i(TAG, "in getUserInfo");
        SQLiteDatabase db = helper3.getReadableDatabase();
        String sql = "select * from userInfo where userId ='"+ userId + "'";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            dbUserName = cursor.getString(2);
        }

        Log.i(TAG, "output3 ===>"+ userId +" "+ dbUserName);

        helper3.close();
        cursor.close();

        return dbUserName;

    }
}
