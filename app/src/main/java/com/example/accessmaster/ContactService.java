package com.example.accessmaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.accessmaster.repository.lockpinRepository;
import com.example.accessmaster.utils.SharedPrefUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Executor;


public class ContactService extends Service {
    private static final String TAG = "ContactService";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SharedPrefUtils.init(getApplicationContext());
            Log.d(TAG, "onCreate: SharedPrefUtils initialized successfully.");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to initialize SharedPrefUtils.", e);
        }
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

            // Split the message into parts
            String[] parts = message.split(" ");

            // Debug log to check the parts of the message
            Log.d(TAG, "Command parts: " + Arrays.toString(parts));

            // Check if the message has enough parts
            if (parts.length >= 3) {  // Expecting 3 parts: AccessMaster, lockPin, and command
                String lockPin = parts[1].trim();  // The lock pin
                String command = parts[2].trim();  // The command (either "getLocation" or "getContact")

                // Use equalsIgnoreCase for case-insensitive comparison
                if ("getLocation".equalsIgnoreCase(command)) {
                    handleLocationCommand(sender, lockPin);
                } else if ("getContact".equalsIgnoreCase(command)) {
                    if (parts.length > 3) {  // Make sure there’s a contact name
                        String contactName = message.substring(message.indexOf(command) + command.length()).trim();
                        handleContactCommand(sender, lockPin, contactName);
                    } else {
                        sendSms(sender, "Please provide a contact name.");
                    }
                } else {
                    Log.e(TAG, "Invalid command.");
                    sendSms(sender, "Invalid command.");
                }
            } else {
                Log.e(TAG, "Invalid command format.");
                sendSms(sender, "Invalid command format.");
            }
        } catch (Exception e) {
            Log.e(TAG, "processCommand: Error processing command.", e);
            sendSms(sender, "Error processing request.");
        }
    }


    private void handleLocationCommand(String sender, String lockPin) {
        // Fetch the logged-in user's email
        String userEmail = SharedPrefUtils.getLoggedInUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "handleLocationCommand: User email not found.");
            sendSms(sender, "User email not found. Please sign up first.");
            return;
        }

        // Fetch PIN from Room Database
        lockpinRepository lockPinRepository = new lockpinRepository(getApplicationContext());
        lockPinRepository.getPinByEmail(userEmail, storedPin -> {
            if (storedPin == null) {
                Log.e("DB_FETCH", "PIN not found in DB");
                sendSms(sender, "PIN not found.");
            } else if (storedPin.equals(lockPin)) {
                Log.d("DB_FETCH", "PIN is valid.");
                fetchLocation(sender);
            } else {
                Log.e("DB_FETCH", "Invalid PIN.");
                sendSms(sender, "Invalid lock pin.");
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void fetchLocation(String sender) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted.");
            sendSms(sender, "Location permission not granted.");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Executor executor = ContextCompat.getMainExecutor(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(executor, location -> {
                    if (location != null) {
                        String locationString = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
                        sendSms(sender, "Location found: " + locationString);
                    } else {
                        Log.e(TAG, "Location not available.");
                        sendSms(sender, "Failed to retrieve location.");
                    }
                })
                .addOnFailureListener(executor, e -> {
                    Log.e(TAG, "Failed to retrieve location.");
                    sendSms(sender, "Failed to retrieve location.");
                });

    }

    private void handleContactCommand(String sender, String lockPin, String contactName) {
        // Fetch the logged-in user's email
        String userEmail = SharedPrefUtils.getLoggedInUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "handleContactCommand: User email not found.");
            sendSms(sender, "User email not found. Please sign up first.");
            return;
        }

        // Fetch PIN from Room Database
        lockpinRepository lockPinRepository = new lockpinRepository(getApplicationContext());
        lockPinRepository.getPinByEmail(userEmail, storedPin -> {
            if (storedPin == null) {
                Log.e("DB_FETCH", "PIN not found in DB");
                sendSms(sender, "PIN not found.");
            } else if (storedPin.equals(lockPin)) {
                Log.d("DB_FETCH", "PIN is valid.");
                String contactNumber = getContactNumbers(getApplicationContext(), contactName);
                if (contactNumber != null) {
                    Log.d(TAG, "handleContactCommand: Contact found - " + contactName + ": " + contactNumber);
                    sendSms(sender, "Contact found: " + contactName + " - " + contactNumber);
                } else {
                    Log.e(TAG, "handleContactCommand: Contact not found: " + contactName);
                    sendSms(sender, "Contact not found: " + contactName);
                }
            } else {
                Log.e("DB_FETCH", "Invalid PIN.");
                sendSms(sender, "Invalid lock pin.");
            }
        });
    }

    private String getContactNumbers(Context context, String contactName) {
        try {
            Log.d(TAG, "getContactNumbers: Searching for contact - " + contactName);
            ContentResolver resolver = context.getContentResolver();
            HashSet<String> uniqueNumbers = new HashSet<>();
            StringBuilder result = new StringBuilder();

            Cursor cursor = resolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    "LOWER(" + ContactsContract.Contacts.DISPLAY_NAME + ") LIKE ?",
                    new String[]{"%" + contactName.toLowerCase() + "%"},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String fullName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                    Cursor phoneCursor = resolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null
                    );

                    if (phoneCursor != null && phoneCursor.moveToFirst()) {
                        StringBuilder numbers = new StringBuilder();
                        do {
                            String number = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    .replaceAll("\\s+", ""); // Remove spaces for duplicate prevention

                            if (uniqueNumbers.add(number)) { // Ensure unique numbers
                                if (numbers.length() > 0) {
                                    numbers.append(", ");
                                }
                                numbers.append(number);
                            }
                        } while (phoneCursor.moveToNext());

                        if (numbers.length() > 0) {
                            if (result.length() > 0) {
                                result.append("\n"); // New line between contacts
                            }
                            result.append(fullName).append(" - ").append(numbers);
                        }
                        phoneCursor.close();
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            if (result.length() > 0) {
                return ":\n" + result.toString(); // Only one "Contact found"
            }

            return "Contact not found: " + contactName; // Return proper message if no contacts match
        } catch (Exception e) {
            Log.e(TAG, "getContactNumbers: Error retrieving contact numbers.", e);
            return "Error retrieving contact information.";
        }
    }
    private void sendSms(String phoneNumber, String message) {
        try {
            Log.d(TAG, "sendSms: Sending SMS to " + phoneNumber + " with message: " + message);

            SmsManager smsManager = SmsManager.getDefault();

            // Force Unicode if the message contains emojis or special characters
            boolean containsSpecialCharacters = !message.matches("\\A\\p{Print}*\\z");

            if (containsSpecialCharacters) {
                Log.d(TAG, "sendSms: Detected emojis or special characters, sending in Unicode.");
                ArrayList<String> messageParts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null);
            } else {
                Log.d(TAG, "sendSms: No special characters detected, sending normally.");
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }

            Log.d(TAG, "sendSms: SMS successfully sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, "sendSms: Failed to send SMS", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
