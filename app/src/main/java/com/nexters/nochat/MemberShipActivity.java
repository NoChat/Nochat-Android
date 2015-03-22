package com.nexters.nochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

public class MemberShipActivity extends ActionBarActivity {

    private static final String TAG = "MemberShipActivity";
    private static final String CTAG = "AsyncHttpClient";
    private static final String URL = "http://todaytrend.cafe24.com:9000/users/signup";
    private Button backMain; //메인화면으로 이동
    private EditText memberShipId; //ID입력
    private EditText memberShipPassword; //password입력
    private Button memberShipBtn; //노챗시작하기
    private String loginId;
    private String password;
    private String deviceToken;
    private String locale = "ko_KR"; //일단 한국어만
    private String os = "Android";
    RequestParams paramData; //회원가입 요청 관련 param data
    String apiToken; //토큰값

    /*  findViewById 세팅  */
/*    private void findViewByIdInit(){
        backMain = (Button)findViewById(R.id.backMain);
        memberShipId = (EditText)findViewById(R.id.memberShipId);
        memberShipPassword = (EditText)findViewById(R.id.memberShipPassword);
        memberShipBtn = (Button)findViewById(R.id.memberShipBtn);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        //findViewByIdInit();
        backMain = (Button)findViewById(R.id.backMain);
        memberShipId = (EditText)findViewById(R.id.memberShipId);
        memberShipPassword = (EditText)findViewById(R.id.memberShipPassword);
        memberShipBtn = (Button)findViewById(R.id.memberShipBtn);

        backMain.setOnClickListener(backMainListener);
        memberShipBtn.setOnClickListener(memberShipBtnListener);
    }
    /*메인화면으로 이동*/
    View.OnClickListener backMainListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MemberShipActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
    /* 노챗 가입하기*/
    View.OnClickListener memberShipBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"노챗 가입하기 버튼눌림");
            loginId = memberShipId.getText().toString();
            password = memberShipPassword.getText().toString();
            jsonDateSetting(loginId,password);
        }
    };

    /* deviceToken 설정*/
    private String GetDevicesUUID(Context mContext) {
        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tManager.getDeviceId();
        Log.i(TAG,"@@@deviceToken값은 :"+deviceId);
        return deviceId;
    }

    /* 서버로 보낼 json data 세팅*/
    private void jsonDateSetting(String loginId, String password){
        deviceToken = GetDevicesUUID(this.getBaseContext()); //UUID값
        try{
            Log.i(CTAG,"서버로 보낼 json data 세팅중");
            paramData = new RequestParams();
            paramData.put("loginId",loginId); //한글x,공백x,중복 아이디 체크 ->alert띄우기
            paramData.put("password",password);
            paramData.put("deviceToken",deviceToken);
            paramData.put("locale",locale);
            paramData.put("os", os);

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
                System.out.println("회원가입관련 response : " + response.toString());
                try {
                    JSONObject signUpData = null;
                    signUpData = response.getJSONObject("data");
                    apiToken = signUpData.getString("apiToken");
                    System.out.println("$$$" + apiToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences preferencesApiToken = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //apiToken값 폰에 저장
                SharedPreferences.Editor editor = preferencesApiToken.edit();
                editor.putString("apiToken",apiToken);
                editor.commit();

                startCertifyActivity();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(CTAG, "json response Failure");


            }
        });
    }

    /*CertifyActivity으로 이동*/
    private void startCertifyActivity(){
        Log.i(TAG,"CertifyActivity로 이동");
        Intent intent = new Intent(MemberShipActivity.this, CertifyActivity.class);
        startActivity(intent);
    }
}
