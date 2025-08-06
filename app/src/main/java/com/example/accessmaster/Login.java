package com.example.accessmaster;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.database.User;
import com.example.accessmaster.database.LockPin;
import com.example.accessmaster.repository.UserRepository;
import com.example.accessmaster.repository.lockpinRepository;
import com.example.accessmaster.utils.SharedPrefUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button loginButton;
    private TextView linkRegister, linkForgotPassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private UserRepository userRepository;
    private lockpinRepository lockPinRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("AccessMasterPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            navigateToMainActivity(); // Skip login if already logged in
            return;
        }

        setContentView(R.layout.login);
        SharedPrefUtils.init(getApplicationContext());

        // Initialize Views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.button11);
        linkRegister = findViewById(R.id.link_register);
        linkForgotPassword = findViewById(R.id.link_forgotPassword);
        ImageView backarrow = findViewById(R.id.backarrow);

        // Initialize Firebase, Database, and Repositories
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userRepository = new UserRepository(this);
        lockPinRepository = new lockpinRepository(this);

        // Set up button listeners
        loginButton.setOnClickListener(v -> validateAndLogin());
        linkRegister.setOnClickListener(v -> navigateToSignUp());
        linkForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
        backarrow.setOnClickListener(v -> finish());
    }

    private void validateAndLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email address");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("Password is required");
            return;
        }

        Log.d(TAG, "Attempting login for: " + email);
        loginButton.setEnabled(false); // Prevent multiple clicks

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginButton.setEnabled(true); // Re-enable button
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            if (!currentUser.isEmailVerified()) {
                                showToast("Please verify your email before logging in.");
                                Log.d(TAG, "Email not verified for: " + email);
                            } else {
                                Log.d(TAG, "Login successful, fetching user data...");
                                fetchUserDataFromFirebase(currentUser.getUid());

                                // Save login status
                                SharedPreferences sharedPreferences = getSharedPreferences("AccessMasterPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();
                            }
                        }
                    } else {
                        Log.e(TAG, "Login failed for: " + email, task.getException());
                        showToast("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    // Fetch user details and lock PIN from Firebase and store in Room DB.
    private void fetchUserDataFromFirebase(String userId) {
        databaseReference.child(userId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String username = snapshot.child("username").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String mobile = snapshot.child("mobile").getValue(String.class);
                String storedPassword = snapshot.child("password").getValue(String.class);
                String lockPinValue = snapshot.child("lockPin").getValue(String.class); // Fetch Lock PIN

                if (storedPassword == null) storedPassword = "N/A";

                if (username != null && email != null && mobile != null) {
                    User user = new User(username, email, mobile, storedPassword);

                    // Check if user exists in Room DB.
                    userRepository.getUserByEmail(email, existingUser -> {
                        if (existingUser != null) {
                            SharedPrefUtils.setLoggedInUserEmail(email);
                            SharedPrefUtils.setLoggedInUserMobile(mobile);

                            // Store Lock PIN in Room if not already stored
                            if (lockPinValue != null && !lockPinValue.isEmpty()) {
                                storeLockPinInRoom(email, lockPinValue);
                            }

                            showToast("Login successful!");
                            navigateToMainActivity();
                        } else {
                            // Insert user into Room DB.
                            userRepository.insertUser(user, new UserRepository.DatabaseCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    SharedPrefUtils.setLoggedInUserEmail(email);
                                    SharedPrefUtils.setLoggedInUserMobile(mobile);

                                    // Store Lock PIN in Room if not already stored
                                    if (lockPinValue != null && !lockPinValue.isEmpty()) {
                                        storeLockPinInRoom(email, lockPinValue);
                                    }

                                    showToast("Login successful!");
                                    navigateToMainActivity();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    showToast("Error saving user: " + errorMessage);
                                }
                            });
                        }
                    });
                } else {
                    showToast("User data is incomplete in Firebase");
                }
            } else {
                showToast("User not found in Firebase database");
            }
        }).addOnFailureListener(e -> showToast("Failed to fetch data: " + e.getMessage()));
    }

    // Store lock PIN in Room Database
    private void storeLockPinInRoom(String email, String lockPinValue) {
        lockPinRepository.getLockPinByEmail(email, existingLockPin -> {
            if (existingLockPin == null) {
                LockPin newLockPin = new LockPin(email, lockPinValue);
                lockPinRepository.insertLockPin(newLockPin,
                        () -> Log.d(TAG, "Lock Pin stored in Room Database"),
                        () -> Log.e(TAG, "Failed to store Lock Pin in Room Database"));
            } else {
                Log.d(TAG, "Lock Pin already exists in Room Database");
            }
        });
    }

    private void navigateToSignUp() {
        startActivity(new Intent(Login.this, SignUp.class));
    }

    private void navigateToForgotPassword() {
        startActivity(new Intent(Login.this, ForgotPasswordActivity.class));
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity
    }

    private void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show());
    }
}
