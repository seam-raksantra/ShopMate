package com.example.shopmate.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.shopmate.R;
import com.example.shopmate.adapter.HistoryAdapter;
import com.example.shopmate.model.GroceryItem;
import com.example.shopmate.model.HistoryDay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryManagementActivity extends AppCompatActivity {

    private RecyclerView rvHistoryDays;
    private LinearLayout emptyStateLayout;
    private ImageView btnBackHistory;

    private static final String PREF_NAME = "history";
    private static final String KEY_HISTORY = "history_items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_management);

        rvHistoryDays = findViewById(R.id.rvHistoryDays);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnBackHistory = findViewById(R.id.btnBackHistory);

        rvHistoryDays.setLayoutManager(new LinearLayoutManager(this));

        btnBackHistory.setOnClickListener(v -> onBackPressed());

        refreshHistoryList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHistoryList();
    }

    private void refreshHistoryList() {
        List<HistoryDay> days = loadGroupedHistory();
        if (days.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvHistoryDays.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            rvHistoryDays.setVisibility(View.VISIBLE);
            HistoryAdapter adapter = new HistoryAdapter(days, this::openDayDetail, this::deleteList, this::editListName);
            rvHistoryDays.setAdapter(adapter);
        }
    }

    private List<HistoryDay> loadGroupedHistory() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<HistoryDay> days = new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<GroceryItem>>() {}.getType();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(KEY_HISTORY + "_")) {
                String listName = entry.getKey().substring((KEY_HISTORY + "_").length());
                String json = entry.getValue().toString();
                List<GroceryItem> items = gson.fromJson(json, type);
                if (items != null && !items.isEmpty()) {
                    long dayStart = getDayStart(items.get(0).getDateSaved());
                    HistoryDay day = new HistoryDay(dayStart, items);
                    day.setTitle(listName);
                    days.add(day);
                }
            }
        }
        return days;
    }

    private long getDayStart(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private void openDayDetail(HistoryDay day) {
        Intent intent = new Intent(this, HistoryDetailActivity.class);
        intent.putExtra("dayStart", day.getDayStartMillis());
        intent.putExtra("listName", day.getTitle());
        startActivity(intent);
    }

    private void deleteList(String listName) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_HISTORY + "_" + listName);
        editor.apply();
        refreshHistoryList();
    }

    private void editListName(String oldName, String newName) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String oldKey = KEY_HISTORY + "_" + oldName;
        String newKey = KEY_HISTORY + "_" + newName;
        String json = prefs.getString(oldKey, "");
        editor.remove(oldKey);
        editor.putString(newKey, json);
        editor.apply();
        refreshHistoryList();
    }
}
