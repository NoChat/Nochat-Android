package com.nexters.nochat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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

public class CertifyActivity extends ActionBarActivity {

    private static final String TAG = "CertifyActivity";
    private static final String CTAG = "AsyncHttpClient";
    private static final String URL = "http://todaytrend.cafe24.com:9000/users/phone/token";

    private Button backMembership; //뒤로가기
    private EditText inputPhoneNumber; //폰번호 입력
    private Button certifyBtn; // 인증번호 전송
    String phoneNumberValue; //폰번호 값
    String apiToken; //폰에 저장된 토큰값
    private RequestParams paramData; //인증번호 요청 관련 param data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify);

        backMembership = (Button) findViewById(R.id.backMembership);
        inputPhoneNumber = (EditText) findViewById(R.id.inputPhoneNumber);
        certifyBtn = (Button) findViewById(R.id.certifyBtn);
        SharedPreferences preferencesApiToken = PreferenceManager.getDefaultSharedPreferences(this); //폰에 저장된 토큰값 가져오기
        apiToken = preferencesApiToken.getString("apiToken"," ");

        backMembership.setOnClickListener(backMembershipListener);
        certifyBtn.setOnClickListener(certifyBtnListener);

    }

    View.OnClickListener backMembershipListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"뒤로가기 버튼");
            Intent intent = new Intent(CertifyActivity.this,MemberShipActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener certifyBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            phoneNumberValue = inputPhoneNumber.getText().toString();
            jsonDateSetting(phoneNumberValue);

        }
    };

    /* 서버로 보낼 json data 세팅*/
    private void jsonDateSetting(String phoneNumberValue){
        try{
            Log.i(CTAG,"서버로 보낼 json data 세팅중");
            paramData = new RequestParams();
            paramData.put("phoneNumber",phoneNumberValue); //한글x,공백x,중복 아이디 체크 ->alert띄우기
            paramData.put("apiToken",apiToken);

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
        Header[] headers = {new BasicHeader("apiToken",apiToken)};
        mClient.post(this, URL, headers, paramData,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //reqBuilder.setHeader(String name, String value);
                Log.i(CTAG, "json response Success");
                System.out.println("인증요청관련 response : " + response.toString());

                SharedPreferences preferencesphoneNumberValue = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //자기 폰번호값 폰에 저장
                SharedPreferences.Editor editor = preferencesphoneNumberValue.edit();
                editor.putString("phoneNumberValue",phoneNumberValue);
                editor.commit();

                startReCertifyActivity();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(CTAG, "json response Failure");


            }
        });
    }

    /*ReCertifyActivity으로 이동*/
    private void startReCertifyActivity(){
        Log.i(TAG,"ReCertifyActivity로 이동");
        Intent intent = new Intent(CertifyActivity.this, ReCertifyActivity.class);
        startActivity(intent);
    }


}
