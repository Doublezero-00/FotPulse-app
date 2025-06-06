package com.example.fotpulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView profileImage = findViewById(R.id.profileImage);
        Glide.with(this)
                .load(R.drawable.mypic)
                .circleCrop()
                .into(profileImage);


        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Exit button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, NewsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
