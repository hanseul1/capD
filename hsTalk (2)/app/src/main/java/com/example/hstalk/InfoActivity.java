package com.example.hstalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class InfoActivity extends AppCompatActivity {
    private EditText userName;
    private EditText userEmail;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SharedPreferences sharedPreferences;
    private String user;
    private String email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);
        userName = (EditText) findViewById(R.id.nav_textview_name);
        userEmail = (EditText) findViewById(R.id.nav_textview_email);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }


        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        user = sharedPreferences.getString(Constants.USER_NAME,"");

        FirebaseUser users = firebaseAuth.getCurrentUser();
        if(users != null){
            LoginActivity loginact = new LoginActivity();
            loginact.getUserInfo(users.getEmail());

            SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFS , MODE_PRIVATE);
            email = sp.getString("email", "No data");
            Toast.makeText(InfoActivity.this,email + " " + user,Toast.LENGTH_SHORT).show();
            userName.setText(user);
            userEmail.setText(email);
        }
    }
}
