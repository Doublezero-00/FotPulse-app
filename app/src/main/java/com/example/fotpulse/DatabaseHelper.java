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
    // IMPORTANT: Increment this version if you've changed the table schema
    // (e.g., added 'uid' column, removed 'password' column, or added/changed news columns)
    // and run the app before.
    // For example, if your previous version was 5, change it to 6.
    public static final int DATABASE_VERSION = 10; // <--- CHECK AND INCREMENT THIS IF YOU'VE CHANGED NEWS TABLE SCHEMA!

    // Constants for the News Table
    public static final String TABLE_NEWS = "news";
    public static final String COLUMN_NEWS_ID = "_id"; // Changed to _id for common Android convention
    public static final String COLUMN_NEWS_TITLE = "title";
    public static final String COLUMN_NEWS_CONTENT = "content";
    public static final String COLUMN_NEWS_CATEGORY = "category";
    public static final String COLUMN_NEWS_MEDIA_URL = "media_url"; // Optional: for image URLs

    // Constants for the Users Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERS_UID = "uid"; // Firebase User ID
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

        // Create the Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERS_UID + " TEXT PRIMARY KEY, " +
                COLUMN_USERS_USERNAME + " TEXT," +
                COLUMN_USERS_EMAIL + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
        Log.d("DatabaseHelper", "Users table created.");

        // Optional: Populate with some initial news data for testing
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

    // --- User Profile Operations (Existing) ---

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

    // --- News Operations (NEW) ---

    /**
     * Adds a new news item to the database.
     * @param newsItem The NewsItem object to add.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
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

    /**
     * Retrieves all news items for a specific category.
     * @param category The category of news to retrieve (e.g., "Sports", "Academic", "Events").
     * @return A list of NewsItem objects.
     */
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

    /**
     * Inserts sample news data into the database for testing purposes.
     * This method is called in onCreate().
     */
    private void insertSampleNewsData(SQLiteDatabase db) {
        // Clear existing news data to avoid duplicates on upgrade/recreate
        db.execSQL("DELETE FROM " + TABLE_NEWS);

        // --- SPORTS CATEGORY ---
        ContentValues sports1 = new ContentValues();
        sports1.put(COLUMN_NEWS_CATEGORY, "Sports");
        sports1.put(COLUMN_NEWS_TITLE, "Champions League Final: Unforgettable Night!");
        sports1.put(COLUMN_NEWS_CONTENT, "Real Madrid clinched their 15th Champions League title in a thrilling final against Dortmund, with Vinicius Jr. and Carvajal finding the net.");
        sports1.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1549476462-970559e373c7?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // Football match
        db.insert(TABLE_NEWS, null, sports1);

        ContentValues sports2 = new ContentValues();
        sports2.put(COLUMN_NEWS_CATEGORY, "Sports");
        sports2.put(COLUMN_NEWS_TITLE, "Olympics Preparations Intensify Across Cities");
        sports2.put(COLUMN_NEWS_CONTENT, "With just months to go, host cities are buzzing with activity as athletes prepare for the greatest sporting spectacle on Earth. New venues are nearing completion.");
        sports2.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1579737525287-e25f82c42c94?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // Olympic rings / stadium
        db.insert(TABLE_NEWS, null, sports2);

        ContentValues sports3 = new ContentValues();
        sports3.put(COLUMN_NEWS_CATEGORY, "Sports");
        sports3.put(COLUMN_NEWS_TITLE, "Rising Star Breaks Track Record");
        sports3.put(COLUMN_NEWS_CONTENT, "Local athlete Sarah Chen shattered the 100-meter sprint record at the regional championships, cementing her place as a formidable talent.");
        sports3.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1579294244583-04e38e1b2f0a?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // Sprinter on track
        db.insert(TABLE_NEWS, null, sports3);

        // --- ACADEMIC CATEGORY ---
        ContentValues academic1 = new ContentValues();
        academic1.put(COLUMN_NEWS_CATEGORY, "Academic");
        academic1.put(COLUMN_NEWS_TITLE, "University Launches Innovative AI Research Hub");
        academic1.put(COLUMN_NEWS_CONTENT, "FotPulse University proudly announces the opening of its state-of-the-art Artificial Intelligence Research Hub, fostering collaborative breakthroughs.");
        academic1.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1517430816045-df43b7430d21?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // People working on computers/AI
        db.insert(TABLE_NEWS, null, academic1);

        ContentValues academic2 = new ContentValues();
        academic2.put(COLUMN_NEWS_CATEGORY, "Academic");
        academic2.put(COLUMN_NEWS_TITLE, "New Scholarship Fund Benefits STEM Students");
        academic2.put(COLUMN_NEWS_CONTENT, "A generous new scholarship fund has been established to support students pursuing degrees in Science, Technology, Engineering, and Mathematics fields.");
        academic2.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1526374965328-b32c69990567?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // Books/library, focus on learning
        db.insert(TABLE_NEWS, null, academic2);

        ContentValues academic3 = new ContentValues();
        academic3.put(COLUMN_NEWS_CATEGORY, "Academic");
        academic3.put(COLUMN_NEWS_TITLE, "Student-Led Startup Wins National Innovation Award");
        academic3.put(COLUMN_NEWS_CONTENT, "A groundbreaking startup founded by FotPulse students has secured a prestigious national award for its innovative solution to urban sustainability.");
        academic3.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1629904853716-f0bc5963f4b4?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // Students working collaboratively/discussion
        db.insert(TABLE_NEWS, null, academic3);

        // --- EVENTS CATEGORY ---
        ContentValues events1 = new ContentValues();
        events1.put(COLUMN_NEWS_CATEGORY, "Events");
        events1.put(COLUMN_NEWS_TITLE, "Annual Cultural Festival Returns with New Acts");
        events1.put(COLUMN_NEWS_CONTENT, "The much-loved annual cultural festival is back with an exciting lineup of performances, food stalls, and artisan crafts for all ages.");
        events1.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1530177728775-f2d4f2b96317?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // Crowd at a festival/concert
        db.insert(TABLE_NEWS, null, events1);

        ContentValues events2 = new ContentValues();
        events2.put(COLUMN_NEWS_CATEGORY, "Events");
        events2.put(COLUMN_NEWS_TITLE, "Tech Summit 2025: Innovation on Display");
        events2.put(COLUMN_NEWS_CONTENT, "The city's biggest tech summit is set to bring together industry leaders, startups, and enthusiasts to explore the future of technology.");
        events2.put(COLUMN_NEWS_MEDIA_URL, "https://images.unsplash.com/photo-1555546556-9b575a226b52?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"); // Tech conference/expo
        db.insert(TABLE_NEWS, null, events2);

        Log.d("DatabaseHelper", "Sample news data inserted.");
    }
}