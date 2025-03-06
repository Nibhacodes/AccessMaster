package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private Button btnExit, btnNo, btnYes, btnAccessContacts, btnAccessLocation, btnSoundProfile, btnLockMobile;
    private ImageView settingsIcon;
    private Button schedRepButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMainLayout(); // Ensure the layout is properly set
    }

    private void setMainLayout() {
        // Set the main layout for the activity
        setContentView(R.layout.activity_main);

        // Initialize buttons and views
        btnExit = findViewById(R.id.exit_btn);
        settingsIcon = findViewById(R.id.imageView3);
        schedRepButton = findViewById(R.id.schedrep);

        // Exit button logic
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitLayout();
            }
        });

        schedRepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToScheduledReply();
            }
        });



        // Settings icon logic
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings(); // Navigate to Settings
            }
        });
        btnAccessContacts = findViewById(R.id.button2);

        btnAccessContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAccessContacts(); // Navigate to AccessContacts activity
            }
        });
        btnAccessLocation = findViewById(R.id.button3);

        btnAccessLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAccessLocation(); // Navigate to AccessContacts activity
            }
        });

        btnSoundProfile = findViewById(R.id.button4);

        btnSoundProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToChangeSoundProfile(); // Navigate to AccessContacts activity
            }
        });

        btnLockMobile = findViewById(R.id.button5);

        btnLockMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLockMobile(); // Navigate to AccessContacts activity
            }
        });
    }

    private void showExitLayout() {
        // Set the layout for the exit page
        setContentView(R.layout.activity_exit_page);

        btnNo = findViewById(R.id.btn_no);
        btnYes = findViewById(R.id.btn_yes);

        // Set onClick listeners for exit confirmation buttons
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMainLayout(); // Go back to the main layout
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeApp(); // Close the application
            }
        });
    }


    private void navigateToAccessContacts() {
        // Navigate to AccessContacts activity
        Intent intent = new Intent(MainActivity.this, AccessContacts.class);
        startActivity(intent);
    }

    private void navigateToAccessLocation() {
        // Navigate to AccessContacts activity
        Intent intent = new Intent(MainActivity.this, AccessLocation.class);
        startActivity(intent);
    }

    private void navigateToChangeSoundProfile() {
        // Navigate to AccessContacts activity
        Intent intent = new Intent(MainActivity.this, SoundProfile.class);
        startActivity(intent);
    }

    private void navigateToLockMobile() {
        // Navigate to AccessContacts activity
        Intent intent = new Intent(MainActivity.this, LockScreen.class);
        startActivity(intent);
    }

    private void navigateToScheduledReply() {
        // Navigate to AccessContacts activity
        Intent intent = new Intent(MainActivity.this, ScheduledReply.class);
        startActivity(intent);
    }


    private void closeApp() {
        // Exit the application
        finishAffinity();
        System.exit(0);
    }

    private void openSettings() {
        // Navigate to the Settings activity
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
    }
}
