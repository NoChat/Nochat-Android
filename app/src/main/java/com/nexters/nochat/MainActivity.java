package com.nexters.nochat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
    private static final String TYPEFACE_NAME2 = "NanumPen.otf"; //다른폰트(테스트용)
    private Typeface typeface = null; //font

    GoogleCloudMessaging gcm;
    private static final String SENDER_ID="467703711556";
    private String regId; //뽑아올 regid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //regId 등록 여부 체크
        /*GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            Log.i("Main", "Already registered - " + regId );
        }*/
        registerBackground(); //get regid

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
    View.OnClickListener memberShipListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "member joining method");
            /*Intent intent = new Intent(MainActivity.this, MemberShipActivity.class);
            startActivity(intent);*/
            Intent intent = new Intent(MainActivity.this, FriendsListActivity.class);
            startActivity(intent);
        }
    };

    /*  로그인 Listener    */
    View.OnClickListener logintextListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click Login");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
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
