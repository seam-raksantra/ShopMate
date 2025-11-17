package com.example.shopmate.controller;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shopmate.R;

public class GroceryListActivity extends AppCompatActivity {

    private LinearLayout cardVegetable, cardFruits, cardMeats, cardDairy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        // Link views
        cardVegetable = findViewById(R.id.cardVegetable);
        cardFruits = findViewById(R.id.cardFruits);
        cardMeats = findViewById(R.id.cardMeats);
        cardDairy = findViewById(R.id.cardDairy);

        // Click events
        cardVegetable.setOnClickListener(v ->
                Toast.makeText(this, "Vegetable Selected", Toast.LENGTH_SHORT).show()
        );

        cardFruits.setOnClickListener(v ->
                Toast.makeText(this, "Fruits Selected", Toast.LENGTH_SHORT).show()
        );

        cardMeats.setOnClickListener(v ->
                Toast.makeText(this, "Meats Selected", Toast.LENGTH_SHORT).show()
        );

        cardDairy.setOnClickListener(v ->
                Toast.makeText(this, "Dairy Selected", Toast.LENGTH_SHORT).show()
        );
    }
}
