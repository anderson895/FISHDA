package com.project.fish;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQrCode extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code); // Set the content view for the activity

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                initQRCodeScanner();
            }
        } else {
            initQRCodeScanner();
        }
    }

    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ScanQrCode.this, UserActivity.class)); // Start UserActivity
                finish();
            } else {
                String scannedContent = result.getContents();
                String[] parts = scannedContent.split("~");
                if (parts.length == 5) {
                    String qrName = parts[1];
                    String qrDescription = parts[2];
                    String qrPrice = parts[3];
                    String quantity = parts[4];

                    // Create and configure the dialog
                    Dialog dialog = new Dialog(ScanQrCode.this);
                    dialog.setContentView(R.layout.dialogitem);
                    dialog.setTitle("Buy item");

                    // Initialize views
                    EditText etQty = dialog.findViewById(R.id.quantityEditText);
                    TextView closeButton = dialog.findViewById(R.id.close_button);
                    TextView confirmButton = dialog.findViewById(R.id.confirm_button);

                    // Handle confirm button click
                    confirmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String quantity = etQty.getText().toString();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference userBuyRef = FirebaseDatabase.getInstance().getReference().child("buy").child(userId);

                            userBuyRef.orderByChild("buyName").equalTo(scannedContent).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    DatabaseReference newBuyRef = userBuyRef.push();
                                    String buyId = newBuyRef.getKey();
                                    ClassBuy newBuy = new ClassBuy(qrName, qrDescription, qrPrice, quantity, userId, buyId, "pending");

                                    newBuyRef.setValue(newBuy).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ScanQrCode.this, "Item bought successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ScanQrCode.this, UserActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Log.e("Firebase", "Failed to buy item: " + task.getException().getMessage());
                                                Toast.makeText(ScanQrCode.this, "Failed to buy item", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ScanQrCode.this, UserActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("Firebase", "Failed to check item existence: " + databaseError.getMessage());
                                }
                            });
                        }
                    });

                    // Handle close button click
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    // Show the dialog
                    dialog.show();

                    Toast.makeText(this, "Scanned: " + scannedContent, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "QR code is not belong to our system", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ScanQrCode.this, UserActivity.class);
                    startActivity(intent);
                    finish();
                }



            }
        }
    }
}
