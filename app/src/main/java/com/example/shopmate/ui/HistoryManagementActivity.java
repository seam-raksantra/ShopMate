package com.example.shopmate.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.adapter.GroceryAdapter;
import com.example.shopmate.model.GroceryItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryManagementActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private LinearLayout emptyStateLayout;
    private GroceryAdapter adapter;

    private SharedPreferences prefs;

    private static final String PREF_NAME = "history";
    private static final String KEY_HISTORY = "history_items";

    private List<GroceryItem> savedHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_management);

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        ImageView btnBack = findViewById(R.id.btnBackHistory);
        btnBack.setOnClickListener(v -> finish());

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        loadSavedHistory();
        updateUI();
    }

    private void loadSavedHistory() {
        String json = prefs.getString(KEY_HISTORY, "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<GroceryItem>>() {}.getType();
            savedHistoryList = new Gson().fromJson(json, type);
        } else {
            savedHistoryList = new ArrayList<>();
        }
    }

    private void updateUI() {
        if (savedHistoryList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);

            historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new GroceryAdapter(this, savedHistoryList);
            historyRecyclerView.setAdapter(adapter);
        }
    }
}
