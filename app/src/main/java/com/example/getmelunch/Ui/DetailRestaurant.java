package com.example.getmelunch.Ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.getmelunch.Models.Places.Restaurant;
import com.example.getmelunch.R;

public class DetailRestaurant extends AppCompatActivity {

    //Activity for the detail of the restaurant
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        //Get the restaurant from the intent
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        //Set the title of the activity
        setTitle(restaurant.getName());
    }
}
