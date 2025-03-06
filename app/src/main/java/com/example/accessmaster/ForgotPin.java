package com.example.accessmaster;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.repository.lockpinRepository;

public class ForgotPin extends AppCompatActivity {

    private EditText etEmail, etNewPin;
    private Button btnResetPin;
    private ProgressBar progressBar;
    private lockpinRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpin);

        // Initialize Views
        etEmail = findViewById(R.id.et_email);
        etNewPin = findViewById(R.id.et_new_pin);
        btnResetPin = findViewById(R.id.btn_reset_pin);
        progressBar = findViewById(R.id.progress_bar);

        // Initialize Repository
        repository = new lockpinRepository(this);

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

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        btnResetPin.setEnabled(false);

        // Check if email is registered
        repository.getPinByEmail(email, pin -> runOnUiThread(() -> {
            if (pin == null) {
                progressBar.setVisibility(View.GONE);
                btnResetPin.setEnabled(true);
                Toast.makeText(this, "Email is not registered", Toast.LENGTH_SHORT).show();
            } else {
                // Update PIN if email exists
                repository.updatePin(email, newPin, () -> runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPin.setEnabled(true);
                    Toast.makeText(ForgotPin.this, "PIN reset successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }), () -> runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPin.setEnabled(true);
                    Toast.makeText(ForgotPin.this, "Failed to reset PIN. Try again.", Toast.LENGTH_SHORT).show();
                }));
            }
        }));
    }
}
