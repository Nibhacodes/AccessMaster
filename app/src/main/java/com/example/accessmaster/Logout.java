package com.example.accessmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Logout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout);

        // Initialize the Logout button
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set click listener for logout action
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        // Clear login state from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AccessMasterPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false); // Set logged-in status to false
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(Logout.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears activity stack
        startActivity(intent);
        finish(); // Close LogoutActivity
    }
}