package com.example.accessmaster;

import android.app.admin.DevicePolicyManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LockScreenService extends Service {
    private static final String TAG = "LockScreenService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lockDeviceScreen();
        return START_NOT_STICKY;
    }

    private void lockDeviceScreen() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);

        if (dpm != null && dpm.isAdminActive(adminComponent)) {
            dpm.lockNow(); // 🔒 This locks the phone screen instantly!
            Log.d(TAG, "🔒 Device Locked Successfully");
        } else {
            Log.e(TAG, "❌ Device Admin Permission Not Granted!");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
