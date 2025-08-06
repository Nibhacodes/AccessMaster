package com.example.accessmaster;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LockScreen extends AppCompatActivity {

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lockscreen);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);

        Button btnActivateAdmin = findViewById(R.id.btn_activate_admin);
        ImageView backarrow = findViewById(R.id.backarrow);

        backarrow.setOnClickListener(v -> {
            finish(); // Navigates to the previous activity
        });

        btnActivateAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateDeviceAdmin();
            }
        });

    }

    private void activateDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This permission is required to lock your phone.");
        startActivity(intent);
    }

    private void lockDevice() {
        if (dpm.isAdminActive(adminComponent)) {
            dpm.lockNow();
            Toast.makeText(this, "🔒 Phone Locked!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "⚠️ Device Admin not active!", Toast.LENGTH_SHORT).show();
        }
    }
}
