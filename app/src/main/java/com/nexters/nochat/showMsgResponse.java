package com.nexters.nochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class ShowMsgResponse extends Activity {

    private static final String TAG = "showMsgResponse";

    private Button btn_resCancel; //닫기버튼
    private TextView msgResUserName; //보내온 유저이름
    private TextView msgResChatType; //타입(밥,술,커피)등등
    private TextView msgResponse; //좋대요 , 싫어요
    private Button btnCall; //전화걸기

    private String userName, msg,userPhone;

    private DataManager dm;

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFont(); //폰트적용
        setContentView(R.layout.showmsgresponse);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e(TAG,"in showMsgResponse");

        btn_resCancel = (Button)findViewById(R.id.btn_resCancel);
        msgResUserName = (TextView)findViewById(R.id.msgResUserName);
        msgResChatType = (TextView)findViewById(R.id.msgResChatType);
        msgResponse = (TextView)findViewById(R.id.msgResponse);
        btnCall = (Button)findViewById(R.id.btnCall);

        dm = new DataManager(this);

        //GcmBroadcastReceiver에서 받은 데이터들
        Bundle bun = getIntent().getExtras();
        userName = bun.getString("userName");
        msg = bun.getString("msg");
        Log.e(TAG,userName+msg);
        String like = "좋아요";
        String hate = "싫어요";
        String msgText = null;
        String msgResponseText = null;
        if(msg.contains(like)){
            msgText = msg.substring(0,2);
            msgResponseText = "좋대요ㅎ";
            Log.i(TAG,"******!"+msgText+msgResponseText);

        }else if(msg.contains(hate)){
            msgText = msg.substring(0,2);
            msgResponseText = "싫대요ㅠ";
            Log.i(TAG, "******!" + msgText+msgResponseText);
        }
        if(userName == null){
                String noUserName = "동기화ㄴㄴ새로고침ㄱㄱ";
                AlertDialog.Builder alert = new AlertDialog.Builder(ShowMsgResponse.this);
                alert.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage(noUserName);
                alert.show();
        }

        userPhone = dm.getUserPhoneInfo(userName);
        Log.e("userPhone 번호:",userPhone);

        msgResUserName.setText(userName);
        msgResChatType.setText(msgText);
        msgResponse.setText(msgResponseText);

        msgResUserName.setTypeface(typeface);
        msgResChatType.setTypeface(typeface);
        msgResponse.setTypeface(typeface);

        btn_resCancel.setOnClickListener(btn_resCancelListener);
        btnCall.setOnClickListener(btnCallListener);
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
    View.OnClickListener btn_resCancelListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_cancelListener");
            PushWakeLock.releaseCpuLock();
            ShowMsgResponse.this.finish();
        }
    };

    /*전화걸기*/
    View.OnClickListener btnCallListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btnCallListener");
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse( "tel:"+userPhone ));
            startActivity(phoneIntent);
        }
    };
}

