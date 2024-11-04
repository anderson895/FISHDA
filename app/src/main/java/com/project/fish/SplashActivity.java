package com.project.fish;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        String userType = sharedPreferences.getString("userType", "");

//        Toast.makeText(this, "User : " + userType, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (userType.equals("user")) {
                    intent = new Intent(SplashActivity.this, UserActivity.class);
                } else if (userType.equals("admin")) {
                    intent = new Intent(SplashActivity.this, AdminActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                intent.putExtra("userType", userType);
                startActivity(intent);
                finish(); // finish splash activity to prevent going back
            }
        }, SPLASH_DELAY);
    }
}
