package com.example.fotpulse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "Splash screen delay finished. Checking Firebase user state.");

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                Intent mainIntent;
                if (currentUser != null) {
                    Log.d(TAG, "User already logged in. Redirecting to NewsActivity.");
                    mainIntent = new Intent(MainActivity.this, NewsActivity.class);
                } else {

                    Log.d(TAG, "No user logged in. Redirecting to LoginActivity.");
                    mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                }
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}