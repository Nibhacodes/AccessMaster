package com.example.accessmaster;

import static com.example.accessmaster.utils.SharedPrefUtils.getLoggedInUserEmail;
import static com.example.accessmaster.utils.SharedPrefUtils.getLoggedInUserMobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.accessmaster.database.LockPin;
import com.example.accessmaster.repository.lockpinRepository;
import com.example.accessmaster.utils.SharedPrefUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class lockpin extends AppCompatActivity {

    private static final String TAG = "LockPin";
    private Button btnSetPin;
    private EditText etLockPin;
    private lockpinRepository lockPinRepository;
    private DatabaseReference userDbReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockpin);
        SharedPrefUtils.init(getApplicationContext());

        // Initialize UI components
        btnSetPin = findViewById(R.id.button10);
        etLockPin = findViewById(R.id.editText);

        // Set window insets for proper padding (if needed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth, Realtime Database reference and repository for Room DB.
        firebaseAuth = FirebaseAuth.getInstance();
        userDbReference = FirebaseDatabase.getInstance().getReference("Users");
        lockPinRepository = new lockpinRepository(this);

        // Set button click listener
        btnSetPin.setOnClickListener(v -> setLockPin());

        ImageView backarrow = findViewById(R.id.backarrow);
        backarrow.setOnClickListener(v -> finish()); // Navigate back
    }

    private void setLockPin() {
        String lockPinValue = etLockPin.getText().toString().trim();

        if (lockPinValue.isEmpty() || lockPinValue.length() != 4) {
            Toast.makeText(this, "Please enter a valid 4-digit pin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user from FirebaseAuth
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = currentUser.getUid();

        // First update the Realtime Database under the user's node with the lock pin.
        userDbReference.child(uid).child("lockPin").setValue(lockPinValue)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Lock Pin updated in Realtime DB for UID: " + uid);
                        // Next, store the lock pin in Room DB using your repository.
                        LockPin newLockPin = new LockPin(currentUser.getEmail(), lockPinValue);
                        lockPinRepository.insertLockPin(newLockPin,
                                () -> runOnUiThread(() -> {
                                    Toast.makeText(this, "Lock Pin set successfully!", Toast.LENGTH_SHORT).show();
                                    navigateToMainActivity();
                                }),
                                () -> runOnUiThread(() -> Toast.makeText(this, "Failed to set Lock Pin in Room DB. Try again!", Toast.LENGTH_SHORT).show())
                        );
                    } else {
                        Toast.makeText(this, "Failed to update Lock Pin in Realtime DB", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        runOnUiThread(() -> {
            try {
                Log.d(TAG, "Navigating to MainActivity");
                Intent intent = new Intent(lockpin.this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Navigation failed: " + e.getMessage());
                Toast.makeText(this, "Navigation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
