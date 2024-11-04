package com.project.fish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private String loginAdmin = "admin@gmail.com";
    private String loginAdminPasswrod = "admin123";

    private Boolean Test = true;

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ImageView passwordVisibilityToggle;

    private TextView register;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set window insets


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        if(Test){
            usernameEditText.setText(loginAdmin);
            passwordEditText.setText(loginAdminPasswrod);
        }

        loginButton = findViewById(R.id.loginButton);
        register = findViewById(R.id.register);
        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usersData");

        loginButton.setOnClickListener(view -> loginUser());

        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                int inputType = passwordEditText.getInputType();
                if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Password is currently visible, hide it
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisibilityToggle.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    // Password is currently hidden, show it
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordVisibilityToggle.setImageResource(R.drawable.ic_eye_open);
                }
                // Move the cursor to the end of the input to maintain cursor position
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            checkUserType(userId);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType(String userId) {
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("usertype").getValue(String.class);
                    if (userType != null) {
                        if (userType.equals("user")) {
                            Toast.makeText(LoginActivity.this, "Login Success. User Type: " + userType, Toast.LENGTH_SHORT).show();
                            saveUserDetails(userId, userType);
                            setAuthenticatedUser();
                            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                            finish();
                        }else if (userType.equals("admin")) {
                            Toast.makeText(LoginActivity.this, "Login Success. User Type: " + userType, Toast.LENGTH_SHORT).show();
                            saveUserDetails(userId, userType);
                            setAuthenticatedUser();
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed. Incorrect user type.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User type not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDetails(String userId, String userType) {
        SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.putString("userId", userId);
        editor.putString("userType", userType);
        editor.apply();
    }

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_AUTHENTICATED = "is_authenticated";

    private void setAuthenticatedUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_AUTHENTICATED, true);
        editor.apply();
    }
}
