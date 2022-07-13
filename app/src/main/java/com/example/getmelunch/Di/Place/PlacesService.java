package com.example.getmelunch.Di.Place;

import com.example.getmelunch.Models.Places.NearbySearchResponse;
import com.example.getmelunch.Models.Places.Restaurant;
import com.google.android.libraries.places.api.Places;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface PlacesService {

    @GET
    retrofit2.Call<NearbySearchResponse> getNearbyPlaces(@Url String url);

    @GET
    retrofit2.Call<NearbySearchResponse> getNearbySearch(@Url String url);
}