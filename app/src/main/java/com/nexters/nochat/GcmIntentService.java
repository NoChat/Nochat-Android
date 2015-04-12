package com.nexters.nochat;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

//푸시 클릭하고 들어갔을때
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService()
    {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
