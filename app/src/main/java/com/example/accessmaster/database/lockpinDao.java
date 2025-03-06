package com.example.accessmaster.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface lockpinDao {
    @Query("SELECT * FROM lock_pin WHERE email = :email LIMIT 1")
    LockPin getLockPinByEmail(String email);

    @Query("SELECT pin FROM lock_pin WHERE email = :email LIMIT 1")
    String getPinByEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLockPin(LockPin lockPin);

    @Query("UPDATE lock_pin SET pin = :pin WHERE email = :email")
    void updatePin(String email, String pin);
}
