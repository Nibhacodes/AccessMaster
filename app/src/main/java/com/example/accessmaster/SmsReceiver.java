package com.example.accessmaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.telephony.SmsManager;
import com.example.accessmaster.utils.SharedPrefUtils;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String COMMAND_PREFIX = "AccessMaster";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Log.w(TAG, "❌ Ignored unexpected intent action: " + (intent != null ? intent.getAction() : "null"));
            return;
        }

        Log.d(TAG, "✅ SMS Received!");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String format = bundle.getString("format");

            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                    } else {
                        smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    }

                    String sender = smsMessage.getOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody().trim();

                    Log.d(TAG, "📩 Received SMS from " + sender + ": " + messageBody);

                    // Check if message matches any saved keyword
                    String autoReply = SharedPrefUtils.getStoredResponse(messageBody);
                    if (autoReply != null) {
                        sendAutoReply(sender, autoReply);
                    }

                    // Handle other SMS commands if needed
                    if (messageBody.startsWith(COMMAND_PREFIX)) {
                        Intent serviceIntent = null;

                        if (messageBody.contains("getLocation")) {
                            serviceIntent = new Intent(context, LocationService.class);
                        } else if (messageBody.contains("getContact")) {
                            serviceIntent = new Intent(context, ContactService.class);
                        } else if (messageBody.contains("changeSoundProfile")) {
                            serviceIntent = new Intent(context, SoundProfileService.class);
                        } else if (messageBody.contains("LockMobileScreen")) {
                            serviceIntent = new Intent(context, LockScreenService.class);
                        }

                        if (serviceIntent != null) {
                            serviceIntent.putExtra("sender", sender);
                            serviceIntent.putExtra("message", messageBody);
                            context.startService(serviceIntent);
                        }
                    }
                }
            }
        }
    }

    // Function to send auto-reply SMS
    private void sendAutoReply(String recipient, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, message, null, null);
        Log.d(TAG, "📤 Auto-reply sent to " + recipient + ": " + message);
    }
}
