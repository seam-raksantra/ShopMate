package com.example.shopmate.controller;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.example.shopmate.R;

public class GroceryCategoryActivity extends AppCompatActivity {

    private LinearLayout cardVegetable, cardFruits, cardMeats, cardDairy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_category);

        cardVegetable = findViewById(R.id.cardVegetable);
        cardFruits    = findViewById(R.id.cardFruits);
        cardMeats     = findViewById(R.id.cardMeats);
        cardDairy     = findViewById(R.id.cardDairy);

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
