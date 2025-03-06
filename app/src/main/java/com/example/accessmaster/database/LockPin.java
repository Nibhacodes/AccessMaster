package com.example.accessmaster.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lock_pin")
public class LockPin {
    @PrimaryKey
    @NonNull
    private String email;
    private String pin;

    public LockPin(@NonNull String email, String pin) {
        this.email = email;
        this.pin = pin;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
