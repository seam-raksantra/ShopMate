package com.example.shopmate.data;

import com.example.shopmate.model.GroceryItem;
import com.example.shopmate.network.GroceryApi;
import com.example.shopmate.network.SupabaseClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroceryData {

    private static List<GroceryItem> vegetables = new ArrayList<>();
    private static List<GroceryItem> fruits = new ArrayList<>();
    private static List<GroceryItem> meats = new ArrayList<>();
    private static List<GroceryItem> dairy = new ArrayList<>();

    // category name → Supabase categories_id
    private static final Map<String, Integer> CATEGORY_IDS = new HashMap<>();

    static {
        // Local defaults (fallback)
        vegetables.add(new GroceryItem("Carrot", "carrot"));
        vegetables.add(new GroceryItem("Broccoli", "broccoli"));
        vegetables.add(new GroceryItem("Cabbage", "cabbage"));
        vegetables.add(new GroceryItem("Potato", "potato"));
        vegetables.add(new GroceryItem("Pumpkin", "pumpkin"));
        vegetables.add(new GroceryItem("Cucumber", "cucumber"));
        vegetables.add(new GroceryItem("Lettuce", "lettuce"));
        vegetables.add(new GroceryItem("Bell Pepper", "bell_pepper"));
        vegetables.add(new GroceryItem("Aubergine", "aubergine"));
        vegetables.add(new GroceryItem("Leek", "leek"));

        fruits.add(new GroceryItem("Apple", "apple"));
        fruits.add(new GroceryItem("Banana", "banana"));
        fruits.add(new GroceryItem("Orange", "orange"));
        fruits.add(new GroceryItem("Mango", "mango"));
        fruits.add(new GroceryItem("Star Fruit", "star_fruit"));
        fruits.add(new GroceryItem("Mangosteen", "mangosteen"));
        fruits.add(new GroceryItem("Pineapple", "pineapple"));
        fruits.add(new GroceryItem("Dragon Fruit", "dragon_fruit"));
        fruits.add(new GroceryItem("SourSop", "soursop"));
        fruits.add(new GroceryItem("Guava", "guava"));

        meats.add(new GroceryItem("Chicken", "chicken"));
        meats.add(new GroceryItem("Pork", "pork"));
        meats.add(new GroceryItem("Beef", "beef"));
        meats.add(new GroceryItem("Lamb", "lamb"));
        meats.add(new GroceryItem("Duck", "duck"));
        meats.add(new GroceryItem("Goose", "goose"));
        meats.add(new GroceryItem("Veal", "veal"));
        meats.add(new GroceryItem("Ham", "ham"));
        meats.add(new GroceryItem("Bacon", "bacon"));
        meats.add(new GroceryItem("Fish", "fish"));
        meats.add(new GroceryItem("Prawn", "prawn"));

        dairy.add(new GroceryItem("Milk", "milk"));
        dairy.add(new GroceryItem("Cheese", "cheese"));
        dairy.add(new GroceryItem("Butter", "butter"));
        dairy.add(new GroceryItem("Egg", "egg"));
        dairy.add(new GroceryItem("Wine", "wine"));
        dairy.add(new GroceryItem("Pepper", "pepper"));
        dairy.add(new GroceryItem("Oil", "oil"));
        dairy.add(new GroceryItem("Sugar", "sugar"));
        dairy.add(new GroceryItem("Salt", "salt"));
        dairy.add(new GroceryItem("Soybean", "soybean"));

        // ⚠️ Make sure these match your real categories table ids
        CATEGORY_IDS.put("Vegetable", 1);
        CATEGORY_IDS.put("Fruits",    2);
        CATEGORY_IDS.put("Meats",     3);
        CATEGORY_IDS.put("Dairy",     4);
    }

    public static List<GroceryItem> getVegetables() { return new ArrayList<>(vegetables); }
    public static List<GroceryItem> getFruits()     { return new ArrayList<>(fruits); }
    public static List<GroceryItem> getMeats()      { return new ArrayList<>(meats); }
    public static List<GroceryItem> getDairy()      { return new ArrayList<>(dairy); }

    public static void addVegetable(GroceryItem item) { vegetables.add(item); }
    public static void addFruit(GroceryItem item)     { fruits.add(item); }
    public static void addMeat(GroceryItem item)      { meats.add(item); }
    public static void addDairy(GroceryItem item)     { dairy.add(item); }

    public static List<GroceryItem> getCategory(String category) {
        switch (category) {
            case "Vegetable": return getVegetables();
            case "Fruits":    return getFruits();
            case "Meats":     return getMeats();
            case "Dairy":     return getDairy();
            default:          return new ArrayList<>();
        }
    }

    // ---------- Supabase async fetch ----------

    public interface GroceryCallback {
        void onSuccess(List<GroceryItem> items);
        void onError(Throwable t);
    }

    public static void fetchCategoryFromSupabase(String category, GroceryCallback callback) {
        Integer categoryId = CATEGORY_IDS.get(category);
        if (categoryId == null) {
            callback.onSuccess(getCategory(category)); // fallback
            return;
        }

        GroceryApi api = SupabaseClient.getClient().create(GroceryApi.class);
        String filter = "eq." + categoryId;

        Call<List<GroceryItem>> call = api.getItemsByCategory(filter, "*");
        call.enqueue(new Callback<List<GroceryItem>>() {
            @Override
            public void onResponse(Call<List<GroceryItem>> call,
                                   Response<List<GroceryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(getCategory(category)); // fallback
                }
            }

            @Override
            public void onFailure(Call<List<GroceryItem>> call, Throwable t) {
                callback.onSuccess(getCategory(category)); // fallback
            }
        });
    }
}
