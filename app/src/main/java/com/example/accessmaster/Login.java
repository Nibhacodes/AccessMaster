package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.repository.UserRepository;


public class Login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button loginButton;
    private TextView linkRegister, linkForgotPassword;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.button11);
        linkRegister = findViewById(R.id.link_register);
        linkForgotPassword = findViewById(R.id.link_forgotPassword);

        // Initialize Repository
        userRepository = new UserRepository(this);

        // Set up button listeners
        loginButton.setOnClickListener(v -> validateAndLogin());
        linkRegister.setOnClickListener(v -> navigateToSignUp());
        linkForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
    }

    private void validateAndLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check email and password in Room database
        userRepository.isEmailRegistered(email, isRegistered -> {
            if (!isRegistered) {
                runOnUiThread(() -> Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show());
            } else {
                userRepository.getUserByEmail(email, user -> {
                    if (user != null && user.getPassword().equals(password)) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity
    }
}
