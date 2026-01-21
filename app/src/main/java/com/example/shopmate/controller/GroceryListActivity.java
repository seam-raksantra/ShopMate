package com.example.shopmate.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopmate.R;
import com.example.shopmate.adapter.GroceryAdapter;
import com.example.shopmate.data.GroceryData;
import com.example.shopmate.model.GroceryItem;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    private TextView txtTitle;
    private RecyclerView recyclerView;
    private ImageView ivBack;
    private EditText edtSearch;

    private FloatingActionButton fabAddItem;
    private ExtendedFloatingActionButton fabSave;
    private List<GroceryItem> data = new ArrayList<>();
    private GroceryAdapter adapter;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "history";
    private static final String KEY_HISTORY = "history_items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        txtTitle = findViewById(R.id.txtCategoryTitle);
        recyclerView = findViewById(R.id.recyclerView);
        ivBack = findViewById(R.id.ivBack);
        edtSearch = findViewById(R.id.edtSearch);

        fabAddItem = findViewById(R.id.fabAddItem);
        fabSave = findViewById(R.id.fabSave);

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

        ivBack.setOnClickListener(v -> onBackPressed());

        String category = getIntent().getStringExtra("category");
        if (category == null) {
            Toast.makeText(this, "Category not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        txtTitle.setText(category + " List");
        final String selectedCategory = category;

        adapter = new GroceryAdapter(this, new ArrayList<>(), this::updateSaveFab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        List<GroceryItem> cached = loadCategoryData(selectedCategory);
        data.clear();
        if (!cached.isEmpty()) {
            data.addAll(cached);
        } else {
            data.addAll(GroceryData.getCategory(selectedCategory));
        }
        adapter.updateData(data);

        GroceryData.fetchCategoryFromSupabase(selectedCategory, new GroceryData.GroceryCallback() {
            @Override
            public void onSuccess(List<GroceryItem> items) {
                runOnUiThread(() -> {
                    if (items == null || items.isEmpty()) return;

                    Toast.makeText(
                            GroceryListActivity.this,
                            "Loaded " + items.size() + " items from Supabase",
                            Toast.LENGTH_SHORT
                    ).show();

                    data.clear();
                    data.addAll(items);
                    adapter.updateData(data);

                    saveCategoryData(selectedCategory, items);
                });
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(GroceryListActivity.this,
                                "Failed to load from server, using local data",
                                Toast.LENGTH_SHORT).show()
                );
            }
        });

        fabAddItem.setOnClickListener(v -> showAddItemDialog());
        fabSave.setOnClickListener(v -> saveSelectedItems());
        fabSave.hide();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void updateSaveFab() {
        int count = 0;
        for (GroceryItem item : data) {
            if (item.isChecked()) count++;
        }
        if (count > 0) fabSave.show();
        else fabSave.hide();
    }

    private void saveItemsToList(String listName, List<GroceryItem> newItems) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String key = KEY_HISTORY + "_" + listName;
        String existingJson = prefs.getString(key, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<GroceryItem>>() {}.getType();

        List<GroceryItem> existingList = new ArrayList<>();
        if (!existingJson.isEmpty()) {
            existingList = gson.fromJson(existingJson, type);
        }

        existingList.addAll(newItems);

        editor.putString(key, gson.toJson(existingList));
        editor.apply();

        Toast.makeText(this, "Saved as \"" + listName + "\"!", Toast.LENGTH_SHORT).show();

        for (GroceryItem item : data) {
            item.setChecked(false);
        }
        adapter.updateData(data);
        fabSave.hide();
    }

    private void saveSelectedItems() {
        List<GroceryItem> selectedList = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (GroceryItem item : data) {
            if (item.isChecked()) {
                item.setDateSaved(now);
                selectedList.add(item);
            }
        }

        if (selectedList.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> existingNames = getExistingListNames();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Grocery List");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_list, null);
        EditText input = dialogView.findViewById(R.id.edtListName);
        ListView listView = dialogView.findViewById(R.id.listViewNames);
        android.widget.ArrayAdapter<String> arrayAdapter =
                new android.widget.ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        existingNames);
        listView.setAdapter(arrayAdapter);

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String listName = input.getText().toString().trim();
            if (listName.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }
            saveItemsToList(listName, selectedList);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = existingNames.get(position);
            input.setText(selectedName);
        });
    }

    private List<String> getExistingListNames() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        java.util.Map<String, ?> allEntries = prefs.getAll();
        List<String> names = new ArrayList<>();
        for (java.util.Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(KEY_HISTORY + "_")) {
                String listName = entry.getKey().substring((KEY_HISTORY + "_").length());
                names.add(listName);
            }
        }
        return names;
    }

    private void showAddItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null, false);
        EditText edtName = dialogView.findViewById(R.id.edtItemName);

        new AlertDialog.Builder(this)
                .setTitle("Add new item")
                .setView(dialogView)
                .setPositiveButton("Add", (d, which) -> {
                    String name = edtName.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // default icon for custom items; exists in drawable
                    GroceryItem newItem =
                            new GroceryItem(name, "ic_launcher_foreground");

                    String category = getIntent().getStringExtra("category");
                    if (category != null) {
                        switch (category) {
                            case "Vegetable":
                                GroceryData.addVegetable(newItem);
                                break;
                            case "Fruits":
                                GroceryData.addFruit(newItem);
                                break;
                            case "Meats":
                                GroceryData.addMeat(newItem);
                                break;
                            case "Dairy":
                                GroceryData.addDairy(newItem);
                                break;
                        }
                        saveCategoryData(category, GroceryData.getCategory(category));
                    }

                    data.add(newItem);
                    adapter.updateData(data);
                    recyclerView.scrollToPosition(data.size() - 1);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveCategoryData(String category, List<GroceryItem> items) {
        SharedPreferences prefs = getSharedPreferences("categories", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString(category, json);
        editor.apply();
    }

    private List<GroceryItem> loadCategoryData(String category) {
        SharedPreferences prefs = getSharedPreferences("categories", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(category, "");
        Type type = new TypeToken<List<GroceryItem>>() {}.getType();
        List<GroceryItem> items = gson.fromJson(json, type);
        return items != null ? items : new ArrayList<>();
    }
}
