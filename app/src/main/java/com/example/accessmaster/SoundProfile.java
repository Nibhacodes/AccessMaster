package com.example.accessmaster;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SoundProfile extends AppCompatActivity {

    private static final String TAG = "SoundProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soundprofile);  // Make sure the XML file name is correct

        // Display the formatted SMS command for the user
        String formattedCommand = "AccessMaster <yourLoginPassword> changeSoundProfile";

        // Initialize and handle the "Grant DND Permission" button
        Button btnGrantDnd = findViewById(R.id.btnGrantDndPermission);
        btnGrantDnd.setOnClickListener(view -> requestDndPermission());
    }

    private void requestDndPermission() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager != null && !Settings.System.canWrite(this)) {
            Log.d(TAG, "🚨 DND Access Not Granted! Requesting Permission.");

            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);

            Toast.makeText(this, "Please enable 'Do Not Disturb' access for AccessMaster.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "✅ DND Permission Already Granted!", Toast.LENGTH_SHORT).show();
        }
    }
}
