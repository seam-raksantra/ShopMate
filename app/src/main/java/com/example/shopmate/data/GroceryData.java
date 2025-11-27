package com.example.shopmate.data;

import com.example.shopmate.model.GroceryItem;

import java.util.ArrayList;
import java.util.List;

public class GroceryData {

    public static List<GroceryItem> getVegetables() {
        List<GroceryItem> list = new ArrayList<>();
        list.add(new GroceryItem("Carrot", "carrot"));
        list.add(new GroceryItem("Broccoli", "broccoli"));
        list.add(new GroceryItem("Cabbage", "cabbage"));
        list.add(new GroceryItem("Potato", "potato"));
        list.add(new GroceryItem("Pumpkin", "pumpkin"));
        list.add(new GroceryItem("Cucumber", "cucumber"));
        list.add(new GroceryItem("Lettuce", "lettuce"));
        list.add(new GroceryItem("Bell Pepper", "bell pepper"));
        list.add(new GroceryItem("Aubergine", "aubergine"));
        list.add(new GroceryItem("Leek", "leek"));
        return list;
    }

    public static List<GroceryItem> getFruits() {
        List<GroceryItem> list = new ArrayList<>();
        list.add(new GroceryItem("Apple", "apple"));
        list.add(new GroceryItem("Banana", "banana"));
        list.add(new GroceryItem("Orange", "orange"));
        list.add(new GroceryItem("Mango", "mango"));
        list.add(new GroceryItem("Star Fruit", "star fruit"));
        list.add(new GroceryItem("Mangosteen", "mangosteen"));
        list.add(new GroceryItem("Pineapple", "pineapple"));
        list.add(new GroceryItem("Dragon Fruit", "dragon fruit"));
        list.add(new GroceryItem("SourSop", "soursop"));
        list.add(new GroceryItem("Guava", "guava"));
        return list;
    }

    public static List<GroceryItem> getMeats() {
        List<GroceryItem> list = new ArrayList<>();
        list.add(new GroceryItem("Chicken", "chicken"));
        list.add(new GroceryItem("Pork", "pork"));
        list.add(new GroceryItem("Beef", "beef"));
        list.add(new GroceryItem("Lamb", "lamb"));
        list.add(new GroceryItem("Duck", "duck"));
        list.add(new GroceryItem("Goose", "goose"));
        list.add(new GroceryItem("Veal", "veal"));
        list.add(new GroceryItem("Ham", "ham"));
        list.add(new GroceryItem("Bacon", "bacon"));
        list.add(new GroceryItem("Fish", "fish"));
        list.add(new GroceryItem("Prawn", "prawn"));
        return list;
    }

    public static List<GroceryItem> getDairy() {
        List<GroceryItem> list = new ArrayList<>();
        list.add(new GroceryItem("Milk", "milk"));
        list.add(new GroceryItem("Cheese", "cheese"));
        list.add(new GroceryItem("Butter", "butter"));
        list.add(new GroceryItem("Egg", "egg"));
        list.add(new GroceryItem("Wine", "wine"));
        list.add(new GroceryItem("Pepper", "pepper"));
        list.add(new GroceryItem("Oil", "oil"));
        list.add(new GroceryItem("Sugar", "sugar"));
        list.add(new GroceryItem("Salt", "salt"));
        list.add(new GroceryItem("Soybean", "soybean"));
        return list;
    }
}
