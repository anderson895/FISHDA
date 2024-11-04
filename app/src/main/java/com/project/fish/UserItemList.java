package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class UserItemList extends AppCompatActivity {
    private GridView gridView;
    private DatabaseReference itemsRef;

    private List<ManageItems.itemData> dataList; // Changed the type to ManageItems.ItemData

    private ItemAdapterUser adapter;
    private FirebaseAuth mAuth,firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_item_list);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        // Set the title to "Profile"
        toolbar.setTitle("Buy fish");
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
        adapter = new ItemAdapterUser(this, dataList, new ItemAdapterUser.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ManageItems.itemData clickedItem = dataList.get(position);

                String itemID = clickedItem.getItemID();
                String itemName = clickedItem.getItemName();
                String itemPrice = clickedItem.getItemPrice();
                String itemQuantity = clickedItem.getItemQuantity();
                String itemDescription = clickedItem.getItemDescription();

                // Create a dialog
                Dialog dialog = new Dialog(UserItemList.this);

                // Set the content view of the dialog to your custom layout
                dialog.setContentView(R.layout.dialogitem);

                // You can set title if needed
                dialog.setTitle("Buy item");

                // You can find views and perform actions on them, for example:
                TextView closeButton = dialog.findViewById(R.id.close_button);
                TextView confirm_button = dialog.findViewById(R.id.confirm_button);



                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et_qty = dialog.findViewById(R.id.quantityEditText);
                        String quantity = et_qty.getText().toString();

                        int itemQuantityInt = Integer.parseInt(itemQuantity);
                        int quantityInt = Integer.parseInt(quantity);

                        if(quantityInt <= itemQuantityInt && quantityInt != 0)
                        {

                            int newQuantity = itemQuantityInt - quantityInt;
//                            ADDING TO ORDERS

                            // Get the current user ID
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Reference to the "buy" node under the user's ID
                            DatabaseReference userBuyRef = FirebaseDatabase.getInstance().getReference().child("buy").child(userId);

                            // Check if email already exists
                            userBuyRef.orderByChild("buyName").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // Item is not in the user's buy database, proceed with adding
                                    DatabaseReference newBuyRef = userBuyRef.push(); // Generate unique ID for the new buy item
                                    String buyID = newBuyRef.getKey(); // Get the generated unique ID

                                    ClassBuy newBuy = new ClassBuy(itemName, itemDescription, itemPrice, quantity, userId, buyID,"pending");

                                    // Add the new buy item to the user's buy database
                                    newBuyRef.setValue(newBuy).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Item added successfully
                                                decreaseItem(newQuantity,itemID);

                                                Toast.makeText(UserItemList.this, "Item bought successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(getIntent());
                                            } else {
                                                Log.e("Firebase", "Failed to buy item: " + task.getException().getMessage());
                                                Toast.makeText(UserItemList.this, "Failed to buy item", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle error
                                    Log.e("Firebase", "Failed to check item existence: " + databaseError.getMessage());
                                }
                            });

//                            ADDING TO ORDERS
                        }else{
                            Toast.makeText(UserItemList.this, "Insufficient Items", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });

        gridView.setAdapter(adapter);

        // Initialize Firebase database reference
        itemsRef = FirebaseDatabase.getInstance().getReference("items");

        // Retrieve all item data
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the dataList before adding new data
                dataList.clear();
                // Iterate through all items
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve item data
                    String itemId = itemSnapshot.getKey();
                    String itemName = itemSnapshot.child("itemName").getValue(String.class);
                    String itemDescription = itemSnapshot.child("itemDescription").getValue(String.class);
                    String itemPrice = itemSnapshot.child("itemPrice").getValue(String.class); // Removed unnecessary casting
                    String itemQuantity = itemSnapshot.child("itemQuantity").getValue(String.class);
                    String itemQrCode = itemSnapshot.child("itemQrCode").getValue(String.class);

                    // Add ItemData object to dataList
                    dataList.add(new ManageItems.itemData(itemName, itemDescription, itemPrice, itemQuantity, itemId,itemQrCode));

                    // Handle the retrieved item data (e.g., display in UI)
                    Log.d("itemsData", "ItemID: " + itemId + ", Name: " + itemName);
                }
                // Notify adapter that the data set has changed
                adapter.notifyDataSetChanged();

                // Check if dataList is empty
                if (dataList.isEmpty()) {
                    // Display a message indicating no data available
                    Toast.makeText(UserItemList.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.d("Firebase", "Failed to retrieve item data: " + databaseError.getMessage());
            }
        });
    }

    void decreaseItem (int newQuantity,String itemID){
        // Update item quantity in items collection
        itemsRef.child(itemID).child("itemQuantity").setValue(String.valueOf(newQuantity))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Quantity updated successfully
                            Log.d("Firebase", "Item quantity updated");
                        } else {
                            // Failed to update quantity
                            Log.e("Firebase", "Failed to update item quantity: " + task.getException().getMessage());
                            Toast.makeText(UserItemList.this, "Failed to update item quantity", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}
