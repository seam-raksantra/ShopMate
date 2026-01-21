package com.example.shopmate.controller;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.shopmate.R;
import com.example.shopmate.ui.HistoryManagementActivity;

public class GroceryCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_category);

        LinearLayout cardVegetable = findViewById(R.id.cardVegetable);
        LinearLayout cardFruits = findViewById(R.id.cardFruits);
        LinearLayout cardMeats = findViewById(R.id.cardMeats);
        LinearLayout cardDairy = findViewById(R.id.cardDairy);

        ImageView ivTopIcon = findViewById(R.id.ivTopIcon);
        ivTopIcon.setOnClickListener(v -> startActivity(new Intent(GroceryCategoryActivity.this, HistoryManagementActivity.class)));

        cardVegetable.setOnClickListener(v -> openCategory("Vegetable"));
        cardFruits.setOnClickListener(v -> openCategory("Fruits"));
        cardMeats.setOnClickListener(v -> openCategory("Meats"));
        cardDairy.setOnClickListener(v -> openCategory("Dairy"));
    }

    private void openCategory(String category) {
        Intent intent = new Intent(GroceryCategoryActivity.this, GroceryListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
