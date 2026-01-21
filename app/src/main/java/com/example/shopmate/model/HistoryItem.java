package com.example.shopmate.model;

public class HistoryItem {
    private int id;
    private String name;
    private int quantity;
    private String date;

    public HistoryItem(int id, String name, int quantity, String date) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.date = date;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getDate() { return date; }
}

