package com.example.getmelunch.Di;

import com.example.getmelunch.Models.Restaurant;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PlacesService {
    @GET("nearbysearch/json")
    retrofit2.Call<Restaurant> getNearbyPlaces(@Url String url);
}
