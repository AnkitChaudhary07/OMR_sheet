package com.example.omrscanningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class Dummy_users extends AppCompatActivity {
    private Context context = this;
    String[] user = {"User 1","User 2","User 3","User 4","User 5","User 6","User 7","User 8","User 9",};
    RecyclerView recyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //To check if OpenCV has loaded or not
//        if(OpenCVLoader.initDebug()) Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
//        else Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomAdapter c = new CustomAdapter(context,user);
        recyclerView.setAdapter(c);
    }
}