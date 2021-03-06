package com.nexters.nochatteam;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class PushWakeLock {

    private static String TAG = "PushWakeLock";
    private static PowerManager.WakeLock sCpuWakeLock;
    private static KeyguardManager.KeyguardLock mKeyguardLock;
    private static boolean isScreenLock;

    static void acquireCpuWakeLock(Context context) {
        Log.e(TAG, "Acquiring cpu wake lock");
        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "Nochat");
        sCpuWakeLock.acquire();

//        KeyguardManager km = (KeyguardManager)context.getSystemService(context.KEYGUARD_SERVICE);
//        mKeyguardLock = km.newKeyguardLock("key guard");
//        if (km.inKeyguardRestrictedInputMode()) {
//        mKeyguardLock.disableKeyguard();
//          isScreenLock = true;
//        } else {
//          isScreenLock = false;
//        }

    }

    static void releaseCpuLock() {
        Log.e(TAG, "Releasing cpu wake lock");
//        if (isScreenLock) {
//          mKeyguardLock.reenableKeyguard();
//          isScreenLock = false;
//      }

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

}