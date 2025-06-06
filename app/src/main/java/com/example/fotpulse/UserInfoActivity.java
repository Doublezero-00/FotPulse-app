package com.example.fotpulse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";

    TextView tvUsername;
    TextView tvEmail;
    Button btnEditInfo;
    Button btnSignOut;

    DatabaseHelper dbHelper;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "No Firebase user logged in. Redirecting to LoginActivity.");
            Toast.makeText(this, "Please log in to view your profile.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
            finish(); //
            return; //
        }

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnSignOut = findViewById(R.id.btnSignOut);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked. Navigating to NewsActivity.");
                Intent intent = new Intent(UserInfoActivity.this, NewsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        dbHelper = new DatabaseHelper(this);

        loadUserProfile();

        btnEditInfo.setOnClickListener(v -> {
            Log.d(TAG, "Edit Info button clicked. Navigating to EditUserActivity.");
            startActivity(new Intent(UserInfoActivity.this, EditUserActivity.class));
        });

        btnSignOut.setOnClickListener(v -> {
            Log.d(TAG, "Sign Out button clicked. Attempting Firebase sign out.");
            mAuth.signOut();
            Toast.makeText(UserInfoActivity.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (currentUser == null) {
            Log.w(TAG, "loadUserProfile called with null currentUser. Exiting.");
            return;
        }

        tvEmail.setText(currentUser.getEmail());

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase(); // Get a readable database instance

            // Query the local database for user data using Firebase UID
            String[] projection = {
                    DatabaseHelper.COLUMN_USERS_USERNAME,
                    DatabaseHelper.COLUMN_USERS_EMAIL
            };
            String selection = DatabaseHelper.COLUMN_USERS_UID + " = ?";
            String[] selectionArgs = {currentUser.getUid()};

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    projection,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int usernameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERS_USERNAME);
                String username = cursor.getString(usernameIndex);
                tvUsername.setText(username);
                Log.d(TAG, "Local user profile loaded: Username=" + username + ", Firebase Email=" + currentUser.getEmail());
            } else {
                Log.d(TAG, "No local profile found for Firebase UID: " + currentUser.getUid() + ". Inserting default.");

                String defaultUsername = currentUser.getDisplayName();
                if (defaultUsername == null || defaultUsername.isEmpty()) {
                    defaultUsername = "Default User";
                }

                tvUsername.setText(defaultUsername);
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_USERS_UID, currentUser.getUid());
                values.put(DatabaseHelper.COLUMN_USERS_USERNAME, defaultUsername);
                values.put(DatabaseHelper.COLUMN_USERS_EMAIL, currentUser.getEmail());

                SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
                long newRowId = writableDb.insert(DatabaseHelper.TABLE_USERS, null, values);
                writableDb.close();

                if (newRowId != -1) {
                    Log.d(TAG, "Default user info inserted into local DB with row ID: " + newRowId);
                    Toast.makeText(this, "Local profile created.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to insert default user info into local DB for UID: " + currentUser.getUid());
                    Toast.makeText(this, "Error creating local profile.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user profile: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to load user info: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}