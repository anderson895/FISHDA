package com.project.fish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity {

    private  Button scanBtn,identifyBtn,logoutBtn,historyBtn,scanQrCode,search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        scanBtn = findViewById(R.id.scanBtn);
        identifyBtn = findViewById(R.id.identifyBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        historyBtn = findViewById(R.id.historyBtn);
        scanQrCode = findViewById(R.id.scanQrCode);
        search = findViewById(R.id.search);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, UserItemList.class);
                startActivity(intent);
            }
        });



        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, UserBuyHistory.class);
                startActivity(intent);
            }
        });


        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, ScanQrCode.class);
                startActivity(intent);
            }
        });
        identifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, UserIdentifyFish.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, UserSearchFish.class);
                startActivity(intent);
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAuthenticationStatus();
                Toast.makeText(v.getContext(), "Logout Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UserActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_AUTHENTICATED = "is_authenticated";
    private void removeAuthenticationStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This removes all key-value pairs from the SharedPreferences
        editor.apply();

        SharedPreferences userDetailsSharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        editor = userDetailsSharedPreferences.edit(); // Reuse the editor variable
        editor.clear(); // This removes all key-value pairs from the "userDetails" SharedPreferences
        editor.apply();

    }
}