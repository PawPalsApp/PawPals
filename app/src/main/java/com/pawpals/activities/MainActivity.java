package com.pawpals.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.pawpals.R;
import com.pawpals.fragments.ExploreFragment;
import com.pawpals.fragments.MessagesFragment;
import com.pawpals.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final Fragment exploreFragment = new ExploreFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment messagesFragment = new MessagesFragment();
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            ParseUser.logOutInBackground(e -> {
                // Move back to login screen and destroy main activity
                Intent i = new Intent(MainActivity.this, LaunchActivity.class);
                startActivity(i);
                setResult(Activity.RESULT_OK);
                Toast.makeText(MainActivity.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                finish();
            });
            return true;
        });

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // check id of item and perform appropriate navigation
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_messages:
                    fragment = messagesFragment;
                    break;
                case R.id.action_profile:
                    fragment = profileFragment;
                    break;
                case R.id.action_explore:
                default:
                    fragment = exploreFragment;
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        });
        // set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_explore);
    }

}