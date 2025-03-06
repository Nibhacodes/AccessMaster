package com.example.accessmaster.repository;

import android.content.Context;

import com.example.accessmaster.database.AppDatabase;
import com.example.accessmaster.database.User;
import com.example.accessmaster.database.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertUser(User user, DatabaseCallback callback) {
        executorService.execute(() -> {
            try {
                userDao.insert(user);
                callback.onSuccess("User added successfully.");
            } catch (Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void isEmailRegistered(String email, DatabaseCallbackBoolean callback) {
        executorService.execute(() -> {
            Boolean isRegistered = userDao.isEmailRegistered(email);
            callback.onResult(isRegistered != null && isRegistered);
        });
    }

    public void isUsernameTaken(String username, DatabaseCallbackBoolean callback) {
        executorService.execute(() -> {
            Boolean isTaken = userDao.isUsernameTaken(username);
            callback.onResult(isTaken != null && isTaken);
        });
    }

    public void isMobileTaken(String mobile, DatabaseCallbackBoolean callback) {
        executorService.execute(() -> {
            Boolean isTaken = userDao.isMobileTaken(mobile);
            callback.onResult(isTaken != null && isTaken);
        });
    }

    public void getUserByEmail(String email, DatabaseCallbackUser callback) {
        executorService.execute(() -> {
            User user = userDao.getUserByEmail(email);
            callback.onResult(user);
        });
    }

    public void deleteUserByEmail(String email, DatabaseCallback callback) {
        executorService.execute(() -> {
            try {
                userDao.deleteByEmail(email);
                callback.onSuccess("User deleted successfully.");
            } catch (Exception e) {
                callback.onFailure("Failed to delete user: " + e.getMessage());
            }
        });
    }

    public void updatePassword(String email, String newPassword, DatabaseCallback callback) {
        executorService.execute(() -> {
            try {
                userDao.updatePassword(email, newPassword);
                callback.onSuccess("Password updated successfully.");
            } catch (Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void updateUser(User user, DatabaseCallback callback) {
        executorService.execute(() -> {
            try {
                userDao.updateUser(user);
                callback.onSuccess("User updated successfully.");
            } catch (Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public interface DatabaseCallback {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }

    public interface DatabaseCallbackBoolean {
        void onResult(boolean result);
    }

    public interface DatabaseCallbackUser {
        void onResult(User user);
    }
}
