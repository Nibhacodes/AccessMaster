package com.example.accessmaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.database.LockPin;
import com.example.accessmaster.repository.lockpinRepository;
import com.example.accessmaster.utils.SharedPrefUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPin extends AppCompatActivity {

    private EditText etEmail, etNewPin;
    private Button btnResetPin;
    private ProgressBar progressBar;
    private lockpinRepository repository;
    private DatabaseReference lockPinRef; // For Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpin);
        SharedPrefUtils.init(getApplicationContext());

        // Initialize Views
        etEmail = findViewById(R.id.et_email);
        etNewPin = findViewById(R.id.et_new_pin);
        btnResetPin = findViewById(R.id.btn_reset_pin);
        progressBar = findViewById(R.id.progress_bar);
        ImageView backarrow = findViewById(R.id.backarrow);

        backarrow.setOnClickListener(v -> finish()); // Navigates to the previous activity

        // Initialize repository and Firebase Database reference for LockPin
        repository = new lockpinRepository(this);
        lockPinRef = FirebaseDatabase.getInstance().getReference("LockPin");

        // Set button click listener
        btnResetPin.setOnClickListener(view -> resetPin());
    }

    private void resetPin() {
        String email = etEmail.getText().toString().trim();
        String newPin = etNewPin.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(newPin)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPin.length() != 4) {
            Toast.makeText(this, "PIN must be exactly 4 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar and disable button
        progressBar.setVisibility(View.VISIBLE);
        btnResetPin.setEnabled(false);

        // Step 1: Send an email intent for PIN reset confirmation.


        // Step 2: Update the lock pin in Firebase Realtime Database.
        // We use email as key after encoding (replace '.' with ',').
        String encodedEmail = email.replace(".", ",");
        LockPin newLockPin = new LockPin(email, newPin);

        lockPinRef.child(encodedEmail).setValue(newLockPin)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Step 3: Also update in Room Database via lockpinRepository.
                        repository.insertLockPin(newLockPin,
                                () -> runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnResetPin.setEnabled(true);
                                    Toast.makeText(ForgotPin.this, "PIN reset successful!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }),
                                () -> runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnResetPin.setEnabled(true);
                                    Toast.makeText(ForgotPin.this, "Failed to update PIN locally. Try again.", Toast.LENGTH_SHORT).show();
                                })
                        );
                    } else {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnResetPin.setEnabled(true);
                            Toast.makeText(ForgotPin.this, "Failed to update PIN in Firebase.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    // This method sends an email using an intent. The user will receive an email informing them of the new PIN
}
