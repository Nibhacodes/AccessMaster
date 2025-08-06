package com.example.accessmaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Feedback extends AppCompatActivity {

    private int rating = 0;
    private ImageView[] stars = new ImageView[5];
    private Button btnLike, btnNotLike;
    private final String ADMIN_EMAIL = "ankitasingh84634@gmail.com";  // Change to your admin email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);  // Ensure the XML filename matches

        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);
        btnLike = findViewById(R.id.btn_like);
        btnNotLike = findViewById(R.id.btn_not_like);

        // Star rating click listener
        for (int i = 0; i < stars.length; i++) {
            final int index = i;
            stars[i].setOnClickListener(view -> {
                rating = index + 1;
                updateStars();
            });
        }

        // Button to send positive feedback
        btnLike.setOnClickListener(view -> sendFeedbackEmail("I like the app!", rating));

        // Button to send negative feedback
        btnNotLike.setOnClickListener(view -> sendFeedbackEmail("Not satisfied with the app.", rating));
        ImageView backarrow = findViewById(R.id.backarrow);

        backarrow.setOnClickListener(v -> {
            finish(); // Navigates to the previous activity
        });
    }

    private void updateStars() {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.star_filled);  // Use your filled star drawable
            } else {
                stars[i].setImageResource(R.drawable.star_empty);   // Use your empty star drawable
            }
        }
    }

    private void sendFeedbackEmail(String feedbackMessage, int rating) {
        if (rating == 0) {
            Toast.makeText(this, "Please select a rating first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user inputs
        EditText etUserName = findViewById(R.id.et_user_name);
        EditText etUserEmail = findViewById(R.id.et_user_email);
        EditText etFeedbackText = findViewById(R.id.et_feedback_text);

        String userName = etUserName.getText().toString().trim();
        String userEmail = etUserEmail.getText().toString().trim();
        String userFeedback = etFeedbackText.getText().toString().trim();

        // Check if user provided their email
        if (userName.isEmpty() || userEmail.isEmpty() || userFeedback.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String subject = "User Feedback - AccessMaster App";

        // Include name, email, rating, and feedback in email body
        String message = "User Name: " + userName +
                "\nUser Email: " + userEmail +
                "\nUser Rating: " + rating + " stars" +
                "\n\nFeedback: " + feedbackMessage +
                "\n\nUser Suggestion/Problem:\n" + userFeedback;

        // Send email
        EmailSender.sendEmail(this, ADMIN_EMAIL, subject, message);

        Toast.makeText(this, "Feedback sent successfully!", Toast.LENGTH_SHORT).show();
    }

}
