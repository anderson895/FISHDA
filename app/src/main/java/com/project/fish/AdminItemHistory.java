package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminItemHistory extends AppCompatActivity {
    private GridView gridView;
    private DatabaseReference itemsRef;

    private List<ClassBuy> dataList; // Changed the type to ManageItems.ItemData

    private BuyHistoryAdapterUser adapter;
    private FirebaseAuth mAuth,firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_history);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        // Set the title to "Profile"
        toolbar.setTitle("Buy History");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        // Set the toolbar as the action bar
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String userId = firebaseAuth.getCurrentUser().getUid();


        gridView = findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        adapter = new BuyHistoryAdapterUser(this, dataList, new BuyHistoryAdapterUser.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ClassBuy clickedItem = dataList.get(position);

                String itemName = clickedItem.getBuyName();

            }
        });

        gridView.setAdapter(adapter);

        itemsRef = FirebaseDatabase.getInstance().getReference("buy");

// Retrieve all item data
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the dataList before adding new data
                dataList.clear();

                // Iterate through each user key
                for (DataSnapshot userKeySnapshot : dataSnapshot.getChildren()) {
                    String buyUserID = userKeySnapshot.getKey();

                    // Iterate through each item for the user key
                    for (DataSnapshot itemSnapshot : userKeySnapshot.getChildren()) {
                        // Retrieve item data
                        String buyId = itemSnapshot.getKey();
                        String buyName = itemSnapshot.child("buyName").getValue(String.class);
                        String buyDescription = itemSnapshot.child("buyDescription").getValue(String.class);
                        String buyPrice = itemSnapshot.child("buyPrice").getValue(String.class);
                        String buyQuantity = itemSnapshot.child("buyQuantity").getValue(String.class);
                        String buyStatus = itemSnapshot.child("buyStatus").getValue(String.class);

                        // Check for null values and handle accordingly
                        if (buyName != null && buyDescription != null && buyPrice != null &&
                                buyQuantity != null && buyStatus != null && buyUserID != null) {
                            // Add ItemData object to dataList
                            dataList.add(new ClassBuy(buyName, buyDescription, buyPrice, buyQuantity, buyUserID, buyId, buyStatus));
                        }
                    }
                }

                // Notify adapter that the data set has changed
                adapter.notifyDataSetChanged();

                // Check if dataList is empty
                if (dataList.isEmpty()) {
                    // Display a message indicating no data available
                    Toast.makeText(AdminItemHistory.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(AdminItemHistory.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}