package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.accessmaster.utils.SharedPrefUtils;

public class AccessContacts extends AppCompatActivity {


    private TextView instructionFormatText;
    private TextView exampleSmsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accesscontacts);

        // Initialize SharedPreferences
        SharedPrefUtils.init(this); // Ensure this is done before fetching data

        // Toolbar setup

        // Initialize views
        instructionFormatText = findViewById(R.id.instruction_format);
        exampleSmsText = findViewById(R.id.example_sms);

        // Retrieve and display stored lock pin
        String savedLockPin = SharedPrefUtils.getLockPin();
        if (savedLockPin != null && !savedLockPin.trim().isEmpty()) {
            savedLockPin = savedLockPin.trim();
        } else {
            savedLockPin = "1234"; // Default PIN if not set
        }

    }
}
