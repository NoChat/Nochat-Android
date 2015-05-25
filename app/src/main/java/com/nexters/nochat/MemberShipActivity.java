package com.nexters.nochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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
import org.w3c.dom.Text;

public class MemberShipActivity extends Activity {

    private static final String TAG = "MemberShipActivity";
    private static final String CTAG = "AsyncHttpClient";
    private static final String URL = "http://todaytrend.cafe24.com:9000/users/signup";

    private Button backMain; //메인화면으로 이동
    private TextView membershipTitle; //타이틀 문구
    private EditText memberShipId; //ID입력
    private EditText memberShipPassword; //password입력
    private TextView certifyCheck; //중복된 아이디가 있는지 검사
    private Button memberShipBtn; //노챗시작하기

    private String loginId;
    private String password;
    //private String deviceToken;
    private String locale = "ko_KR"; //일단 한국어만
    private String os = "Android";

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    private RequestParams paramData; //회원가입 요청 관련 param data
    private String apiToken; //토큰값
    private String regId; // preferences에서 뽑아와서 담을 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFont(); //폰트적용
        setContentView(R.layout.activity_membership);

        backMain = (Button)findViewById(R.id.backMain);
        membershipTitle = (TextView)findViewById(R.id.membershipTitle);
        memberShipId = (EditText)findViewById(R.id.memberShipId);
        memberShipPassword = (EditText)findViewById(R.id.memberShipPassword);
        certifyCheck = (TextView)findViewById(R.id.certifyCheck);
        memberShipBtn = (Button)findViewById(R.id.memberShipBtn);

        memberShipBtn.setTypeface(typeface); //버튼안 text에서도 font 적용
        membershipTitle.setTypeface(typeface);
        memberShipId.setTypeface(typeface);
        memberShipPassword.setTypeface(typeface);
        certifyCheck.setTypeface(typeface);

        //휴대폰 넓이 보다 텍스트가 길 경우 마키 처리
        certifyCheck.setSingleLine(true);
        certifyCheck.setEllipsize(android.text.TextUtils.TruncateAt.END);

        //폰에 저장된 regId 가져오기
        SharedPreferences preferencesRegId = PreferenceManager.getDefaultSharedPreferences(this);
        regId = preferencesRegId.getString("regId"," ");

        backMain.setOnClickListener(backMainListener);
        memberShipBtn.setOnClickListener(memberShipBtnListener);
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

    /*메인화면으로 이동*/
    View.OnClickListener backMainListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MemberShipActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    /*private String GetDevicesUUID(Context mContext) {
        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tManager.getDeviceId();
        Log.i(TAG,"@@@deviceToken값은 :"+deviceId);
        return deviceId;
    }*/

    /* 서버로 보낼 json data 세팅*/
    private void jsonDateSetting(String loginId, String password){
        //deviceToken = GetDevicesUUID(this.getBaseContext()); //UUID값

        try{
            Log.i(CTAG,"서버로 보낼 param data 세팅중");
            paramData = new RequestParams();
            paramData.put("loginId",loginId); //한글x,공백x,중복 아이디 체크 ->alert띄우기
            paramData.put("password",password);
            paramData.put("deviceToken",regId);
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
                String certifyMSG = null;
                try {
                    certifyMSG = response.getString("code");
                    if (certifyMSG.equals("10001")) {   //회원가입 실패시
                        certifyCheck.setVisibility(View.VISIBLE);
                        onStop();
                    } else { //회원가입 성공했을시
                        JSONObject signUpData = null;
                        signUpData = response.getJSONObject("data");
                        apiToken = signUpData.getString("apiToken");
                        System.out.println("$$$" + apiToken);
                        //apiToken을 폰에 저장 ===> CertifyActivity에서 쓰임
                        sharedPreferencesSetting(apiToken);

                        startCertifyActivity();
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

    /* 폰에 저장해놓고 지속적으로 써야할 데이터 처리 */
    private void sharedPreferencesSetting(String paramValue){
        SharedPreferences preferencesApiTokenValue = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //apiToken값 폰에 저장
        SharedPreferences.Editor editor = preferencesApiTokenValue.edit();
        if(paramValue.equals(apiToken)){
            editor.putString("apiToken",apiToken);
            editor.putString("loginId",loginId);
            editor.commit();
        }else{
            System.out.println("sharedPreferences =====>에러");
        }
    }

    /*CertifyActivity으로 이동*/
    private void startCertifyActivity(){
        Log.i(TAG,"CertifyActivity로 이동");
        certifyCheck.setVisibility(View.GONE);
        Intent intent = new Intent(MemberShipActivity.this, CertifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
