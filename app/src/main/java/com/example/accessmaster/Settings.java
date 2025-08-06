package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.accessmaster.database.User;
import com.example.accessmaster.repository.UserRepository;
import com.example.accessmaster.utils.SharedPrefUtils;

public class Settings extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        Button homeButton = findViewById(R.id.homeButton);
        Button forgetpasswordButton = findViewById(R.id.forgetpasswordButton);
        Button forgetpinButton = findViewById(R.id.forgetpinButton);
        Button grantPermissionButton = findViewById(R.id.grantPermissionButton);
        Button feedbackButton = findViewById(R.id.feedbackButton);
        ImageView backarrow = findViewById(R.id.backarrow);
        Button aboutButton = findViewById(R.id.aboutButton);

        userRepository = new UserRepository(this);
        SharedPrefUtils.init(getApplicationContext());

        loadUserData();

        backarrow.setOnClickListener(v -> finish());
        homeButton.setOnClickListener(v -> navigateToActivity(MainActivity.class));
        forgetpasswordButton.setOnClickListener(v -> navigateToActivity(ForgotPasswordActivity.class));
        forgetpinButton.setOnClickListener(v -> navigateToActivity(ForgotPin.class));
        grantPermissionButton.setOnClickListener(v -> navigateToActivity(GrantPermission.class));
        feedbackButton.setOnClickListener(v -> navigateToActivity(Feedback.class));
        aboutButton.setOnClickListener(v -> navigateToActivity(About.class));
    }

    private void loadUserData() {
        String email = SharedPrefUtils.getLoggedInUserEmail();

        if (email != null) {
            userRepository.getUserByEmail(email, user -> runOnUiThread(() -> {
                if (user != null) {
                    tvUserName.setText(user.getUsername());
                    tvUserEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(Settings.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }));
        } else {
            Toast.makeText(this, "No logged-in user!", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(Settings.this, targetActivity));
    }
}
