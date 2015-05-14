package com.nexters.nochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.Timer;
import java.util.TimerTask;

public class showMsg extends Activity {

    private static final String TAG = "showMsg";
    private static final String CTAG = "AsyncHttpClient";
    private static final String LIKEURL = "http://todaytrend.cafe24.com:9000/chats/14/ok";
    private static final String HITEURL = "http://todaytrend.cafe24.com:9000/chats/14/no";

    private Button btn_cancel; //닫기버튼
    private TextView msgFromText; //FROM 문구
    private TextView msgUserName; //보내온 유저이름
    private TextView msgChatType; //타입(밥,술,커피)등등
    private Button btn_msgLike; //좋아요 버튼
    private Button btn_msgHate; //싫어요 버튼

    private RequestParams paramData; //인증번호 요청 관련 param data
    private String apiToken; //토큰값
    private String userName, msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.showmsg);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e(TAG,"in showMsg");

        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        msgUserName = (TextView)findViewById(R.id.msgUserName);
        msgChatType = (TextView)findViewById(R.id.msgChatType);
        btn_msgLike = (Button)findViewById(R.id.btn_msgLike);
        btn_msgHate = (Button)findViewById(R.id.btn_msgHate);

        //GcmBroadcastReceiver에서 받은 데이터들
        Bundle bun = getIntent().getExtras();
        userName = bun.getString("title");
        msg = bun.getString("msg");

        SharedPreferences preferencesApiToken = PreferenceManager.getDefaultSharedPreferences(this); //폰에 저장된 토큰값 가져오기
        apiToken = preferencesApiToken.getString("apiToken"," ");

        btn_cancel.setOnClickListener(btn_cancelListener);
        btn_msgLike.setOnClickListener(btn_msgLikeListener);
        btn_msgHate.setOnClickListener(btn_msgHateListener);

    }

    /*닫기 버튼*/
    View.OnClickListener btn_cancelListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_cancelListener");
            PushWakeLock.releaseCpuLock();
            showMsg.this.finish();
        }
    };

    View.OnClickListener btn_msgLikeListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_msgLikeListener");
            AsyncHttpClient(LIKEURL);

        }
    };

    View.OnClickListener btn_msgHateListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_msgHateListener");
            AsyncHttpClient(HITEURL);
        }
    };

    /* AsyncHttpClient 사용해 서버와 통신*/
    private void AsyncHttpClient(String url) {
        try{
            Log.i(CTAG,"서버로 보낼 param data 세팅중");
            paramData = new RequestParams();
            paramData.put("apiToken",apiToken);

        }catch (Exception e){
            e.printStackTrace();
        }
        Log.i(CTAG, "#서버와 통신 준비중#" + url);
        AsyncHttpClient mClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("apiToken",apiToken)};
        mClient.post(this, url, headers,paramData,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //reqBuilder.setHeader(String name, String value);
                Log.i(CTAG, "json response Success");
                System.out.println(" response : " + response.toString());
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(CTAG, "json response Failure");
            }
        });
    }

}
