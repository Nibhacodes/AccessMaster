package com.example.accessmaster;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GrantPermission extends AppCompatActivity implements LocationListener {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grantpermission);

        TextView appDescription = findViewById(R.id.appDescription);
        Button btnSms = findViewById(R.id.btnSms);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnViewContacts = findViewById(R.id.btnViewContacts);
        Button btnPhoneState = findViewById(R.id.btnPhoneState);  // 👉 Add this button in your layout XML
        ImageView backarrow = findViewById(R.id.backarrow);

        backarrow.setOnClickListener(v -> {
            finish(); // Navigates to the previous activity
        });

        appDescription.setText("This app helps you manage SMS, location, and contacts.");

        btnSms.setOnClickListener(v -> {
            if (checkPermissions()) {
                Toast.makeText(this, "Permissions Enabled", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions();
            }
        });

        btnGetLocation.setOnClickListener(v -> {
            if (checkPermissions()) {
                Toast.makeText(this, "Permissions Enabled", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions();
            }
        });

        btnViewContacts.setOnClickListener(v -> {
            if (checkPermissions()) {
                Toast.makeText(this, "Permissions Enabled", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions();
            }
        });

        btnPhoneState.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                requestPermissions(); // optional if you want to request again
            }
        });
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;

            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];
                int result = grantResults[i];

                if (result == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permission + " Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, permission + " Denied", Toast.LENGTH_SHORT).show();
                    allGranted = false;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String address = getAddressFromLocation(location.getLatitude(), location.getLongitude());
        Toast.makeText(this, "Location: " + address, Toast.LENGTH_SHORT).show();
    }

    // Convert latitude & longitude into an address
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);  // Get the full address
            } else {
                return "Address not found!";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to get address!";
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}