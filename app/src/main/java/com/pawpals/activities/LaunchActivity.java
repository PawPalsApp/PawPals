package com.pawpals.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;
import com.pawpals.R;

public class LaunchActivity extends AppCompatActivity {

    public static final String TAG = "LaunchActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Check if user is logged in using ParseUser
        if (ParseUser.getCurrentUser() != null) {
            // If so, skip launch screen
            goMainActivity();
        }

        // Check if app has been previously launched using shared preferences
        String prevStarted = "prevStarted";
        SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!pref.getBoolean(prevStarted, false)) {
            // If not, show launch screen
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
            // Else, skip launch screen
            goLoginActivity();
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            Log.i(TAG, "onClick login button");
            goLoginActivity();
        });
        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(view -> {
            Log.i(TAG, "onClick sign up button");
            goSignUpActivity();
        });
    }


    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }


    private void goSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }


    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}