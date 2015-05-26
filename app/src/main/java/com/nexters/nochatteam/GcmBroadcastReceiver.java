package com.nexters.nochatteam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

//푸시가 왔을때
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static String TAG = "OnReceive";
    private String pushType = null; //푸시를 보낸건지(0), 푸시에 대한 응답을 한건지(1)
    private String msg = null;
    private String userId = null;
    private String userName =null;
    private String chatId = null;

    private DataManager3 dm3;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "In onReceive");
        dm3 = new DataManager3(context);

        if(intent != null && intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            try {
                PushWakeLock.acquireCpuWakeLock(context);   // 화면 깨우기
                pushType = intent.getStringExtra("pushType");
                userId = intent.getStringExtra("userId");
                userName = dm3.getUserInfo(userId);
                msg = intent.getStringExtra("msg");

                Bundle bundle = intent.getExtras();
                Object value = bundle.get("chatId");
                chatId = value.toString();

                Log.e("&&&&&&id랑name값", userId + userName);
                generateNotification(context, pushType, msg, userName, chatId);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }else if(intent == null){
            Log.e(TAG,"에러!!!");
        }
        else {
            Log.e(TAG,"!!!");
        }
    }

    private void generateNotification(Context context ,String pushType, String msg, String userName, String chatId) {
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
        //String title = context.getString(R.string.app_name);

        //푸시타입을 확인한다.
        if(pushType.equals("0")) {
            Log.i(TAG, "pushType =0");
            notificationIntent = new Intent(context, ShowMsg.class);
        }else if(pushType.equals("1")){
            Log.i(TAG, "pushType =1");
            notificationIntent = new Intent(context, ShowMsgResponse.class);
        }
        notificationIntent.putExtra("msg", msg);
        notificationIntent.putExtra("userName", userName);
        notificationIntent.putExtra("chatId", chatId);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setLatestEventInfo(context, userName, msg, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.defaults |= Notification.DEFAULT_SOUND;

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }


}
