package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
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

public class ManageOrders extends AppCompatActivity {
    private GridView gridView;
    private DatabaseReference itemsRef;

    private List<ClassBuy> dataList; // Changed the type to ManageItems.ItemData

    private BuyHistoryAdapterUser adapter;
    private FirebaseAuth mAuth,firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

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
                String buyUserID = clickedItem.getBuyUserID();
                String buyID = clickedItem.getBuyID();

                // Create a dialog with radio buttons for selecting status
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageOrders.this);
                builder.setTitle("Update Status");
                String[] statusOptions = {"Completed", "Canceled"};
                int checkedItem = 0; // Default checked item (Canceled)
                builder.setSingleChoiceItems(statusOptions, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set the selected status
                        String selectedStatus = statusOptions[which];

                        // Show a confirmation dialog
                        AlertDialog.Builder confirmationBuilder = new AlertDialog.Builder(ManageOrders.this);
                        confirmationBuilder.setTitle("Confirm Update");
                        confirmationBuilder.setMessage("Are you sure you want to mark this item as " + selectedStatus + "?");
                        confirmationBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Update the buyStatus field to the selected status
                                DatabaseReference buyRef = FirebaseDatabase.getInstance().getReference("buy")
                                        .child(buyUserID)
                                        .child(buyID);

                                // Update the buyStatus field to the selected status
                                buyRef.child("buyStatus").setValue(selectedStatus.toLowerCase())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ManageOrders.this, "Buy status updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ManageOrders.this, "Failed to update buy status", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        confirmationBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Dismiss the confirmation dialog if "No" is clicked
                            }
                        });
                        confirmationBuilder.show();

                        dialog.dismiss(); // Dismiss the status selection dialog after confirming the action
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the status selection dialog if "Cancel" is clicked
                    }
                });
                builder.show();
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
                                buyQuantity != null && buyStatus != null && buyUserID != null && buyStatus.equals("pending")) {
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
                    Toast.makeText(ManageOrders.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(ManageOrders.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}