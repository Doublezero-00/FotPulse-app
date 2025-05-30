package com.example.fotpulse;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class NewsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private NewsPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Insert dummy news if none exists
        insertDummyNewsIfNeeded();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new NewsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Sports"); break;
                        case 1: tab.setText("Academic"); break;
                        case 2: tab.setText("Events"); break;
                    }
                }).attach();
    }


    private void insertDummyNewsIfNeeded() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM news", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            ContentValues values = new ContentValues();

            // Sports

            values.put("title", "Big Football Match");
            values.put("content", "Exciting match between A and B!");
            values.put("category", "Sports");
            values.put("media_url", "https://images.unsplash.com/photo-1609333623319-ec4f2a32529b?auto=format&fit=crop&w=800&q=80");
            db.insert("news", null, values);

            // Academic
            values.clear();
            values.put("title", "Exam Results Released");
            values.put("content", "Check the university portal for full details.");
            values.put("category", "Academic");
            values.put("media_url", "https://images.unsplash.com/photo-1609333623319-ec4f2a32529b?auto=format&fit=crop&w=800&q=80");
            db.insert("news", null, values);

            // Events
            values.clear();
            values.put("title", "Hackathon 2025");
            values.put("content", "Register now for the campus-wide hackathon!");
            values.put("category", "Events");
            values.put("media_url", "https://plus.unsplash.com/premium_photo-1677403157589-0583b29f8a23?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
            db.insert("news", null, values);
        }
    }
}
