package com.example.eventmanagerapp.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper - Singleton pattern
 * Chỉ lo quản lý database (tạo, upgrade)
 */
public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "event_manager.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_EVENT = "events";

    // Singleton instance
    private static AppDatabase instance;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Singleton pattern - chỉ có 1 instance duy nhất
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EVENT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "note TEXT, " +
                "start_time INTEGER NOT NULL, " +
                "end_time INTEGER NOT NULL, " +
                "remind_before INTEGER DEFAULT 0" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Trong production, nên migration thay vì drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}