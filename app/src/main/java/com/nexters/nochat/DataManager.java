package com.nexters.nochat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/*
1. Map에서 key, value로 뽑아서 String형태로 DB에 직접 넣는다
2. VO에서 세팅한다.
3. select * from로 조회할때 VO를 ArrayList에 cursor.moveToNext() 이용해 return타입을 List로 받는다
4. 뿌려질 화면에서 DBManager.함수()로 List를 받는다
*/
public class DataManager {
    private final String TAG = "DataManager";
    private Context context = null;

    private DBHelper helper;
    private UsrFriendsVO usrFriendsVO;
    private UsrIdVO usrIdVO;

    public DataManager(Context context)
    {
        this.context = context;
        helper = new DBHelper(this.context);
        Log.i("TAG", "디비 생성자 호출 테이블 만듬");
    }

    //저장
    public void insertUsrFriends(String usr_PN, String usr_N)
    {
        Log.i(TAG, "in insert Usr_Friends");
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into usrFriends values(null, '" + usr_PN + "', '"
                + usr_N + "');";
        db.execSQL(sql);
        helper.close();
    }

    //업데이트
    public void updateUsrFriends(String usr_PN, String usr_N)
    {
        Log.i(TAG, "in Update Usr_Friends");
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "UPDATE usrFriends SET usr_PhoneNumber = '"
                + usr_PN + "', usr_Name = '" + usr_N + "';";
        Log.i("DataManager", sql);
        db.execSQL(sql);
        helper.close();
    }

    //삭제
    public void deleteUsrFriends(String usr_PN)
    {
        Log.i(TAG, "in delete Usr_Friends");
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "delete from usrFriends where usr_PhoneNumber = '" + usr_PN + "';";
        db.execSQL(sql);
        helper.close();
    }
    //삭제
    public void deleteAll()
    {
        Log.i(TAG, "in delete Usr_Friends");
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "delete from usrFriends";
        db.execSQL(sql);
        //db.delete("usrFriends",null,null);
        helper.close();
    }

    //화면에 뿌릴 name값만 조회
    public ArrayList<UsrFriendsVO> getUsrNameInfo()
    {
        Log.i(TAG, "in getUsrFriendsInfo");
        ArrayList<UsrFriendsVO> usr_NameList = new ArrayList<UsrFriendsVO>(); //친구의 name 값만 담을 수 있는 List 생성.
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from usrFriends";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Log.i(TAG, "in while");
            int uId = cursor.getInt(0);
            String uPhoneNumber = cursor.getString(1);
            String uName = cursor.getString(2);

            Log.i(TAG, "output ===>" + uId + " " + uName +" "+ uPhoneNumber);
            usrFriendsVO = new UsrFriendsVO(uName);
            usr_NameList.add(usrFriendsVO);

        }
        helper.close();
        cursor.close();

        return usr_NameList;

    }

}
