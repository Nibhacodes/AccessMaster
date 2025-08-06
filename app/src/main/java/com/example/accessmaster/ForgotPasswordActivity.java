package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText inputEmail, inputNewPassword, inputConfirmPassword;
    private View progressBar, submitButton;

    private FirebaseAuth firebaseAuth;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        inputEmail = findViewById(R.id.input_email);
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        progressBar = findViewById(R.id.progressbar);
        submitButton = findViewById(R.id.btn_submit);

        firebaseAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(this);

        submitButton.setOnClickListener(v -> validateAndResetPassword());
        ImageView backarrow = findViewById(R.id.backarrow);

        backarrow.setOnClickListener(v -> {
            finish(); // Navigates to the previous activity
        });
    }

    private void validateAndResetPassword() {
        String email = inputEmail.getText().toString().trim();
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        userRepository.isEmailRegistered(email, isRegistered -> {
            if (!isRegistered) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    Toast.makeText(this, "Email is not registered", Toast.LENGTH_SHORT).show();
                });
            } else {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                userRepository.updatePassword(email, newPassword, new UserRepository.DatabaseCallback() {
                                    @Override
                                    public void onSuccess(String message) {
                                        runOnUiThread(() -> {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setEnabled(true);
                                            Toast.makeText(ForgotPasswordActivity.this, "Password reset successful. Please check your email.", Toast.LENGTH_SHORT).show();
                                            navigateToLogin();
                                        });
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        runOnUiThread(() -> {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setEnabled(true);
                                            Toast.makeText(ForgotPasswordActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    submitButton.setEnabled(true);
                                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email. Try again.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}