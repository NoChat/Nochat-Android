package com.nexters.nochat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

//푸시가 왔을때
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static String TAG = "OnReceive";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"In onReceive");
        Toast.makeText(context, "GCM테스트입니다ㅁ럼댜ㅐ럼;댜ㅐㅓㄹ;ㅐㅑㅓㅁㄹㄷ;ㅐㅓㅁㄹ대ㅑ;.", Toast.LENGTH_SHORT).show();
        /*AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("푸시테스트입니다");
        alert.show();*/
    }

}
