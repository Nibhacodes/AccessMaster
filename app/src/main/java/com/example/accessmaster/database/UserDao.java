package com.example.accessmaster.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT 1 FROM users WHERE email = :email LIMIT 1")
    Boolean isEmailRegistered(String email);

    @Query("SELECT 1 FROM users WHERE username = :username LIMIT 1")
    Boolean isUsernameTaken(String username);

    @Query("SELECT 1 FROM users WHERE mobile = :mobile LIMIT 1")
    Boolean isMobileTaken(String mobile);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    // Delete a user by email
    @Query("DELETE FROM users WHERE email = :email")
    void deleteByEmail(String email);

    // Update password
    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    void updatePassword(String email, String newPassword);

    // Update a user record
    @Update
    void updateUser(User user); // Added for updating the user object.
}
