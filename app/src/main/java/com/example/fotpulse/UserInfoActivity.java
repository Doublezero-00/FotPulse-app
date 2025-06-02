package com.example.fotpulse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";

    // UI Components
    TextView tvUsername;
    TextView tvEmail;
    Button btnEditInfo;
    Button btnSignOut;

    // Database Helper instance
    DatabaseHelper dbHelper;

    // Firebase Authentication instance and current user
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Crucial: Check if user is logged in. If not, redirect immediately.
        if (currentUser == null) {
            Log.d(TAG, "No Firebase user logged in. Redirecting to LoginActivity.");
            Toast.makeText(this, "Please log in to view your profile.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
            finish(); // Close this activity
            return; // Stop further execution of onCreate
        }

        // Initialize UI components
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnSignOut = findViewById(R.id.btnSignOut);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Load user profile data from Firebase and local SQLite
        loadUserProfile();

        // Set up click listeners
        btnEditInfo.setOnClickListener(v -> {
            Log.d(TAG, "Edit Info button clicked. Navigating to EditUserActivity.");
            // Navigate to the EditUserActivity (you'll implement this next)
            startActivity(new Intent(UserInfoActivity.this, EditUserActivity.class));
        });

        btnSignOut.setOnClickListener(v -> {
            Log.d(TAG, "Sign Out button clicked. Attempting Firebase sign out.");
            mAuth.signOut(); // Sign out from Firebase
            Toast.makeText(UserInfoActivity.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
            // Redirect to LoginActivity after signing out
            startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
            finish(); // Close this activity
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user profile data when the activity resumes.
        // This is important if data was updated in EditUserActivity and we return here.
        loadUserProfile();
    }

    private void loadUserProfile() {
        // Ensure currentUser is not null before proceeding
        if (currentUser == null) {
            Log.w(TAG, "loadUserProfile called with null currentUser. Exiting.");
            return;
        }

        // Always display email from Firebase as it's the primary source
        tvEmail.setText(currentUser.getEmail());

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase(); // Get a readable database instance

            // Query the local database for user data using Firebase UID
            String[] projection = {
                    DatabaseHelper.COLUMN_USERS_USERNAME, // Use the constant
                    DatabaseHelper.COLUMN_USERS_EMAIL     // Use the constant
            };
            String selection = DatabaseHelper.COLUMN_USERS_UID + " = ?"; // Use the constant
            String[] selectionArgs = {currentUser.getUid()};

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,    // Use the constant
                    projection,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                // User data found in local database
                int usernameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERS_USERNAME); // Use the constant
                String username = cursor.getString(usernameIndex);
                tvUsername.setText(username);
                Log.d(TAG, "Local user profile loaded: Username=" + username + ", Firebase Email=" + currentUser.getEmail());
            } else {
                // No local user data found for this Firebase UID.
                // This is the first time this user's local profile is being accessed.
                // Insert a default entry based on Firebase info.
                Log.d(TAG, "No local profile found for Firebase UID: " + currentUser.getUid() + ". Inserting default.");

                String defaultUsername = currentUser.getDisplayName();
                if (defaultUsername == null || defaultUsername.isEmpty()) {
                    // Fallback if Firebase display name is not set
                    defaultUsername = "Default User";
                }

                tvUsername.setText(defaultUsername); // Display default immediately

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_USERS_UID, currentUser.getUid()); // Use the constant
                values.put(DatabaseHelper.COLUMN_USERS_USERNAME, defaultUsername); // Use the constant
                values.put(DatabaseHelper.COLUMN_USERS_EMAIL, currentUser.getEmail()); // Use the constant

                SQLiteDatabase writableDb = dbHelper.getWritableDatabase(); // Get a writable instance
                long newRowId = writableDb.insert(DatabaseHelper.TABLE_USERS, null, values);
                writableDb.close(); // Close the writable database connection

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
                cursor.close(); // Always close the cursor
            }
            if (db != null) {
                db.close(); // Always close the database connection
            }
        }
    }
}