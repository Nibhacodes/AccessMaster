package com.example.accessmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnExit, btnNo, btnYes, btnAccessContacts, btnAccessLocation, btnSoundProfile, btnLockMobile, btnLogout;
    private ImageView settingsIcon;
    private Button schedRepButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("AccessMasterPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // Redirect to Login if not logged in
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        setMainLayout(); // Ensure the layout is properly set
    }

    private void setMainLayout() {
        setContentView(R.layout.activity_main);

        // Initialize buttons and views
        btnExit = findViewById(R.id.exit_btn);
        settingsIcon = findViewById(R.id.imageView3);
        schedRepButton = findViewById(R.id.schedrep);
        btnLogout = findViewById(R.id.logout_btn);

        // Exit button logic
        btnExit.setOnClickListener(v -> showExitLayout());

        schedRepButton.setOnClickListener(v -> navigateToScheduledReply());

        // Navigate to Logout Page instead of logging out directly
        btnLogout.setOnClickListener(v -> navigateToLogoutPage());

        // Settings icon logic
        settingsIcon.setOnClickListener(v -> openSettings());

        btnAccessContacts = findViewById(R.id.button2);
        btnAccessContacts.setOnClickListener(v -> navigateToAccessContacts());

        btnAccessLocation = findViewById(R.id.button3);
        btnAccessLocation.setOnClickListener(v -> navigateToAccessLocation());

        btnSoundProfile = findViewById(R.id.button4);
        btnSoundProfile.setOnClickListener(v -> navigateToChangeSoundProfile());

        btnLockMobile = findViewById(R.id.button5);
        btnLockMobile.setOnClickListener(v -> navigateToLockMobile());
    }

    private void showExitLayout() {
        setContentView(R.layout.activity_exit_page);

        btnNo = findViewById(R.id.btn_no);
        btnYes = findViewById(R.id.btn_yes);

        btnNo.setOnClickListener(v -> setMainLayout());
        btnYes.setOnClickListener(v -> closeApp());
    }

    private void navigateToAccessContacts() {
        startActivity(new Intent(MainActivity.this, AccessContacts.class));
    }

    private void navigateToAccessLocation() {
        startActivity(new Intent(MainActivity.this, AccessLocation.class));
    }

    private void navigateToLogoutPage() {
        // Now navigating to the Logout page
        Intent intent = new Intent(MainActivity.this, Logout.class);
        startActivity(intent);
    }

    private void navigateToChangeSoundProfile() {
        startActivity(new Intent(MainActivity.this, SoundProfile.class));
    }

    private void navigateToLockMobile() {
        startActivity(new Intent(MainActivity.this, LockScreen.class));
    }

    private void navigateToScheduledReply() {
        startActivity(new Intent(MainActivity.this, ScheduledReply.class));
    }

    private void closeApp() {
        finishAffinity();
        System.exit(0);
    }

    private void openSettings() {
        startActivity(new Intent(MainActivity.this, Settings.class));
    }
}
