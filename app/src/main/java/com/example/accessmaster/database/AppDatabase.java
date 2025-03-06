package com.example.accessmaster.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, LockPin.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    // DAOs
    public abstract UserDao userDao(); // Corrected to provide UserDao instance
    public abstract lockpinDao lockPinDao();

    // Singleton instance
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "access_master_db")
                            .fallbackToDestructiveMigration() // To handle version changes
                            .build();
                }
            }
        }
        return instance;
    }
}
