package com.example.fotpulse;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditUserActivity extends AppCompatActivity {

    private static final String TAG = "EditUserActivity";

    EditText etUsername;
    TextView tvEmailDisplay;
    Button btnSave, btnCancel;

    DatabaseHelper dbHelper;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        etUsername = findViewById(R.id.etEditUsername);
        tvEmailDisplay = findViewById(R.id.tvEditEmailDisplay);
        btnSave = findViewById(R.id.btnSaveEdit);
        btnCancel = findViewById(R.id.btnCancelEdit);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        loadCurrentUserInfoForEdit();

        btnSave.setOnClickListener(v -> saveUserInfo());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadCurrentUserInfoForEdit() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    new String[]{DatabaseHelper.COLUMN_USERS_USERNAME, DatabaseHelper.COLUMN_USERS_EMAIL},
                    DatabaseHelper.COLUMN_USERS_UID + " = ?",
                    new String[]{currentUser.getUid()},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERS_USERNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERS_EMAIL));

                etUsername.setText(username);
                tvEmailDisplay.setText("Email: " + email);
            } else {

                Log.w(TAG, "No local profile found for editing. Pre-filling with Firebase data.");
                etUsername.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Default User");
                tvEmailDisplay.setText("Email: " + currentUser.getEmail());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading current user info for editing: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading user data for editing.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private void saveUserInfo() {
        String newUsername = etUsername.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERS_USERNAME, newUsername);

            int rowsAffected = db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_USERS_UID + " = ?",
                    new String[]{currentUser.getUid()}
            );

            if (rowsAffected > 0) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User profile updated for UID: " + currentUser.getUid());
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile. User not found locally?", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Failed to update profile. No rows affected for UID: " + currentUser.getUid());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving user info: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving profile: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) db.close();
        }
    }
}