package com.example.shopmate.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopmate.R;
import com.example.shopmate.adapter.HistoryProductAdapter;
import com.example.shopmate.model.GroceryItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryDetailActivity extends AppCompatActivity {

    private RecyclerView rvHistoryProducts;
    private ImageView btnBackDetail;
    private TextView tvDetailTitle;
    private List<GroceryItem> itemsForDay;
    private static final String PREF_NAME = "history";
    private static final String KEY_HISTORY = "history_items";
    private String currentListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_detail);

        rvHistoryProducts = findViewById(R.id.rvHistoryProducts);
        btnBackDetail = findViewById(R.id.btnBackDetail);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);

        rvHistoryProducts.setLayoutManager(new LinearLayoutManager(this));
        btnBackDetail.setOnClickListener(v -> finish());

        currentListName = getIntent().getStringExtra("listName");
        tvDetailTitle.setText(currentListName + " Record");

        itemsForDay = loadItemsForDay(currentListName);
        itemsForDay = mergeSameItems(itemsForDay);

        HistoryProductAdapter adapter = new HistoryProductAdapter(
                itemsForDay,
                item -> deleteItem(item, currentListName)
        );
        rvHistoryProducts.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveItems();
    }

    private void saveItems() {
        if (itemsForDay == null) return;

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String key = KEY_HISTORY + "_" + currentListName;
        Gson gson = new Gson();
        editor.putString(key, gson.toJson(itemsForDay));
        editor.apply();
    }

    private List<GroceryItem> loadItemsForDay(String listName) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = KEY_HISTORY + "_" + listName;
        String json = prefs.getString(key, "");
        if (json.isEmpty()) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<GroceryItem>>() {
        }.getType();
        List<GroceryItem> items = gson.fromJson(json, type);
        if (items == null) return new ArrayList<>();

        return items;
    }

    private List<GroceryItem> mergeSameItems(List<GroceryItem> items) {
        Map<String, GroceryItem> map = new LinkedHashMap<>();
        for (GroceryItem item : items) {
            String key = item.getName();
            if (map.containsKey(key)) {
                GroceryItem existing = map.get(key);
                existing.setQuantity(existing.getQuantity() + 1);
            } else {
                if (item.getQuantity() <= 0) item.setQuantity(1);
                map.put(key, item);
            }
        }
        return new ArrayList<>(map.values());
    }

    private void deleteItem(GroceryItem toDelete, String listName) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = KEY_HISTORY + "_" + listName;
        String json = prefs.getString(key, "");
        if (json.isEmpty()) return;

        Gson gson = new Gson();
        Type type = new TypeToken<List<GroceryItem>>() {
        }.getType();
        List<GroceryItem> all = gson.fromJson(json, type);
        if (all == null) return;

        // remove from SharedPreferences list
        List<GroceryItem> remain = new ArrayList<>();
        for (GroceryItem item : all) {
            if (!item.getName().equals(toDelete.getName())) {
                remain.add(item);
            }
        }
        prefs.edit().putString(key, gson.toJson(remain)).apply();

        // remove from current in-memory list and update UI
        if (itemsForDay != null) {
            for (int i = 0; i < itemsForDay.size(); i++) {
                if (itemsForDay.get(i).getName().equals(toDelete.getName())) {
                    itemsForDay.remove(i);
                    break;
                }
            }
            rvHistoryProducts.getAdapter().notifyDataSetChanged();
        }

    }
}