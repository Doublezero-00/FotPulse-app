package com.example.fotpulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment; // Import for Fragment management
import androidx.fragment.app.FragmentManager; // Import for Fragment management
import androidx.fragment.app.FragmentTransaction; // Import for Fragment management

import com.example.fotpulse.fragments.NewsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView; // Import for BottomNavigationView
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Import your new fragment classes
import com.example.fotpulse.fragments.AcademicFragment;
import com.example.fotpulse.fragments.EventsFragment;


public class NewsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // If user is not logged in, redirect to LoginActivity
        if (currentUser == null) {
            startActivity(new Intent(NewsActivity.this, LoginActivity.class));
            finish();
            return;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // Initialize BottomNavigationView
        navigationView = findViewById(R.id.navigation_view);

        // --- Setup Toolbar and Navigation Drawer ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("FotNow"); // Set your title here
        }

        // Set the black navigation icon
        toolbar.setNavigationIcon(R.drawable.ic_academic_black_24dp);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- Setup Navigation View Listener ---
        setupNavigationView();

        // --- Setup Bottom Navigation View Listener ---
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_news) {
                    selectedFragment = new NewsFragment();
                    toolbar.setTitle("News"); // Update toolbar title
                } else if (itemId == R.id.navigation_academic) {
                    selectedFragment = new AcademicFragment();
                    toolbar.setTitle("Academic"); // Update toolbar title
                } else if (itemId == R.id.navigation_events) {
                    selectedFragment = new EventsFragment();
                    toolbar.setTitle("Events"); // Update toolbar title
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        // Load the default fragment (NewsFragment) when the activity starts
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_news); // This will trigger the listener
        }
    }

    private void setupNavigationView() {
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_header_username);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            navEmail.setText(user.getEmail());
            navUsername.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_user_info) {
                    Toast.makeText(NewsActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewsActivity.this, UserInfoActivity.class));
                }  else if (id == R.id.nav_logout) {
                    mAuth.signOut();
                    Toast.makeText(NewsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewsActivity.this, LoginActivity.class));
                    finish();
                } else if (id == R.id.nav_dev_info) {
                    Toast.makeText(NewsActivity.this, "Developer info clicked", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewsActivity.this, ProfileActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    // Method to load fragments into the FrameLayout
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}