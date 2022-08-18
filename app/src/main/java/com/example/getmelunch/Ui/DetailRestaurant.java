package com.example.getmelunch.Ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Models.Places.Restaurant;
import com.example.getmelunch.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class DetailRestaurant extends AppCompatActivity {

    private Restaurant restaurant;
    private ImageView ivRestaurantImage;
    private FloatingActionButton fab;
    private TextView tvRestaurantName;
    private TextView tvRestaurantAddress;
    private LinearLayout llRestaurantPhone;
    private LinearLayout llRestaurantLike;
    private LinearLayout llRestaurantWebsite;
    private RecyclerView rvRestaurantParticipating;

    PlacesClient placesClient;

    //Activity for the detail of the restaurant
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        initView();

        PlacesClient placesClient = Places.createClient(this);
        //Get the restaurant from the intent
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        if (restaurant != null) {
            bindData(restaurant);
        }
    }

    private void bindData(Restaurant restaurant) {
        //address & name
        tvRestaurantName.setText(restaurant.getName());
        tvRestaurantAddress.setText(restaurant.getVicinity());
        System.out.println("/// " + restaurant.getVicinity() + ", " + restaurant.getName());

        //image
        Picasso.get()
                .load(restaurant.getPhotos().get(0).getPhotoUrl())
                .fit()
                .centerCrop()
                .into(ivRestaurantImage);
        System.out.println(restaurant.getPhotos().get(0).getPhotoUrl());
    }

    private void initView() {
        ivRestaurantImage = (ImageView) findViewById(R.id.iv_restaurant_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tvRestaurantName = (TextView) findViewById(R.id.tv_restaurant_name);
        tvRestaurantAddress = (TextView) findViewById(R.id.tv_restaurant_address);
        llRestaurantPhone = (LinearLayout) findViewById(R.id.ll_restaurant_phone);
        llRestaurantLike = (LinearLayout) findViewById(R.id.ll_restaurant_like);
        llRestaurantWebsite = (LinearLayout) findViewById(R.id.ll_restaurant_website);
        rvRestaurantParticipating = (RecyclerView) findViewById(R.id.rv_restaurant_participating);
    }
}
