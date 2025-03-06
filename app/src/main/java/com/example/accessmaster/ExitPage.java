package com.example.accessmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExitPage extends AppCompatActivity {
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_page);

        btn = findViewById(R.id.btn_no);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitLayout();
            }
        });
    }
    protected void showExitLayout() {
        Intent intent = new Intent(ExitPage.this, MainActivity.class);
        intent.putExtra("KEY1", "cancel the opreation");     // ye data transfer krne k liye hota hai one activity to another activity
        intent.putExtra("KEY2", "dismiss the dialog");
        intent.putExtra("KEY3", "cancel the opreation");
        startActivity(intent);
    }
}