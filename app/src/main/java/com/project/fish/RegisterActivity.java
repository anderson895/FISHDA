package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_role, usernameEditText, et_gradesection, et_email, passwordEditText, passwordEditText2;

    private ImageView passwordVisibilityToggle,passwordVisibilityToggle2;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    public class UserData {
        private String name;
        private String email;
        private String usertype;

        public UserData() {
            // Default constructor required for Firebase
        }

        public UserData(String name, String email,String usertype) {
            this.name = name;
            this.email = email;
            this.usertype = usertype;
        }

        // Getter methods for Firebase serialization
        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
        public String getUsertype() {
            return usertype;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        usernameEditText = findViewById(R.id.usernameEditText);
        et_email = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText2 = findViewById(R.id.passwordEditText2);
        registerButton = findViewById(R.id.registerButton);

        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        passwordVisibilityToggle2 = findViewById(R.id.passwordVisibilityToggle2);

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


        passwordVisibilityToggle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                int inputType = passwordEditText2.getInputType();
                if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Password is currently visible, hide it
                    passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisibilityToggle2.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    // Password is currently hidden, show it
                    passwordEditText2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordVisibilityToggle2.setImageResource(R.drawable.ic_eye_open);
                }
                // Move the cursor to the end of the input to maintain cursor position
                passwordEditText2.setSelection(passwordEditText2.getText().length());
            }
        });

        FirebaseApp.initializeApp(this);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("usersData");

        TextView login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Corrected Intent creation
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    private void registerUser() {
        final String username = usernameEditText.getText().toString();
        final String email = et_email.getText().toString();
        final String password = passwordEditText.getText().toString();
        final String password2 = passwordEditText2.getText().toString();

        if (!email.endsWith("@gmail.com") && !email.endsWith("@yahoo.com")) {
            Toast.makeText(RegisterActivity.this, "Email must be Gmail or Yahoo.", Toast.LENGTH_SHORT).show();
        }
        else{


            if(password.equals(password2)){
                // Check for duplicate email before attempting to register
                mAuth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
                                        // Email is not registered, proceed with registration
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            String userId = user.getUid();
                                                            Toast.makeText(RegisterActivity.this, "Registered Successfully. ", Toast.LENGTH_SHORT).show();
                                                            UserData userData = new UserData(username, email,"user");

                                                            // Store data in the database
                                                            mDatabase.child(userId).setValue(userData);

                                                            et_email.setText("");
                                                            passwordEditText.setText("");
                                                            passwordEditText2.setText("");
                                                            usernameEditText.setText("");
                                                        } else {
                                                            // Registration failed
                                                            Toast.makeText(RegisterActivity.this, "Email is already registered. or Password is too short", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Email already exists
                                        Toast.makeText(RegisterActivity.this, "Email is already registered.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle the exception
                                    Log.e("SignupActivity", "Error checking email existence: " + task.getException().getMessage());
                                    Toast.makeText(RegisterActivity.this, "Error checking email existence.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(RegisterActivity.this, "Password not match.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}