package com.example.accessmaster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.accessmaster.repository.lockpinRepository;
import com.example.accessmaster.utils.SharedPrefUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SharedPrefUtils.init(getApplicationContext());
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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

            String[] parts = message.trim().split("\\s+");
            if (parts.length == 3) {
                String lockPin = parts[1].trim();
                String command = parts[2].trim();

                if ("getLocation".equalsIgnoreCase(command)) {
                    Log.d(TAG, "processCommand: Extracted Pin: " + lockPin);

                    if (!hasLocationPermission()) {
                        sendSms(sender, "Location permission not granted. Enable location access.");
                        return;
                    }

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
                            fetchLocation(sender);
                        } else {
                            sendSms(sender, "Invalid lock pin.");
                        }
                    });
                } else {
                    sendSms(sender, "Invalid command.");
                }
            } else {
                sendSms(sender, "Invalid command format. Use: AccessMaster PIN getLocation");
            }
        } catch (Exception e) {
            sendSms(sender, "Error processing request.");
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // ADD THIS METHOD BELOW `hasLocationPermission()`
    private boolean hasBackgroundLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void fetchLocation(String sender) {
        if (!hasLocationPermission()) {
            sendSms(sender, "Location permission not granted.");
            return;
        }

        if (!hasBackgroundLocationPermission()) {
            sendSms(sender, "Background location permission not granted. Enable it in app settings.");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        sendLocationSms(sender, location);
                    } else {
                        Log.e(TAG, "Location not available, requesting new location...");
                        requestNewLocation(sender);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve last location.", e);
                    requestNewLocation(sender);
                });
    }

    private void sendLocationSms(String sender, Location location) {
        String address = getAddressFromLocation(location.getLatitude(), location.getLongitude());
        if (address != null) {
            sendSms(sender, "Location: " + address);
        } else {
            sendSms(sender, "Coordinates: Lat " + location.getLatitude() + ", Lng " + location.getLongitude());
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        if (!isInternetAvailable()) {
            Log.e(TAG, "No internet connection, returning only coordinates.");
            return null; // Force it to return only coordinates when offline
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting address", e);
        }
        return null;
    }
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            return activeNetwork != null;
        }
        return false;
    }



    @SuppressLint("MissingPermission")
    private void requestNewLocation(String sender) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000)
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                fusedLocationClient.removeLocationUpdates(this);

                if (locationResult != null && locationResult.getLastLocation() != null) {
                    sendLocationSms(sender, locationResult.getLastLocation());
                } else {
                    sendSms(sender, "Failed to retrieve location.");
                }
            }
        }, Looper.getMainLooper());
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
