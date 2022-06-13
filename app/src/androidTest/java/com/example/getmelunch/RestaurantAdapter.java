package com.example.getmelunch;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Models.Restaurant;

public class RestaurantAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final Restaurant restaurant;

    public RestaurantAdapter(Context context, Restaurant restaurant) {
        this.context = context;
        this.restaurant = restaurant;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


    }
    
    @Override
    public int getItemCount() {
        return 0;
    }
}
