package com.example.accessmaster;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.accessmaster.utils.SharedPrefUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ScheduledReply extends AppCompatActivity {

    private EditText keywordInput, responseInput;
    private Button addResponseButton, saveScheduleButton;
    private ListView keywordsListView;
    private TimePicker startPicker, endPicker;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> keywordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduledreply);

        // Initialize SharedPreferences Utility
        SharedPrefUtils.init(this);

        // Initialize UI elements
        keywordInput = findViewById(R.id.edit_keyword);
        responseInput = findViewById(R.id.edit_response);
        addResponseButton = findViewById(R.id.button_add_response);
        keywordsListView = findViewById(R.id.list_keywords);
        startPicker = findViewById(R.id.time_picker_start);
        endPicker = findViewById(R.id.time_picker_end);
        saveScheduleButton = findViewById(R.id.button_save_schedule);

        keywordsList = new ArrayList<>();
        loadKeywords();

        // Setup ListView adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, keywordsList);
        keywordsListView.setAdapter(adapter);

        // Add keyword-response pair
        addResponseButton.setOnClickListener(v -> {
            String keyword = keywordInput.getText().toString().trim();
            String response = responseInput.getText().toString().trim();

            if (!keyword.isEmpty() && !response.isEmpty()) {
                SharedPrefUtils.saveKeywordResponse(keyword, response);
                loadKeywords(); // Reload the list to reflect the new entry
                adapter.notifyDataSetChanged();
                keywordInput.setText("");
                responseInput.setText("");
                Toast.makeText(this, "Keyword response saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter both keyword and response", Toast.LENGTH_SHORT).show();
            }
        });

        // Save schedule
        saveScheduleButton.setOnClickListener(v -> {
            int startHour = startPicker.getHour();
            int startMinute = startPicker.getMinute();
            int endHour = endPicker.getHour();
            int endMinute = endPicker.getMinute();

            getSharedPreferences("AccessMasterPrefs", MODE_PRIVATE).edit()
                    .putInt("startHour", startHour)
                    .putInt("startMinute", startMinute)
                    .putInt("endHour", endHour)
                    .putInt("endMinute", endMinute)
                    .apply();

            Toast.makeText(this, "Schedule saved!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadKeywords() {
        Set<String> savedKeywords = getSharedPreferences("AccessMasterPrefs", MODE_PRIVATE)
                .getStringSet("keywords", new HashSet<>());

        keywordsList.clear();
        if (savedKeywords != null) {
            keywordsList.addAll(savedKeywords);
        }
    }
}
