package com.example.accessmaster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class SharedPrefUtils {
    private static final String PREF_NAME = "AccessMasterPrefs";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    // Initialize SharedPreferences and editor
    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    // Save email to SharedPreferences
    public static void setLoggedInUserEmail(String email) {
        if (editor != null) {
            editor.putString("user_email", email);
            editor.apply();
        }
    }

    // Retrieve email from SharedPreferences
    public static String getLoggedInUserEmail() {
        return sharedPreferences != null ? sharedPreferences.getString("user_email", null) : null;
    }

    // Save mobile number to SharedPreferences
    public static void setLoggedInUserMobile(String mobile) {
        if (editor != null) {
            editor.putString("user_mobile", mobile);
            editor.apply();
        }
    }

    // Retrieve mobile number from SharedPreferences
    public static String getLoggedInUserMobile() {
        return sharedPreferences != null ? sharedPreferences.getString("user_mobile", null) : null;
    }

    // Save lock pin to SharedPreferences
    public static void setLockPin(String lockPin) {
        if (editor != null) {
            editor.putString("lock_pin", lockPin);
            editor.apply();
        }
    }

    // Retrieve lock pin from SharedPreferences
    public static String getLockPin() {
        return sharedPreferences != null ? sharedPreferences.getString("lock_pin", null) : null;
    }

    // Save keyword-response pair for auto-reply
    public static void saveKeywordResponse(String keyword, String response) {
        if (editor != null) {
            Set<String> savedKeywords = new HashSet<>(sharedPreferences.getStringSet("keywords", new HashSet<>()));

            // Prevent duplicate entries
            savedKeywords.removeIf(entry -> entry.startsWith(keyword + " -> "));
            savedKeywords.add(keyword + " -> " + response);

            editor.putStringSet("keywords", savedKeywords);
            editor.apply();
            Log.d("SharedPrefUtils", "✅ Keyword saved: " + keyword + " -> " + response);
        }
    }

    // Retrieve the auto-reply response for an incoming message
    public static String getStoredResponse(String receivedMessage) {
        if (sharedPreferences == null) return null;

        Set<String> savedKeywords = sharedPreferences.getStringSet("keywords", new HashSet<>());

        for (String entry : savedKeywords) {
            String[] parts = entry.split(" -> ");
            if (parts.length == 2) {
                String keyword = parts[0].trim();
                String response = parts[1].trim();

                if (receivedMessage.toLowerCase().contains(keyword.toLowerCase())) {
                    Log.d("SharedPrefUtils", "✅ Auto-reply matched: " + keyword + " -> " + response);
                    return response;
                }
            }
        }
        Log.d("SharedPrefUtils", "❌ No auto-reply match found for: " + receivedMessage);
        return null;
    }

    // Delete a keyword-response pair
    public static void deleteKeywordResponse(String keyword) {
        if (editor != null) {
            Set<String> savedKeywords = new HashSet<>(sharedPreferences.getStringSet("keywords", new HashSet<>()));

            savedKeywords.removeIf(entry -> entry.startsWith(keyword + " -> "));

            editor.putStringSet("keywords", savedKeywords);
            editor.apply();
            Log.d("SharedPrefUtils", "✅ Keyword deleted: " + keyword);
        }
    }

    // Clear all SharedPreferences data
    public static void clear() {
        if (editor != null) {
            editor.clear();
            editor.apply();
        }
    }
}
