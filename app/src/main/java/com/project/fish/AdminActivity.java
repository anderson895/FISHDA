package com.project.fish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button manageUsers = findViewById(R.id.manageUsers);
        Button manageItems = findViewById(R.id.manageItems);
        Button logoutBtn = findViewById(R.id.logoutBtn);
        Button manageHistory = findViewById(R.id.manageHistory);
        Button manageOrders = findViewById(R.id.manageOrders);


        manageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageUsers.class);
                startActivity(intent);
            }
        });

        manageItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageItems.class);
                startActivity(intent);
            }
        });

        manageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminItemHistory.class);
                startActivity(intent);
            }
        });

        manageOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageOrders.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAuthenticationStatus();
                Toast.makeText(v.getContext(), "Logout Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AdminActivity.this, SplashActivity.class);
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