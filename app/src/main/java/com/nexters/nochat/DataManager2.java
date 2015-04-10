package com.nexters.nochat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DataManager2 {
    private final String TAG = "DataManager2";
    private Context context = null;

    DBHelper2 helper2;
    UsrIdVO usrIdVO;

    public DataManager2(Context context)
    {
        this.context = context;
        helper2 = new DBHelper2(this.context);
        Log.i("TAG", "디비 생성자 호출 테이블 만듬2");
    }

    // usrId(@phoneNumber,@userId) 해당

    // 저장
    public void insertUsrId(String usrPn, String usrID)
    {
        Log.i(TAG, "in insert phoneId2");
        SQLiteDatabase db = helper2.getWritableDatabase();
        String sql = "insert into phoneId values(null, '" + usrPn + "', '"
                + usrID + "');";
        db.execSQL(sql);
        helper2.close();
    }

    //업데이트
    public void updateUsrId(int id, String usrPn, String usrID)
    {
        Log.i(TAG, "in Update phoneId2");
        SQLiteDatabase db = helper2.getWritableDatabase();
        String sql = "UPDATE phoneId SET id = '" + id + "', phoneNumberI = '"
                + usrPn + "', userI = '" + usrID + "';";
        Log.i("DataManager", sql);
        db.execSQL(sql);
        helper2.close();
    }

    //삭제
    public void deleteAll2()
    {
        Log.i(TAG, "in delete phoneId");
        SQLiteDatabase db = helper2.getWritableDatabase();

        String sql = "delete from phoneId";
        db.execSQL(sql);
        //db.delete("usrFriends",null,null);
        helper2.close();
    }

    //화면에 뿌릴 UserId 조회
    public ArrayList<UsrIdVO> getUsrIdInfo()
    {
        Log.i(TAG, "in getUsrIdInfo");
        ArrayList<UsrIdVO> usr_List = new ArrayList<UsrIdVO>(); //친구의 name 값만 담을 수 있는 List 생성.
        SQLiteDatabase db = helper2.getReadableDatabase();
        String sql = "select * from phoneId";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Log.i(TAG, "in while");
            int uId = cursor.getInt(0);
            String userPhoneNumber = cursor.getString(1);
            String userId = cursor.getString(2);

            Log.i(TAG, "output2 ===>" + uId + " " + userId +" "+ userPhoneNumber);
            usrIdVO = new UsrIdVO(userId);
            usr_List.add(usrIdVO);

        }
        helper2.close();
        cursor.close();

        return usr_List;

    }

}
