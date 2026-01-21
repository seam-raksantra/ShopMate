package com.example.shopmate.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.EditText;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.adapter.GroceryAdapter;
import com.example.shopmate.model.GroceryItem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroceryListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView btnSave;

    private List<GroceryItem> groceryList = new ArrayList<>();
    private GroceryAdapter adapter;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "history";
    private static final String KEY_HISTORY = "history_items";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnSave = view.findViewById(R.id.btnSave);

        // Make save icon clickable
        btnSave.setClickable(true);
        btnSave.setFocusable(true);

        prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

        loadGroceryItems();

        // 3-argument constructor: Context, list, SelectionListener
        adapter = new GroceryAdapter(
                requireContext(),
                groceryList,
                this::updateSaveButtonVisibility   // callback on selection change
        );

        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveSelectedItems());

        // initial visibility
        updateSaveButtonVisibility();

        return view;
    }

    // Show or hide save icon based on selections
    private void updateSaveButtonVisibility() {
        boolean hasSelected = false;

        for (GroceryItem item : groceryList) {
            if (item.isChecked()) {
                hasSelected = true;
                break;
            }
        }

        btnSave.setVisibility(hasSelected ? View.VISIBLE : View.GONE);
    }

    // Save items in SharedPreferences
    private void saveSelectedItems() {
        List<GroceryItem> selectedList = new ArrayList<>();

        for (GroceryItem item : groceryList) {
            if (item.isChecked()) {
                selectedList.add(item);
            }
        }

        if (selectedList.isEmpty()) {
            Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show dialog to enter list name
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Save Grocery List");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter list name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String listName = input.getText().toString().trim();
            if (listName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert list to JSON using Gson
            String json = new Gson().toJson(selectedList);
            editor.putString(KEY_HISTORY + "_" + listName, json);
            editor.apply();

            Toast.makeText(requireContext(), "Saved as \"" + listName + "\"!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), HistoryManagementActivity.class));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        try {
            builder.show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error showing dialog", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Fill grocery list (placeholder)
    private void loadGroceryItems() {
        groceryList.add(new GroceryItem("Apple", "ic_apple"));
        groceryList.add(new GroceryItem("Banana", "ic_banana"));
        groceryList.add(new GroceryItem("Milk", "ic_milk"));
        groceryList.add(new GroceryItem("Bread", "ic_bread"));
    }
}
