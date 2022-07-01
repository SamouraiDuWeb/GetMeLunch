package com.example.getmelunch.Di.Place;

import com.example.getmelunch.Models.Places.NearbySearchResponse;
import com.example.getmelunch.Models.Places.Restaurant;

import retrofit2.http.GET;
import retrofit2.http.Url;

public interface PlacesService {

    @GET
    retrofit2.Call<NearbySearchResponse> getNearbyPlaces(@Url String url);
}