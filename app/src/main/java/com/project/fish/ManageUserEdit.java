package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManageUserEdit extends AppCompatActivity {

    private String userID, name;

    private TextView editName, saveBtn, deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_edit);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        // Set the title to "Profile"
        toolbar.setTitle("Manage Users");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));

        // Set the toolbar as the action bar
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editName = findViewById(R.id.editName);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("userID")) {
            userID = intent.getStringExtra("userID");
        }
        if (intent != null && intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
            editName.setText(name);

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newName = editName.getText().toString();
                    userID = intent.getStringExtra("userID");

                    if (userID != null) {
                        // Update the user document in Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersData").child(userID);

                        // Create a map to update the name field
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", newName);

                        userRef.updateChildren(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ManageUserEdit.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ManageUserEdit.this, ManageUsers.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ManageUserEdit.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ManageUserEdit.this, "User ID is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userID = intent.getStringExtra("userID");
                    if (userID != null) {
                        // Get a reference to the user node in the database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersData").child(userID);

                        // Remove the user data from the database
                        userRef.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ManageUserEdit.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ManageUserEdit.this, ManageUsers.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ManageUserEdit.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ManageUserEdit.this, "User ID is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
}
