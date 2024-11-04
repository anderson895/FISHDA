package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageUsers extends AppCompatActivity {
    private DatabaseReference usersRef;
    private GridView gridView;
    private List<UserData> dataList; // Change the type to UserData
    private UserAdapter adapter;

    public class UserData {
        private String userName;
        private String userEmail;
        private String userID;

        public UserData(String userName, String userEmail, String userID) {
            this.userName = userName;
            this.userEmail = userEmail;
            this.userID = userID;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserID() {
            return userID;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        // Set the title to "Profile"
        toolbar.setTitle("Manage Users");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        // Set the toolbar as the action bar
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gridView = findViewById(R.id.gridView);
        dataList = new ArrayList<>();

        // Initialize adapter with click listener
        adapter = new UserAdapter(this, dataList, new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click here
                // For example, you can get the clicked item's data
                ManageUsers.UserData clickedItem = dataList.get(position);
                String userName = clickedItem.getUserName();
                String userEmail = clickedItem.getUserEmail();
                String userID = clickedItem.getUserID();
                Log.d("ManageUsers", "Clicked item: " + userID + userName + ", " + userEmail);

                Intent intent = new Intent(ManageUsers.this, ManageUserEdit.class);
                intent.putExtra("userID", userID);
                intent.putExtra("name", userName);
                startActivity(intent);

            }
        });
        gridView.setAdapter(adapter);

        // Initialize Firebase database reference
        usersRef = FirebaseDatabase.getInstance().getReference("usersData");

        // Retrieve all user data
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the dataList before adding new data
                dataList.clear();
                // Iterate through all users
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve user data
                    String userId = userSnapshot.getKey();
                    String userName = userSnapshot.child("name").getValue(String.class);
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String userType = userSnapshot.child("usertype").getValue(String.class);

                    // Add UserData object to dataList
                    dataList.add(new UserData(userName, userEmail, userId));

                    // Handle the retrieved user data (e.g., display in UI)
                    Log.d("UserData", "UserID: " + userId + ", Name: " + userName + ", Email: " + userEmail + ", User type: " + userType);
                }
                // Notify adapter that the data set has changed
                adapter.notifyDataSetChanged();

                // Check if dataList is empty
                if (dataList.isEmpty()) {
                    // Display a message indicating no data available
                    Toast.makeText(ManageUsers.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.d("Firebase", "Failed to retrieve user data: " + databaseError.getMessage());
            }
        });
    }
}
