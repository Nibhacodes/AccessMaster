package com.example.accessmaster;

import static com.example.accessmaster.utils.SharedPrefUtils.setLoggedInUserEmail;
import static com.example.accessmaster.utils.SharedPrefUtils.setLoggedInUserMobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.database.LockPin;
import com.example.accessmaster.database.User;
import com.example.accessmaster.repository.UserRepository;
import com.example.accessmaster.utils.SharedPrefUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextMobile, editTextPassword;
    private Button sendVerificationButton, signUpButton;

    private FirebaseAuth firebaseAuth;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SharedPrefUtils.init(getApplicationContext());

        // Initialize Views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);
        sendVerificationButton = findViewById(R.id.sendVerificationButton);
        signUpButton = findViewById(R.id.button9);

        // Initialize Firebase Auth and User Repository
        firebaseAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(this);
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();



        // Send verification email
        sendVerificationButton.setOnClickListener(v -> sendEmailVerification());

        // Sign up button logic
        signUpButton.setOnClickListener(v -> validateAndSignUp());
    }


    private void sendEmailVerification() {
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, "tempPassword123")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Failed to create account for verification.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateAndSignUp() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String mobile = editTextMobile.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

      /*  if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email must be a valid @gmail.com address", Toast.LENGTH_SHORT).show();
            return;
        }else {
            setLoggedInUserEmail(email)
            ;
        }*/
        setLoggedInUserEmail(email);
        if (mobile.length() != 10) {
            Toast.makeText(this, "Mobile number must be 10 digits", Toast.LENGTH_SHORT).show();
            return;
        }else{
            setLoggedInUserMobile(mobile);
        }

        // Check if email exists in Firebase
        firebaseAuth.fetchSignInMethodsForEmail(email)

                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() && task.getResult().getSignInMethods().size() > 0) {
                        Toast.makeText(this, "Email is already registered with Firebase", Toast.LENGTH_SHORT).show();
                    } else {
                        checkVerificationAndSignUp(username, email, mobile, password);
                    }
                });
    }



    private void checkVerificationAndSignUp(String username, String email, String mobile, String password) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "Please verify your email before signing up.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseUser.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && firebaseUser.getEmail().matches(email)) {
                if (firebaseUser.isEmailVerified()) {
                    // Proceed with signup if email is verified
                    userRepository.isEmailRegistered(email, isEmailRegistered -> {
                        if (isEmailRegistered) {
                            runOnUiThread(() -> Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show());
                        } else {
                            userRepository.isUsernameTaken(username, isUsernameTaken -> {
                                if (isUsernameTaken) {
                                    runOnUiThread(() -> Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show());
                                } else {
                                    userRepository.isMobileTaken(mobile, isMobileTaken -> {
                                        if (isMobileTaken) {
                                            runOnUiThread(() -> Toast.makeText(this, "Mobile number is already taken", Toast.LENGTH_SHORT).show());
                                        } else {
                                            completeSignUp(username, email, mobile, password);
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(this, "Please verify your email before signing up.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to check email verification status. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeSignUp(String username, String email, String mobile, String password) {
        // Save user data to Room
        User user = new User();
        user.setUsername(username);
        user.setEmail(email)
        ;
        user.setMobile(mobile);
        user.setPassword(password);

        userRepository.insertUser(user, new UserRepository.DatabaseCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(SignUp.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    navigateToLockPin();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(SignUp.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void navigateToLockPin() {
        Intent intent = new Intent(SignUp.this, lockpin.class);
        startActivity(intent);
        finish();
    }


}