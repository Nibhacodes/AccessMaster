package com.example.accessmaster.repository;

import android.content.Context;

import com.example.accessmaster.database.AppDatabase;
import com.example.accessmaster.database.LockPin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class lockpinRepository {

    private final AppDatabase database;
    private final ExecutorService executorService;

    public lockpinRepository(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Insert a LockPin asynchronously into the database.
     *
     * @param lockPin   The LockPin object to insert.
     * @param onSuccess Callback to execute on successful insertion.
     * @param onFailure Callback to execute on insertion failure.
     */
    public void insertLockPin(LockPin lockPin, Runnable onSuccess, Runnable onFailure) {
        executorService.execute(() -> {
            try {
                database.lockPinDao().insertLockPin(lockPin);
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception e) {
                if (onFailure != null) {
                    onFailure.run();
                }
            }
        });
    }

    /**
     * Retrieve a LockPin by email asynchronously.
     *
     * @param email    The email associated with the LockPin.
     * @param callback Callback to handle the LockPin result.
     */
    public void getLockPinByEmail(String email, Callback<LockPin> callback) {
        executorService.execute(() -> {
            LockPin lockPin = null;
            try {
                lockPin = database.lockPinDao().getLockPinByEmail(email);
            } catch (Exception e) {
                // Handle any potential exceptions, if needed.
            }
            if (callback != null) {
                callback.onComplete(lockPin);
            }
        });
    }

    /**
     * Retrieve only the PIN for a specific email asynchronously.
     *
     * @param email    The email to query the PIN.
     * @param callback Callback to handle the PIN result.
     */
    public void getPinByEmail(String email, Callback<String> callback) {
        executorService.execute(() -> {
            String pin = null;
            try {
                pin = database.lockPinDao().getPinByEmail(email);
            } catch (Exception e) {
                // Handle any potential exceptions.
            }
            if (callback != null) {
                callback.onComplete(pin);
            }
        });
    }

    /**
     * Update the PIN for a specific email asynchronously.
     *
     * @param email     The email for which the PIN should be updated.
     * @param newPin    The new PIN to set.
     * @param onSuccess Callback to execute on successful update.
     * @param onFailure Callback to execute on update failure.
     */
    public void updatePin(String email, String newPin, Runnable onSuccess, Runnable onFailure) {
        executorService.execute(() -> {
            try {
                database.lockPinDao().updatePin(email, newPin);
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception e) {
                if (onFailure != null) {
                    onFailure.run();
                }
            }
        });
    }

    /**
     * Update a LockPin object asynchronously.
     *
     * @param lockPin   The LockPin object to update.
     * @param onSuccess Callback to execute on successful update.
     * @param onFailure Callback to execute on update failure.
     */


    /**
     * Generic callback interface to handle asynchronous results.
     *
     * @param <T> The type of result to handle.
     */
    public interface Callback<T> {
        void onComplete(T result);
    }
}
