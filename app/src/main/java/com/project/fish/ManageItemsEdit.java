package com.project.fish;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ManageItemsEdit extends AppCompatActivity {
    private String itemName, itemDescription, itemPrice, itemQuantity, itemID, itemQrCode, imageName;

    private EditText editName, editDescription, editPrice, editQuantity;

    private TextView saveBtn, deleteBtn, btnSaveQrCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items_edit);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("itemName")) {
            itemName = intent.getStringExtra("itemName");
        }
        if (intent != null && intent.hasExtra("itemDescription")) {
            itemDescription = intent.getStringExtra("itemDescription");
        }
        if (intent != null && intent.hasExtra("itemPrice")) {
            itemPrice = intent.getStringExtra("itemPrice");
        }
        if (intent != null && intent.hasExtra("itemQuantity")) {
            itemQuantity = intent.getStringExtra("itemQuantity");
        }
        if (intent != null && intent.hasExtra("itemID")) {
            itemID = intent.getStringExtra("itemID");
        }
        if (intent != null && intent.hasExtra("itemQrCode")) {
            itemQrCode = intent.getStringExtra("itemQrCode");
        }

        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription);
        editPrice = findViewById(R.id.editPrice);
        editQuantity = findViewById(R.id.editQuantity);

        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        btnSaveQrCode = findViewById(R.id.btnSaveQrCode);

        editName.setText(itemName);
        editDescription.setText(itemDescription);
        editPrice.setText(itemPrice);
        editQuantity.setText(itemQuantity);

        imageName = itemName.replaceAll("\\s", "_");

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString();
                String newDescription = editDescription.getText().toString();
                String newPrice = editPrice.getText().toString();
                String newQuantity = editQuantity.getText().toString();

                if (itemID != null) {
                    // Update the user document in Realtime Database
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("items").child(itemID);

                    // Create a map to update the name field
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("itemName", newName);
                    updates.put("itemDescription", newDescription);
                    updates.put("itemPrice", newPrice);
                    updates.put("itemQuantity", newQuantity);

                    userRef.updateChildren(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ManageItemsEdit.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ManageItemsEdit.this, ManageItems.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ManageItemsEdit.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ManageItemsEdit.this, "Item ID is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemID != null) {
                    // Get a reference to the user node in the database
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("items").child(itemID);

                    // Remove the user data from the database
                    userRef.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ManageItemsEdit.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ManageItemsEdit.this, ManageItems.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ManageItemsEdit.this, "Failed to delete Item", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ManageItemsEdit.this, "Item ID is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSaveQrCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Retrieve QR code link from your database storage
                String qrCodeLink = itemQrCode;
                new DownloadFile(ManageItemsEdit.this).execute(qrCodeLink); // Pass the activity's context
            }
        });


    }


    public class DownloadFile extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;

        public DownloadFile(Context context) {
            this.mContext = context;
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                // Save the downloaded bitmap to a file
//                saveBitmapToFile(result);
                saveBitmapToDownloads(result);

            }
        }

//        private void saveBitmapToFile(Bitmap bitmap) {
//            // Create a directory if it doesn't exist
//            File dir = new File(mContext.getFilesDir(), "MyImages");
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//
//            // Create a file with a unique name
//            File destination = new File(dir, "image.jpg");
//
//            try {
//                destination.createNewFile();
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos); // Compress to JPEG with quality 100
//                byte[] bitmapdata = bos.toByteArray();
//
//                // Write the bitmap data to the file
//                FileOutputStream fos = new FileOutputStream(destination);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
//
//                // Notify the user about the saved file
//                Log.d("FilePath", "Image saved at: " + destination.getAbsolutePath());
//
//                Toast.makeText(mContext, "Image saved successfully", Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                // Notify the user about any errors
//                Toast.makeText(mContext, "Failed to save image", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private void saveBitmapToDownloads(Bitmap bitmap) {
        // Get the default download directory
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create a file with a unique name
        File destination = new File(downloadsDirectory, imageName + ".jpg");

        try {
            // Write the bitmap data to the file
            FileOutputStream fos = new FileOutputStream(destination);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Compress to JPEG with quality 100
            fos.flush();
            fos.close();

            // Notify the user about the saved file
            Toast.makeText(ManageItemsEdit.this, "Image saved to Downloads folder", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Notify the user about any errors
            Toast.makeText(ManageItemsEdit.this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

}