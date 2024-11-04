package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageItems extends AppCompatActivity {

    private DatabaseReference itemsRef;
    private GridView gridView;
    private List<ManageItems.itemData> dataList;
    private ItemAdapter adapter;
    public TextView btn_top;


    public static class itemData {
        private String itemName;
        private String itemDescription;

        private String itemPrice;
        private String itemQuantity;
        private String itemID;
        private String itemQrCode;

        public itemData(String itemName, String itemDescription, String itemPrice, String itemQuantity, String itemID, String itemQrCode) {
            this.itemName = itemName;
            this.itemDescription = itemDescription;
            this.itemPrice = itemPrice;
            this.itemQuantity = itemQuantity;
            this.itemID = itemID;
            this.itemQrCode = itemQrCode;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public String getItemPrice() {
            return itemPrice;
        }

        public String getItemQuantity() {
            return itemQuantity;
        }

        public String getItemID() {
            return itemID;
        }

        public String getItemQrCode() {
            return itemQrCode;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        // Set the title to "Profile"
        toolbar.setTitle("Manage Fish");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        // Set the toolbar as the action bar
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String addItems = "Add Fish";

        btn_top = findViewById(R.id.btn_top);
        btn_top.setVisibility(View.VISIBLE);

        btn_top.setText(addItems);
//        btn top start

        btn_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageItems.this, addItems, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(ManageItems.this);
                // Get the layout inflater.
                LayoutInflater inflater = getLayoutInflater();


                // Inflate and set the layout for the dialog.
                // Pass null as the parent view because it's going in the dialog layout.
                View dialogView = inflater.inflate(R.layout.dialog_add_item, null);

                builder.setView(dialogView)


                        // Add action buttons
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText et_itemName = dialogView.findViewById(R.id.et_itemName);
                                EditText et_itemDescription = dialogView.findViewById(R.id.et_itemDescription);
                                EditText et_itemPrice = dialogView.findViewById(R.id.et_itemPrice);
                                EditText et_itemQuantity = dialogView.findViewById(R.id.et_itemQuantity);

                                final String itemName = et_itemName.getText().toString();
                                final String description = et_itemDescription.getText().toString();
                                final String price = (et_itemPrice.getText().toString());
                                final String quantity = (et_itemQuantity.getText().toString());


                                // Check if email already exists
                                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("items");
                                itemRef.orderByChild("itemName").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Email already registered
                                            Toast.makeText(ManageItems.this, "Fish is already exists", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Email is not registered, proceed with registration
                                            DatabaseReference newDriverRef = itemsRef.push(); // Generate unique ID for the new driver
                                            String itemID = newDriverRef.getKey(); // Get the generated unique ID
                                            uploadQRCodeToStorage(itemName, itemID,description,price,quantity);

                                            // Create a UserData object for the new driver
                                            itemData newDriver = new itemData(itemName, description, price, quantity, itemID, "");


                                            // Add the new driver to the database
                                            newDriverRef.setValue(newDriver)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Driver added successfully
                                                                Toast.makeText(ManageItems.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = getIntent();
                                                                startActivity(intent); // Start the activity again
                                                                // You may choose to update the UI or perform any other actions here
                                                            } else {
                                                                // Failed to add driver
//                                                                Toast.makeText(ManageItems.this, "Failed to add driver", Toast.LENGTH_SHORT).show();
                                                                Log.e("Firebase", "Failed to add Fish: " + task.getException().getMessage());
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                        Log.e("Firebase", "Failed to check Fish existence: " + databaseError.getMessage());
                                    }
                                });

//                                Toast.makeText(ManageItems.this, et_fullname.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });


//        btn top end


        gridView = findViewById(R.id.gridView);
        dataList = new ArrayList<>(); // Initialize dataList as ArrayList<UserData>

        // Initialize adapter with click listener
        adapter = new ItemAdapter(this, dataList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click here
                // For example, you can get the clicked item's data
                ManageItems.itemData clickedItem = dataList.get(position);
                String itemName = clickedItem.getItemName();
                String itemDescription = clickedItem.getItemDescription();
                String itemPrice = String.valueOf(clickedItem.getItemPrice());
                String itemQuantity = String.valueOf(clickedItem.getItemQuantity());
                String itemID = String.valueOf(clickedItem.getItemID());
                String itemQrCode = String.valueOf(clickedItem.getItemQrCode());

                Intent intent = new Intent(ManageItems.this, ManageItemsEdit.class);
                intent.putExtra("itemName", itemName);
                intent.putExtra("itemDescription", itemDescription);
                intent.putExtra("itemPrice", itemPrice);
                intent.putExtra("itemQuantity", itemQuantity);
                intent.putExtra("itemID", itemID);
                intent.putExtra("itemQrCode", itemQrCode);
                startActivity(intent);

            }
        });
        gridView.setAdapter(adapter);

        // Initialize Firebase database reference
        itemsRef = FirebaseDatabase.getInstance().getReference("items");

        // Retrieve all user data
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the dataList before adding new data
                dataList.clear();
                // Iterate through all users
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve user data
                    String itemId = userSnapshot.getKey();
                    String itemName = userSnapshot.child("itemName").getValue(String.class);
                    String itemDescription = userSnapshot.child("itemDescription").getValue(String.class);
                    String itemPrice = (userSnapshot.child("itemPrice").getValue(String.class));
                    String itemQuantity = (userSnapshot.child("itemQuantity").getValue(String.class));
                    String itemQrCode = (userSnapshot.child("itemQrCode").getValue(String.class));

                    // Add UserData object to dataList
                    dataList.add(new ManageItems.itemData(itemName, itemDescription, itemPrice, itemQuantity, itemId, itemQrCode));

                    // Handle the retrieved user data (e.g., display in UI)
                    Log.d("itemsData", "UserID: " + itemId + ", Name: " + itemName);
                }
                // Notify adapter that the data set has changed
                adapter.notifyDataSetChanged();

                // Check if dataList is empty
                if (dataList.isEmpty()) {
                    // Display a message indicating no data available
                    Toast.makeText(ManageItems.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.d("Firebase", "Failed to retrieve Fish data: " + databaseError.getMessage());
            }
        });
    }

    public static Bitmap generateQRCodeBitmap(String qrCodeData, String qrDescription, String qrPrice, String quantity) {
        // Set up hints for QR code generation
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Set QR code margin size

        try {
            // Generate QR code matrix
            String data = "fishqr"+"~"+qrCodeData + "~"+qrDescription+"~"+qrPrice+"~"+quantity;
            QRCodeWriter writer = new QRCodeWriter();
            final int WIDTH_HEIGHT = 512; // Fixed size
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, WIDTH_HEIGHT, WIDTH_HEIGHT, hints);

            // Create bitmap from bit matrix
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Fill bitmap with QR code colors
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            Log.e("QRCodeGenerator", "Error generating QR code", e);
            return null;
        }
    }

    // Method to generate QR code bitmap and upload it to Firebase Storage
    private void uploadQRCodeToStorage(String qrCodeData, String itemID,String qrDescription, String qrPrice, String quantity) {
        // Generate QR code bitmap
        Bitmap qrCodeBitmap = generateQRCodeBitmap(qrCodeData,qrDescription,qrPrice,quantity);

        // Convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] qrCodeByteArray = baos.toByteArray();

        // Define storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference qrCodeRef = storageRef.child("qrcodes/" + itemID + ".png");

        // Upload QR code to Firebase Storage
        UploadTask uploadTask = qrCodeRef.putBytes(qrCodeByteArray);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // QR code uploaded successfully, get the download URL
                    qrCodeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Save the download URL to the item data in the Firebase Realtime Database
                            String downloadUrl = uri.toString();
                            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("items").child(itemID);
                            itemRef.child("itemQrCode").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Firebase", "QR code URL saved successfully");
                                            } else {
                                                Log.e("Firebase", "Failed to save QR code URL: " + task.getException().getMessage());
                                            }
                                        }
                                    });
                        }
                    });
                } else {
                    Log.e("Firebase", "Failed to upload QR code: " + task.getException().getMessage());
                }
            }
        });
    }
}