package com.example.getmelunch.Di.Place;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private static final String BASE_URL = "https://maps.googleapis.com";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static PlacesService getRetrofitApi() {
        return retrofit.create(PlacesService.class);
    }

}
