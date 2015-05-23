package com.nexters.nochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

public class ShowMsg extends Activity {

    private static final String TAG = "showMsg";
    private static final String CTAG = "AsyncHttpClient";
    private String LIKEURL = null;
    private String HITEURL = null;

    private Button btn_cancel; //닫기버튼
    private TextView msgFromText; //FROM 문구
    private TextView msgUserName; //보내온 유저이름
    private TextView msgChatType; //타입(밥,술,커피)등등
    private Button btn_msgLike; //좋아요 버튼
    private Button btn_msgHate; //싫어요 버튼

    private RequestParams paramData; //인증번호 요청 관련 param data
    private String apiToken; //토큰값
    private String userName, msg,chatId;

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFont(); //폰트적용
        setContentView(R.layout.showmsg);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e(TAG,"in showMsg");

        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        msgUserName = (TextView)findViewById(R.id.msgUserName);
        msgChatType = (TextView)findViewById(R.id.msgChatType);
        btn_msgLike = (Button)findViewById(R.id.btn_msgLike);
        btn_msgHate = (Button)findViewById(R.id.btn_msgHate);

        msgUserName.setTypeface(typeface);
        msgChatType.setTypeface(typeface);
        btn_msgLike.setTypeface(typeface);
        btn_msgHate.setTypeface(typeface);


        //GcmBroadcastReceiver에서 받은 데이터들
        Bundle bun = getIntent().getExtras();
        userName = bun.getString("userName");
        msg = bun.getString("msg");
        chatId = bun.getString("chatId");
        Log.e(TAG,userName+msg+chatId);
        if(userName == null){
            String noUserName = "동기화ㄴㄴ새로고침ㄱㄱ";
            AlertDialog.Builder alert = new AlertDialog.Builder(ShowMsg.this);
            alert.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage(noUserName);
            alert.show();
        }
        msgUserName.setText(userName);
        msgChatType.setText(msg);

        LIKEURL = "http://todaytrend.cafe24.com:9000/chats/"+chatId+"/ok";
        HITEURL = "http://todaytrend.cafe24.com:9000/chats/"+chatId+"/no";

        SharedPreferences preferencesApiToken = PreferenceManager.getDefaultSharedPreferences(this); //폰에 저장된 토큰값 가져오기
        apiToken = preferencesApiToken.getString("apiToken"," ");

        btn_cancel.setOnClickListener(btn_cancelListener);
        btn_msgLike.setOnClickListener(btn_msgLikeListener);
        btn_msgHate.setOnClickListener(btn_msgHateListener);

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

    /*닫기 버튼*/
    View.OnClickListener btn_cancelListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_cancelListener");
            PushWakeLock.releaseCpuLock();
            ShowMsg.this.finish();
        }
    };

    View.OnClickListener btn_msgLikeListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_msgLikeListener");
            AsyncHttpClient(LIKEURL);
            PushWakeLock.releaseCpuLock();
            ShowMsg.this.finish();

        }
    };

    View.OnClickListener btn_msgHateListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_msgHateListener");
            AsyncHttpClient(HITEURL);
            PushWakeLock.releaseCpuLock();
            ShowMsg.this.finish();
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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(CTAG, "showMsg json response Success");
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
