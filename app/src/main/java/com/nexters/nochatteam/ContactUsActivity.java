package com.nexters.nochatteam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactUsActivity extends Activity {
    private static final String TAG = "SettingActivity";
    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    private ContactUsAdapter contactUsAdapter;
    private ListView contactUsListView;
    private TextView contactUsTextView;
    private TextView nexters;
    private Button contactUsBackBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFont(); //폰트적용
        setContentView(R.layout.activity_contactus);

        ArrayList<String> contactUsList = new ArrayList<String>();
        String[] names={"서버개발은 김재영","안드로이드는 서계원","아이오에스는 이재경","디자인은 유지민", "기획은 변영표","연락은 넥스터즈"};
        for(int i=0; i<5; i++){
            contactUsList.add(names[i]);
        }
        contactUsListView = (ListView) findViewById(R.id.contactUsListView);
        contactUsTextView = (TextView) findViewById(R.id.contactUsTextView);
        nexters = (TextView) findViewById(R.id.nexters);
        contactUsTextView.setTypeface(typeface);
        nexters.setTypeface(typeface);
        contactUsBackBtn = (Button) findViewById(R.id.contactUsBackBtn);

        contactUsAdapter = new ContactUsAdapter(this,contactUsList);
        contactUsAdapter.notifyDataSetChanged();//화면 중간에 추가됬다면 변경사항을 즉각반영
        contactUsListView.setAdapter(contactUsAdapter);

        contactUsBackBtn.setOnClickListener(contactUsBackBtnListener); //backBtn
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
    View.OnClickListener contactUsBackBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "in contactUsBackBtnListener");
            Intent intent = new Intent(ContactUsActivity.this, SettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };
}
