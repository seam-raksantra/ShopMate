package com.example.shopmate.model;

public class GroceryItem {
    private String name;
    private String image;
    private boolean isChecked; // <-- Checkbox state

    public GroceryItem(String name, String image) {
        this.name = name;
        this.image = image;
        this.isChecked = false; // default unchecked
    }

    // Name
    public String getName() {
        return name;
    }

    // Image filename
    public String getImage() {
        return image;
    }

    // Checkbox state
    public boolean isChecked() {
        return isChecked;
    }

    // Update checkbox state
    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }
}
