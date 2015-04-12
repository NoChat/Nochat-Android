package com.nexters.nochat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

//푸시가 왔을때
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static String TAG = "OnReceive";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"SAAFFEFEFEFE");
        Toast.makeText(context, "GCM테스트입니다.", Toast.LENGTH_SHORT).show();
    }
}
