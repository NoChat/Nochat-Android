package com.nexters.nochat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class showMsgResponse extends Activity {

    private static final String TAG = "showMsgResponse";

    private Button btn_resCancel; //닫기버튼
    private TextView msgResUserName; //보내온 유저이름
    private TextView msgResChatType; //타입(밥,술,커피)등등

    private String userName, msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.showmsgresponse);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e(TAG,"in showMsgResponse");

        btn_resCancel = (Button)findViewById(R.id.btn_resCancel);
        msgResUserName = (TextView)findViewById(R.id.msgResUserName);
        msgResChatType = (TextView)findViewById(R.id.msgResChatType);

        //GcmBroadcastReceiver에서 받은 데이터들
        Bundle bun = getIntent().getExtras();
        userName = bun.getString("title");
        msg = bun.getString("msg");

        btn_resCancel.setOnClickListener(btn_resCancelListener);
    }

    /*닫기 버튼*/
    View.OnClickListener btn_resCancelListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"btn_cancelListener");
            PushWakeLock.releaseCpuLock();
            showMsgResponse.this.finish();
        }
    };

}

