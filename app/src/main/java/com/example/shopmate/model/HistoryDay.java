package com.example.shopmate.model;

import java.util.List;

public class HistoryDay {
    private long dayStartMillis;        // start of day in millis
    private List<GroceryItem> items;
    private String title;                 // NEW

    public HistoryDay(long dayStartMillis, List<GroceryItem> items) {
        this.dayStartMillis = dayStartMillis;
        this.items = items;
        this.title = "";
    }

    public long getDayStartMillis() { return dayStartMillis; }
    public List<GroceryItem> getItems() { return items; }

    public String getTitle() { return title; }       // NEW
    public void setTitle(String title) { this.title = title; }  // NEW

}
