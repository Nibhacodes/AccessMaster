package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings); // Ensure this matches your XML file name

        // Find buttons by their IDs
        Button homeButton = findViewById(R.id.homeButton);
        Button forgetpasswordButton = findViewById(R.id.forgetpasswordButton);
        Button historyButton = findViewById(R.id.historyButton);
        Button forgetpinButton = findViewById(R.id.forgetpinButton);
        Button grantPermissionButton = findViewById(R.id.grantPermissionButton);
        Button feedbackButton = findViewById(R.id.feedbackButton);
        Button aboutButton = findViewById(R.id.aboutButton);

        homeButton.setOnClickListener(v ->
                Toast.makeText(Settings.this, "Home Button Clicked", Toast.LENGTH_SHORT).show()
        );

        forgetpasswordButton.setOnClickListener(v -> {
            Toast.makeText(Settings.this, "Navigating to Forget Password Page", Toast.LENGTH_SHORT).show();
            navigateToForgotPassword(); // Navigate to Forget Password Page
        });

        forgetpinButton.setOnClickListener(v -> {
            Toast.makeText(Settings.this, "Navigating to Forget Pin Page", Toast.LENGTH_SHORT).show();
            navigateToForgotPin(); // Navigate to Forget Pin Page
        });

        historyButton.setOnClickListener(v ->
                Toast.makeText(Settings.this, "History Button Clicked", Toast.LENGTH_SHORT).show()
        );

        grantPermissionButton.setOnClickListener(v ->
                Toast.makeText(Settings.this, "Grant Permission Button Clicked", Toast.LENGTH_SHORT).show()
        );

        feedbackButton.setOnClickListener(v ->
                Toast.makeText(Settings.this, "Feedback Button Clicked", Toast.LENGTH_SHORT).show()
        );

        aboutButton.setOnClickListener(v ->
                Toast.makeText(Settings.this, "About Button Clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(Settings.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void navigateToForgotPin() {
        Intent intent = new Intent(Settings.this, ForgotPin.class);
        startActivity(intent);
    }
}
