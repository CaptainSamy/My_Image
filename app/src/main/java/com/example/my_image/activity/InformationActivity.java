package com.example.my_image.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.my_image.R;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}