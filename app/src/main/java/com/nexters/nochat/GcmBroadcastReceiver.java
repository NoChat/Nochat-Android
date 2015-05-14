package com.nexters.nochat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

//푸시가 왔을때
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static String TAG = "OnReceive";
    private String pushType; //푸시를 보낸건지(0), 푸시에 대한 응답을 한건지(1)
    private String msg;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "In onReceive");

        if(intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            PushWakeLock.acquireCpuWakeLock(context);   // 화면 깨우기
            pushType = intent.getStringExtra("pushType");
            String userId = intent.getStringExtra("userId");
            msg = intent.getStringExtra("msg");
            generateNotification (context, pushType, msg);

        }
    }

    private void generateNotification(Context context ,String pushType, String msg) {
        Log.i(TAG, "in generateNotification");
        NotificationManager notificationManager = null;
        Notification notification = null;
        Intent notificationIntent = null;
        PendingIntent intent = null;

        int icon = R.drawable.appicon;
        long when = System.currentTimeMillis();
        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification(icon, msg, when);

        //타이틀 대신에 보낸사람 이름 넣어야함
        String title = context.getString(R.string.app_name);

        //푸시타입을 확인한다.
        if(pushType.equals("0")) {
            Log.i(TAG, "pushType =0");
            notificationIntent = new Intent(context, showMsg.class);
        }else if(pushType.equals("1")){
            Log.i(TAG, "pushType =1");
            notificationIntent = new Intent(context, showMsgResponse.class);
        }
        notificationIntent.putExtra("msg", msg);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setLatestEventInfo(context, title, msg, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.defaults |= Notification.DEFAULT_SOUND;

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }



}
