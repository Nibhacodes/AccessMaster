package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class front extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        // Automatically navigate to SignUp after a 10-second delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(front.this, Login.class); // Navigate to SignUp
                startActivity(intent);
                finish(); // Finish the current activity to prevent back navigation
            }
        }, 5000); // 10-second delay (10,000 milliseconds)
    }
}
