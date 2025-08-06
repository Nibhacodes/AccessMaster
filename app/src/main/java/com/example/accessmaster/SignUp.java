package com.example.accessmaster;

import static com.example.accessmaster.utils.SharedPrefUtils.setLoggedInUserEmail;
import static com.example.accessmaster.utils.SharedPrefUtils.setLoggedInUserMobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.database.User;
import com.example.accessmaster.repository.UserRepository;
import com.example.accessmaster.utils.SharedPrefUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "AccessMaster";
    private EditText editTextUsername, editTextEmail, editTextMobile, editTextPassword;
    private Button sendVerificationButton, signUpButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private UserRepository userRepository;
    private String realPassword; // Store real password temporarily

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SharedPrefUtils.init(getApplicationContext());

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);
        sendVerificationButton = findViewById(R.id.sendVerificationButton);
        signUpButton = findViewById(R.id.button9);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userRepository = new UserRepository(this);

        sendVerificationButton.setOnClickListener(v -> sendEmailVerification());
        signUpButton.setOnClickListener(v -> saveUserDetails());

        ImageView backarrow = findViewById(R.id.backarrow);
        backarrow.setOnClickListener(v -> finish());
    }

    private void sendEmailVerification() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email address");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("Enter a password");
            return;
        }

        realPassword = password; // Store real password to use later

        Log.d(TAG, "Checking if email is already registered: " + email);

        firebaseAuth.createUserWithEmailAndPassword(email, password) // Store real password
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    showToast("Verification email sent! Check your inbox.");
                                    Log.d(TAG, "Verification email sent to: " + email);
                                } else {
                                    showToast("Failed to send verification email.");
                                    Log.e(TAG, "Error sending verification email", emailTask.getException());
                                }
                            });
                        }
                    } else {
                        showToast("Signup failed: " + task.getException().getMessage());
                        Log.e(TAG, "Firebase sign-up failed", task.getException());
                    }
                });
    }

    private void saveUserDetails() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String mobile = editTextMobile.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile)) {
            showToast("All fields are required");
            return;
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful() && firebaseUser.isEmailVerified()) {
                    saveUserToDatabase(firebaseUser.getUid(), username, email, mobile);
                } else {
                    showToast("Please verify your email before signing up.");
                }
            });
        } else {
            showToast("No verified email found. Please verify first.");
        }
    }

    private void saveUserToDatabase(String uid, String username, String email, String mobile) {
        // ✅ Create User object WITHOUT password for Firebase
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);
        userMap.put("mobile", mobile);

        // ✅ Store in Firebase (without password)
        databaseReference.child(uid).setValue(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // ✅ Save in Room Database (WITH password)
                        User user = new User(username, email, mobile, realPassword);
                        userRepository.insertUser(user, new UserRepository.DatabaseCallback() {
                            @Override
                            public void onSuccess(String message) {
                                showToast("User saved. Navigating to LockPin...");
                                navigateToLockPin();
                            }
                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e(TAG, "Error saving data locally: " + errorMessage);
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to save user in Firebase", task.getException());
                    }
                });
    }


    private void navigateToLockPin() {
        startActivity(new Intent(SignUp.this, lockpin.class));
        finish();
    }

    private void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(SignUp.this, msg, Toast.LENGTH_SHORT).show());
    }
}
