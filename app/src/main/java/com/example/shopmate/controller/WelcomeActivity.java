package com.example.shopmate.controller;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.shopmate.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("WelcomeActivity", "onCreate START");
        setContentView(R.layout.activity_welcome);
        android.util.Log.d("WelcomeActivity", "setContentView DONE");

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, GroceryCategoryActivity.class);
            startActivity(intent);
        });
    }

}
