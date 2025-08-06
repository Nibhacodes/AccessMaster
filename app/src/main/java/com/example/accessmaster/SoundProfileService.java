package com.example.accessmaster;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.provider.Settings;

import com.example.accessmaster.repository.lockpinRepository;
import com.example.accessmaster.utils.SharedPrefUtils;

public class SoundProfileService extends Service {
    private static final String TAG = "SoundProfileService";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefUtils.init(getApplicationContext());
        Log.d(TAG, "onCreate: SharedPrefUtils initialized successfully.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service started.");

        if (intent != null) {
            String sender = intent.getStringExtra("sender");
            String message = intent.getStringExtra("message");

            if (sender != null && message != null) {
                Log.d(TAG, "onStartCommand: Received SMS from " + sender + " with message: " + message);
                processCommand(sender, message);
            } else {
                Log.e(TAG, "onStartCommand: Missing sender or message in intent.");
            }
        } else {
            Log.e(TAG, "onStartCommand: Intent is null.");
        }
        return START_NOT_STICKY;
    }

    private void processCommand(String sender, String message) {
        try {
            Log.d(TAG, "processCommand: Processing SMS command: " + message);

            String[] parts = message.trim().split("\\s+");
            if (parts.length == 3) {
                String lockPin = parts[1].trim();
                String command = parts[2].trim();

                if ("changeSoundProfile".equalsIgnoreCase(command)) {
                    Log.d(TAG, "processCommand: Extracted Pin: " + lockPin);

                    String userEmail = SharedPrefUtils.getLoggedInUserEmail();
                    if (userEmail == null || userEmail.isEmpty()) {
                        sendSms(sender, "User email not found. Please sign up first.");
                        return;
                    }

                    lockpinRepository lockPinRepository = new lockpinRepository(getApplicationContext());
                    lockPinRepository.getPinByEmail(userEmail, storedPin -> {
                        if (storedPin == null) {
                            sendSms(sender, "PIN not found.");
                        } else if (storedPin.equals(lockPin)) {
                            changeSoundProfile(sender);
                        } else {
                            sendSms(sender, "Invalid lock pin.");
                        }
                    });
                } else {
                    sendSms(sender, "Invalid command.");
                }
            } else {
                sendSms(sender, "Invalid command format. Use: AccessMaster PIN changeSoundProfile");
            }
        } catch (Exception e) {
            sendSms(sender, "Error processing request.");
        }
    }
    private void changeSoundProfile(String sender) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // ✅ Check for DND permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Log.e(TAG, "❌ DND Access Not Granted! Requesting Permission.");
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return; // Exit method
            }
        }

        int currentMode = audioManager.getRingerMode();

        if (currentMode == AudioManager.RINGER_MODE_SILENT || currentMode == AudioManager.RINGER_MODE_VIBRATE) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.d(TAG, "🔊 Changed Sound Profile to Normal Mode.");
            sendSms(sender, "Sound profile changed to Ringing mode.");
        } else {
            Log.d(TAG, "✅ Phone is already in Ringing Mode.");
            sendSms(sender, "Phone is already in Ringing mode.");
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
