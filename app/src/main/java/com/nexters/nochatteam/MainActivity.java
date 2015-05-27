package com.nexters.nochatteam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.w3c.dom.Text;

import java.io.IOException;


public class MainActivity extends Activity { //android:theme="@android:style/Theme.NoTitleBar" 때문에 ActionBarActivity -> Activity

    private static final String TAG = "MainActivity";
    private static final String ASYNCTAG = "AsyncTask";
    private Button joinBtn; //회원가입
    private TextView nochatMaintext1;
    private TextView nochatMaintext2;
    private TextView loginContent;
    private TextView logintextBtn; //로그인

    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";
    private Typeface typeface = null; //font

    GoogleCloudMessaging gcm;
    private static final String SENDER_ID="467703711556";
    private String regId; //뽑아올 regid
    private Intent Mintent = null;
    private String loginIdName = null;
    private boolean loginBoolean =true;
    private boolean start = true;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            moveTaskToBack(true); // 본Activity finish후 다른 Activity가 뜨는 걸 방지.
            finish();
            android.os.Process.killProcess(android.os.Process.myPid()); // -> 해당 어플의 프로세스를 강제 Kill시킨다.

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref;
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if(!pref.getBoolean("isFirst", false)){
            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean("workCheckBox", true);
            edit.putBoolean("isFirst", true);
            edit.commit();
            start = false;
        }

        Mintent = getIntent();
        loginIdName = Mintent.getStringExtra("settingId"); // 로그아웃시 가져오는 Intent값

        //시작점 구별하기 위해서
        if(loginIdName == null){    //로그아웃이 아닐때
            loginBoolean = false;
        }

        Log.e("시작점구별","loginId값,"+loginIdName);
        if(start == true && loginBoolean == false) { //apiToken 값이 있으면 앱실행시 FriendsListActivity 화면으로 이동
            Log.e("시작점구별","FriendsListActivity 실행");
            Intent Fintent = new Intent(MainActivity.this, FriendsListActivity.class);
            Fintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(Fintent);
        }
        Log.e("시작점구별","MainActivity 실행");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        registerBackground();   //get regId
        setFont();  //폰트적용
        setContentView(R.layout.activity_main);

        joinBtn = (Button) findViewById(R.id.joinBtn);
        nochatMaintext1 = (TextView) findViewById(R.id.nochatMaintext1);
        nochatMaintext2 = (TextView) findViewById(R.id.nochatMaintext2);
        loginContent = (TextView) findViewById(R.id.loginContent);
        logintextBtn = (TextView) findViewById(R.id.logintextBtn);

        joinBtn.setTypeface(typeface); //버튼안 text에서도 font 적용
        nochatMaintext1.setTypeface(typeface);
        nochatMaintext2.setTypeface(typeface);
        loginContent.setTypeface(typeface);
        logintextBtn.setTypeface(typeface);

        logintextBtn.setPaintFlags(logintextBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //밑줄 긋기

        joinBtn.setOnClickListener(memberShipListener); //회원가입
        logintextBtn.setOnClickListener(logintextListener); //로그인

    }
    private void setFont() {
        if(typeface==null) {
            typeface = Typeface.createFromAsset(getAssets(), TYPEFACE_NAME);
        }else{
            Log.e(TAG,"폰트가 없습니다.");
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

    /*  회원가입 Listener   */
    View.OnClickListener memberShipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "member joining method");
            Intent intent = new Intent(MainActivity.this, MemberShipActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            /*Intent intent = new Intent(MainActivity.this, FriendsListActivity.class);
            startActivity(intent);*/
        }
    };

    /*  로그인 Listener    */
    View.OnClickListener logintextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click Login");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };

    /*  GCM 처리*/
    private void registerBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Log.i(ASYNCTAG,"in doinBackground");
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + regId;
                    Log.e(TAG, msg);
                    sharedPreferencesSetting(regId);

                } catch (IOException e) {
                    msg = "Error :" + e.getMessage();
                }
                return msg;
            }


        }.execute(null, null, null);
    }

    /* 폰에 저장해놓고 지속적으로 써야할 데이터 처리 */
    private void sharedPreferencesSetting(String paramRegId){//preferencesRegId
        SharedPreferences preferencesRegId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //regid값 폰에 저장
        SharedPreferences.Editor editor = preferencesRegId.edit();
        editor.putString("regId",paramRegId);
        editor.commit();
    }

}
