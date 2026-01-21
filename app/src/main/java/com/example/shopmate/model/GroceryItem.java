package com.example.shopmate.model;

import com.google.gson.annotations.SerializedName;

public class GroceryItem {

    private String name;

    // Supabase column "icon" → this field
    @SerializedName("icon")
    private String image;

    private boolean checked;
    private long dateSaved;
    private int quantity;
    private double price;

    // Required by Gson
    public GroceryItem() {
        this.checked = false;
        this.dateSaved = 0L;
        this.quantity = 1;
        this.price = 0.0;
    }

    public GroceryItem(String name, String image) {
        this.name = name;
        this.image = image;
        this.checked = false;
        this.dateSaved = 0L;
        this.quantity = 1;
        this.price = 0.0;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }

    public long getDateSaved() { return dateSaved; }
    public void setDateSaved(long dateSaved) { this.dateSaved = dateSaved; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
