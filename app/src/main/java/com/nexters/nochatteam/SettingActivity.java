package com.nexters.nochatteam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingActivity extends Activity{

    private static final String TAG = "SettingActivity";
    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    private SettingAdapter settingAdapter;
    private ListView settingListView;
    private Button settingBackBtn;
    private TextView settingTextView;

    private String loginId;
    private String settingId;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent intent = new Intent(SettingActivity.this, FriendsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //moveTaskToBack(true); // 본Activity finish후 다른 Activity가 뜨는 걸 방지.
            finish();
            //android.os.Process.killProcess(android.os.Process.myPid()); // -> 해당 어플의 프로세스를 강제 Kill시킨다.
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFont(); //폰트적용
        setContentView(R.layout.activity_setting);

        //폰에 저장된 loginId 가져오기
        SharedPreferences preferencesLoginId = PreferenceManager.getDefaultSharedPreferences(this);
        loginId = preferencesLoginId.getString("loginId"," ");

        ArrayList<String> settingList = new ArrayList<String>();
        String[] names={"내 ID는 : "+loginId,"초대하기","로그아웃","CONTACT US"};
        for(int i=0; i<4; i++){
            settingList.add(names[i]);
        }
        settingListView = (ListView) findViewById(R.id.settingListView);
        settingBackBtn = (Button) findViewById(R.id.settingBackBtn);
        settingTextView = (TextView) findViewById(R.id.settingTextView);
        settingTextView.setTypeface(typeface);

        settingAdapter = new SettingAdapter(this,settingList);
        settingAdapter.notifyDataSetChanged();//화면 중간에 추가됬다면 변경사항을 즉각반영
        settingListView.setAdapter(settingAdapter);
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0 :
                        Log.i(TAG, "In list 0");
                        break;

                    case 1 :
                        Log.i(TAG, "In list 1");
                        showAddressBooklist();
                        break;

                    case 2 :
                        Log.i(TAG, "In list 2");
                        settingId = "1";
                        Intent Mintent = new Intent(SettingActivity.this, MainActivity.class);
                        Mintent.putExtra("settingId",settingId);
                        Mintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(Mintent);
                        finish();
                        break;

                    case 3 :
                        Log.i(TAG, "In list 3");
                        Intent CIntent = new Intent(SettingActivity.this, ContactUsActivity.class);
                        CIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(CIntent);
                        break;

                }
            }
        });
        settingBackBtn.setOnClickListener(settingBackBtnListener); //backBtn
    }


    private void setFont() {
        if(typeface==null) {
            typeface = Typeface.createFromAsset(getAssets(), TYPEFACE_NAME);
        }else{
            Log.e(TAG, "폰트가 없습니다.");
        }
    }

    @Override
    public void setContentView(int viewId) {
        View view = LayoutInflater.from(this).inflate(viewId, null);
        ViewGroup group = (ViewGroup)view;
        int childCnt = group.getChildCount();
        for(int i=0; i<childCnt; i++){
            View v = group.getChildAt(i);
            if(v instanceof TextView){
                ((TextView)v).setTypeface(typeface);
            }else if(v instanceof Button){
                ((Button)v).setTypeface(typeface);
            }
        }
        super.setContentView(view);
    }

    /*  뒤로가기 Listener */
    View.OnClickListener settingBackBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "in settingBackBtnListener");
            Intent intent = new Intent(SettingActivity.this, FriendsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };

    private void showAddressBooklist() {
        Intent intent = new Intent(SettingActivity.this,
                AddressBookListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 10001);
    }
}
