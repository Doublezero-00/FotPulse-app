package com.example.fotpulse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Added for logging

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FotPulse.db";
    public static final int DATABASE_VERSION = 5; // Keep your current version

    // Constants for the News Table (already existing)
    public static final String TABLE_NEWS = "news";
    public static final String COLUMN_NEWS_ID = "id"; // Renamed for clarity with other tables
    public static final String COLUMN_NEWS_TITLE = "title";
    public static final String COLUMN_NEWS_CONTENT = "content";
    public static final String COLUMN_NEWS_CATEGORY = "category";
    public static final String COLUMN_NEWS_MEDIA_URL = "media_url";

    // Constants for the Users Table (newly defined for consistency)
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERS_UID = "uid"; // Matches your 'uid' column name
    public static final String COLUMN_USERS_USERNAME = "username";
    public static final String COLUMN_USERS_EMAIL = "email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables...");

        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "(" +
                COLUMN_NEWS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NEWS_TITLE + " TEXT, " +
                COLUMN_NEWS_CONTENT + " TEXT, " +
                COLUMN_NEWS_CATEGORY + " TEXT, "+
                COLUMN_NEWS_MEDIA_URL + " TEXT)";
        db.execSQL(CREATE_NEWS_TABLE);
        Log.d("DatabaseHelper", "News table created.");

        // Using constants for the Users table creation as well
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USERS_UID + " TEXT PRIMARY KEY, " +
                COLUMN_USERS_USERNAME + " TEXT, " +
                COLUMN_USERS_EMAIL + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
        Log.d("DatabaseHelper", "Users table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS); // Make sure to drop users table too

        // Recreate tables
        onCreate(db);
        Log.d("DatabaseHelper", "Tables dropped and recreated.");
    }
}