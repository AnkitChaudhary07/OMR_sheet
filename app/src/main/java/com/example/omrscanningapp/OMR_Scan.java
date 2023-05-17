package com.example.omrscanningapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

public class OMR_Scan extends AppCompatActivity {

    int CAM_REQ_CODE = 7;
    ImageView imageView;
    Button camButton;
    Bitmap bitmap;
    Mat mat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.omr_scan);

        imageView = findViewById(R.id.imageView);
        camButton = findViewById(R.id.camButton);

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,CAM_REQ_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAM_REQ_CODE && data != null) {
            Uri imageUri = data.getData();
           try {
               //imageView.setImageURI(data.getData());
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                imageView.setImageBitmap(bitmap);

//                mat = new Mat();
//                Utils.bitmapToMat(bitmap,mat);

            } catch (IOException e) {
               Toast.makeText(this, "e", Toast.LENGTH_SHORT).show();
            }
        }
    }
}