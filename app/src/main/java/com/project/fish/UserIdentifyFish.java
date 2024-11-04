package com.project.fish;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.project.fish.ml.Fish2;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
public class UserIdentifyFish extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView result, confidence;
    ImageView imageView;
    Button picture;
    int imageSize = 224;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_identify_fish);
        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);

//        DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Note: This Identification is still under development," +
                " and it will take a long time to finish training the data sets needed to analyze exact fish.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


//        DIALOG
        picture.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (ContextCompat.checkSelfPermission(UserIdentifyFish.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    ActivityCompat.requestPermissions(UserIdentifyFish.this, new String[]{Manifest.permission.CAMERA}, 100);
                }
            }

        });
    }

    public void classifyImage(Bitmap image){
        try {
            Fish2 fish= Fish2.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4* imageSize * imageSize * 3);
            int [] intValues = new int [imageSize * imageSize];
            image.getPixels(intValues, 0 , image.getWidth(), 0 ,0, image.getWidth(),image.getHeight());
            int pixel =0;
            for(int i = 0;i <imageSize; i++){
                for(int j = 0; j <imageSize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val>> 16)&0xFF)*(1.f/ 255.f));
                    byteBuffer.putFloat(((val>> 8)&0xFF)*(1.f/ 255.f));
                    byteBuffer.putFloat((val &0xFF)*(1.f/ 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Fish2.Outputs outputs = fish.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0 ; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Gold Fish", "Bangus","Tambakol","Tamban","Tilapia"};


            result.setText(classes[maxPos]);
            result.setFocusableInTouchMode(true);
            result.requestFocus();

            if (result instanceof EditText) {
                ((EditText) result).setSelection(result.getText().length()); // Cast to EditText and set cursor to end
                ((EditText) result).setKeyListener(new EditText(this).getKeyListener()); // Enable editing and copying
            }

            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            confidence.setText(s);
            confidence.setFocusable(false); // Disable editing of confidence values

            // Releases model resources if no longer used.
            fish.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());

            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}