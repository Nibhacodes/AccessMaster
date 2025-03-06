package com.example.accessmaster;

import static com.example.accessmaster.utils.SharedPrefUtils.getLoggedInUserEmail;
import static com.example.accessmaster.utils.SharedPrefUtils.getLoggedInUserMobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.accessmaster.database.LockPin;
import com.example.accessmaster.repository.lockpinRepository;
import com.example.accessmaster.utils.SharedPrefUtils;

public class lockpin extends AppCompatActivity {

    private Button btnSetPin;
    private EditText etLockPin;
    private lockpinRepository lockPinRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockpin);
        SharedPrefUtils.init(getApplicationContext());

        // Initialize UI components
        btnSetPin = findViewById(R.id.button10);
        etLockPin = findViewById(R.id.editText);

        // Set window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize repository
        lockPinRepository = new lockpinRepository(this);

        // Set button click listener
        btnSetPin.setOnClickListener(v -> setLockPin());
    }

    private void setLockPin() {
        String lockPinValue = etLockPin.getText().toString().trim();

        if (lockPinValue.isEmpty() || lockPinValue.length() != 4) {
            Toast.makeText(this, "Please enter a valid 4-digit pin", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = getLoggedInUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found. Please sign up first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userMobile = getLoggedInUserMobile();
        if (userMobile == null || userMobile.isEmpty()) {
            Toast.makeText(this, "User mobile number not found. Please sign up again.", Toast.LENGTH_SHORT).show();
            return;
        }

        LockPin newLockPin = new LockPin(userEmail, lockPinValue);

        lockPinRepository.insertLockPin(newLockPin,
                () -> runOnUiThread(() -> {
                    Toast.makeText(this, "Lock Pin set successfully!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                }),
                () -> runOnUiThread(() -> Toast.makeText(this, "Failed to set Lock Pin. Try again!", Toast.LENGTH_SHORT).show())
        );
    }



    private void navigateToMainActivity() {
        runOnUiThread(() -> {
            try {
                Log.d("LockPin", "Navigating to MainActivity");
                Intent intent = new Intent(lockpin.this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("LockPin", "Navigation failed: " + e.getMessage());
                Toast.makeText(this, "Navigation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}