package com.example.fotpulse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FotPulse.db";
    public static final int DATABASE_VERSION = 18;

    public static final String TABLE_NEWS = "news";
    public static final String COLUMN_NEWS_ID = "_id";
    public static final String COLUMN_NEWS_TITLE = "title";
    public static final String COLUMN_NEWS_CONTENT = "content";
    public static final String COLUMN_NEWS_CATEGORY = "category";
    public static final String COLUMN_NEWS_MEDIA_URL = "media_url";

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERS_UID = "uid";
    public static final String COLUMN_USERS_USERNAME = "username";
    public static final String COLUMN_USERS_EMAIL = "email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables...");

        // Create the News table
        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "(" +
                COLUMN_NEWS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NEWS_TITLE + " TEXT NOT NULL, " +
                COLUMN_NEWS_CONTENT + " TEXT, " +
                COLUMN_NEWS_CATEGORY + " TEXT NOT NULL, "+
                COLUMN_NEWS_MEDIA_URL + " TEXT)";
        db.execSQL(CREATE_NEWS_TABLE);
        Log.d("DatabaseHelper", "News table created.");


        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERS_UID + " TEXT PRIMARY KEY, " +
                COLUMN_USERS_USERNAME + " TEXT," +
                COLUMN_USERS_EMAIL + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
        Log.d("DatabaseHelper", "Users table created.");

        insertSampleNewsData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Drop existing tables to recreate with new schema
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db); // Recreate all tables
        Log.d("DatabaseHelper", "Tables dropped and recreated.");
    }


    public long addUserProfile(String uid, String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERS_UID, uid);
        values.put(COLUMN_USERS_USERNAME, username);
        values.put(COLUMN_USERS_EMAIL, email);

        long newRowId = db.insert(TABLE_USERS, null, values);
        db.close();
        Log.d("DatabaseHelper", "addUserProfile: Inserted UID: " + uid + ", Username: " + username + ", Email: " + email + ". Result: " + newRowId);
        return newRowId;
    }

    public int updateUsername(String uid, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERS_USERNAME, newUsername);

        String selection = COLUMN_USERS_UID + " = ?";
        String[] selectionArgs = {uid};

        int rowsAffected = db.update(
                TABLE_USERS,
                values,
                selection,
                selectionArgs
        );
        db.close();
        Log.d("DatabaseHelper", "updateUsername: UID: " + uid + ", New Username: " + newUsername + ". Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    public long addNews(NewsItem newsItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NEWS_CATEGORY, newsItem.getCategory());
        values.put(COLUMN_NEWS_TITLE, newsItem.getTitle());
        values.put(COLUMN_NEWS_CONTENT, newsItem.getContent());
        values.put(COLUMN_NEWS_MEDIA_URL, newsItem.getImageUrl());
        long id = db.insert(TABLE_NEWS, null, values);
        db.close();
        Log.d("DatabaseHelper", "addNews: Added news item: " + newsItem.getTitle() + " to category: " + newsItem.getCategory() + ". Result: " + id);
        return id;
    }


    public List<NewsItem> getAllNewsByCategory(String category) {
        List<NewsItem> newsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_NEWS,
                    new String[]{COLUMN_NEWS_ID, COLUMN_NEWS_CATEGORY, COLUMN_NEWS_TITLE, COLUMN_NEWS_CONTENT, COLUMN_NEWS_MEDIA_URL},
                    COLUMN_NEWS_CATEGORY + " = ?",
                    new String[]{category},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    NewsItem newsItem = new NewsItem(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NEWS_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEWS_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEWS_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEWS_CONTENT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEWS_MEDIA_URL))
                    );
                    newsList.add(newsItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting news by category: " + category, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        Log.d("DatabaseHelper", "getAllNewsByCategory: Found " + newsList.size() + " items for category: " + category);
        return newsList;
    }

    private void insertSampleNewsData(SQLiteDatabase db) {
        // Clear existing news data to avoid duplicates on upgrade/recreate
        db.execSQL("DELETE FROM " + TABLE_NEWS);


        ContentValues sports1 = new ContentValues();
        sports1.put(COLUMN_NEWS_CATEGORY, "Sports");
        sports1.put(COLUMN_NEWS_TITLE, "Champions League Final: Unforgettable Night!");
        sports1.put(COLUMN_NEWS_CONTENT, "Real Madrid clinched their 15th Champions League title in a thrilling final against Dortmund, with Vinicius Jr. and Carvajal finding the net.");
        sports1.put(COLUMN_NEWS_MEDIA_URL, "https://fastly.picsum.photos/id/841/200/300.jpg?hmac=G9hBg_h2jvXDwBgnqCm8LO9PXRrPRWbz1xgdUrMf1Y8");


        db.insert(TABLE_NEWS, null, sports1);

        //Sports
        ContentValues sports2 = new ContentValues();
        sports2.put(COLUMN_NEWS_CATEGORY, "Sports");
        sports2.put(COLUMN_NEWS_TITLE, "Olympics Preparations Intensify Across Cities");
        sports2.put(COLUMN_NEWS_CONTENT, "With just months to go, host cities are buzzing with activity as athletes prepare for the greatest sporting spectacle on Earth. New venues are nearing completion.");
        sports2.put(COLUMN_NEWS_MEDIA_URL, "https://fastly.picsum.photos/id/238/800/600.jpg?hmac=x11Kjfo7lchZw_mrIGtTlwi_ncxdy1RXEYCkgGGaXcA");

        db.insert(TABLE_NEWS, null, sports2);


        //Academic
        ContentValues academic1 = new ContentValues();
        academic1.put(COLUMN_NEWS_CATEGORY, "Academic");
        academic1.put(COLUMN_NEWS_TITLE, "University Launches Innovative AI Research Hub");
        academic1.put(COLUMN_NEWS_CONTENT, "FotPulse University proudly announces the opening of its state-of-the-art Artificial Intelligence Research Hub, fostering collaborative breakthroughs.");
        academic1.put(COLUMN_NEWS_MEDIA_URL, "https://fastly.picsum.photos/id/65/200/300.jpg?hmac=o9HaDBPcrCPi8zfB6MoTe6MNNVPsEN4orpzsHhCPlbU");
        db.insert(TABLE_NEWS, null, academic1);



        //Events
        ContentValues events1 = new ContentValues();
        events1.put(COLUMN_NEWS_CATEGORY, "Events");
        events1.put(COLUMN_NEWS_TITLE, "Annual Cultural Festival Returns with New Acts");
        events1.put(COLUMN_NEWS_CONTENT, "The much-loved annual cultural festival is back with an exciting lineup of performances, food stalls, and artisan crafts for all ages.");
        events1.put(COLUMN_NEWS_MEDIA_URL, "https://fastly.picsum.photos/id/281/200/300.jpg?hmac=KCN8F5QTgxHdeQxLpZ5BOuUEVQEp8jAedlLSRERW7CY");
        db.insert(TABLE_NEWS, null, events1);


        Log.d("DatabaseHelper", "Sample news data inserted.");
    }
}