package com.nexters.nochat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String CTAG = "AsyncHttpClient";
    private static final String URL = "http://todaytrend.cafe24.com:9000/users/signin";

    private Button backMain2; //메인화면으로 이동
    private TextView membershipTitle2; //타이틀 문구
    private EditText memberShipId2; //ID입력
    private EditText memberShipPassword2; //password입력
    private TextView certifyCheck2; //중복된 아이디가 있는지 검사
    private Button memberShipBtn2; //노챗시작하기

    private String loginId;
    private String password;
    //private String deviceToken;
    private String locale = "ko_KR"; //일단 한국어만
    private String os = "Android";

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    private RequestParams paramData; //회원가입 요청 관련 param data
    private String regId; // preferences에서 뽑아와서 담을 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFont(); //폰트적용
        setContentView(R.layout.activity_login);

        backMain2 = (Button)findViewById(R.id.backMain2);
        membershipTitle2 = (TextView)findViewById(R.id.membershipTitle2);
        memberShipId2 = (EditText)findViewById(R.id.memberShipId2);
        memberShipPassword2 = (EditText)findViewById(R.id.memberShipPassword2);
        certifyCheck2 = (TextView)findViewById(R.id.certifyCheck2);
        memberShipBtn2 = (Button)findViewById(R.id.memberShipBtn2);

        memberShipBtn2.setTypeface(typeface); //버튼안 text에서도 font 적용
        membershipTitle2.setTypeface(typeface);
        memberShipId2.setTypeface(typeface);
        memberShipPassword2.setTypeface(typeface);
        certifyCheck2.setTypeface(typeface);

        //휴대폰 넓이 보다 텍스트가 길 경우 마키 처리
        certifyCheck2.setSingleLine(true);
        certifyCheck2.setEllipsize(android.text.TextUtils.TruncateAt.END);

        //폰에 저장된 regId 가져오기
        SharedPreferences preferencesRegId = PreferenceManager.getDefaultSharedPreferences(this);
        regId = preferencesRegId.getString("regId"," ");

        backMain2.setOnClickListener(backMainListener);
        memberShipBtn2.setOnClickListener(memberShipBtnListener);
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

    /*메인화면으로 이동*/
    View.OnClickListener backMainListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
    /* 노챗 가입하기*/
    View.OnClickListener memberShipBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"노챗 가입하기 버튼눌림");
            loginId = memberShipId2.getText().toString();
            password = memberShipPassword2.getText().toString();
            jsonDateSetting(loginId,password);
        }
    };

    /* 서버로 보낼 json data 세팅*/
    private void jsonDateSetting(String loginId, String password){
        try{
            Log.i(CTAG,"서버로 보낼 param data 세팅중");
            paramData = new RequestParams();
            paramData.put("loginId",loginId); //한글x,공백x,중복 아이디 체크 ->alert띄우기
            paramData.put("password",password);

        }catch (Exception e){
            e.printStackTrace();
        }
        AsyncHttpClient(paramData);
    }

    /* AsyncHttpClient 사용해 서버와 통신*/
    private void AsyncHttpClient(RequestParams paramData) {
        String param = paramData.toString();
        Log.i(CTAG, "#서버와 통신 준비중#" + "PARAMDATA:" + param);
        AsyncHttpClient mClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("apiToken","")};
        mClient.post(this, URL, headers, paramData,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //reqBuilder.setHeader(String name, String value);
                Log.i(CTAG, "json response Success");
                System.out.println("로그인관련 response : " + response.toString());
                String certifyMSG = null;
                try {
                    certifyMSG = response.getString("code");
                    if (certifyMSG.equals("12000")) {   //로그인 실패시
                        certifyCheck2.setVisibility(View.VISIBLE);
                        onStop();
                    } else { //로그인 성공했을시
                        startLoginActivity();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(CTAG, "json response Failure");


            }
        });
    }

    /*FriendsListActivity으로 이동*/
    private void startLoginActivity(){
        Log.i(TAG,"CertifyActivity로 이동");
        certifyCheck2.setVisibility(View.GONE);
        Intent intent = new Intent(LoginActivity.this, FriendsListActivity.class);
        startActivity(intent);
        finish();
    }
}

