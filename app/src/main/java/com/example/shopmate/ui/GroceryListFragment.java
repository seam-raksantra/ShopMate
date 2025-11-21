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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.adapter.GroceryAdapter;
import com.example.shopmate.model.GroceryItem;

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
        btnSave = view.findViewById(R.id.ivTopIcon);

        // Make save icon clickable
        btnSave.setClickable(true);
        btnSave.setFocusable(true);

        prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

        loadGroceryItems();

        adapter = new GroceryAdapter(
                getContext(),
                groceryList,
                this::updateSaveButtonVisibility
        );

        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveSelectedItems());

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
        JSONArray arr = new JSONArray();

        for (GroceryItem item : groceryList) {
            if (item.isChecked()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", item.getName());
                    obj.put("image", item.getImage());
                } catch (Exception ignored) {}
                arr.put(obj);
            }
        }

        editor.putString(KEY_HISTORY, arr.toString());
        editor.apply();

        Toast.makeText(getContext(), "Saved to history!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getContext(), HistoryManagementActivity.class));
    }

    // Fill grocery list (placeholder)
    private void loadGroceryItems() {
        groceryList.add(new GroceryItem("Apple", "ic_apple"));
        groceryList.add(new GroceryItem("Banana", "ic_banana"));
        groceryList.add(new GroceryItem("Milk", "ic_milk"));
        groceryList.add(new GroceryItem("Bread", "ic_bread"));
    }
}
