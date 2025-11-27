package com.example.shopmate.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopmate.R;
import com.example.shopmate.adapter.GroceryAdapter;
import com.example.shopmate.data.GroceryData;
import com.example.shopmate.model.GroceryItem;

import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    private TextView txtTitle;
    private RecyclerView recyclerView;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        // Init Views
        txtTitle = findViewById(R.id.txtCategoryTitle);
        recyclerView = findViewById(R.id.recyclerView);
        ivBack = findViewById(R.id.ivBack);

        // Handle Back Button
        ivBack.setOnClickListener(v -> onBackPressed());

        // Get category from Intent
        String category = getIntent().getStringExtra("category");

        if (category == null) {
            Toast.makeText(this, "Category not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set Title
        txtTitle.setText(category + " List");

        // Load correct data list
        List<GroceryItem> data;

        switch (category) {
            case "Vegetable":
                data = GroceryData.getVegetables();
                break;

            case "Fruits":
                data = GroceryData.getFruits();
                break;

            case "Meats":
                data = GroceryData.getMeats();
                break;

            case "Dairy":
                data = GroceryData.getDairy();
                break;

            default:
                data = null;
                Toast.makeText(this, "Unknown Category", Toast.LENGTH_SHORT).show();
                break;
        }

        // Setup Adapter
        GroceryAdapter adapter = new GroceryAdapter(this, data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
