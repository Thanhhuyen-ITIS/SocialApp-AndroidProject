package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.socialapp.fragments.HomeFragment;
import com.example.socialapp.fragments.NotificationFragment;
import com.example.socialapp.fragments.ProfileFragment;
import com.example.socialapp.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class StartActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Fragment selectorFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    selectorFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_search) {
                    selectorFragment = new SearchFragment();
                } else if (item.getItemId() == R.id.nav_add) {
                    selectorFragment = null;
                    startActivity(new Intent(StartActivity.this, PostActivity.class));
                } else if (item.getItemId() == R.id.nav_noti) {
                    selectorFragment = new NotificationFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectorFragment = new ProfileFragment();
                }

                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
                return true;
            }
        });
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String profileIdd = intent.getString("publisherId");

            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileIdd).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }
}