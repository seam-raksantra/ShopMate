package com.example.shopmate.network;

import com.example.shopmate.model.GroceryItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GroceryApi {

    // Will call:
    // /grocery_items?categories_id=eq.{id}&select=*
    @GET("grocery_items")
    Call<List<GroceryItem>> getItemsByCategory(
            @Query("categories_id") String categoryFilter, // e.g. "eq.1"
            @Query("select") String select                  // usually "*"
    );
}
