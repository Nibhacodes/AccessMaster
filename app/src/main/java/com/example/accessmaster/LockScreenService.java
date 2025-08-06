package com.example.accessmaster;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.telephony.SmsManager;

public class LockScreenService extends Service {
    private static final String TAG = "LockScreenService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String sender = intent.getStringExtra("sender");
            lockDeviceScreen(sender);
        }
        return START_NOT_STICKY;
    }

    private void lockDeviceScreen(String sender) {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);

        if (dpm != null && dpm.isAdminActive(adminComponent)) {
            dpm.lockNow();
            Log.d(TAG, "🔒 Device Locked Successfully");
            sendSms(sender, "Screen locked successfully.");
        } else {
            Log.e(TAG, "❌ Device Admin Permission Not Granted!");
            sendSms(sender, "Device admin permission not granted. Please enable it.");
        }
    }

    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
