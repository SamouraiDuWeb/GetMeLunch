package com.example.getmelunch.Ui;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.getmelunch.Di.Place.PlacesService;
import com.example.getmelunch.R;

public class ListFragment extends Fragment {

    RecyclerView recyclerViewRestaurants;
    PlacesService placesService;
    Location currentLocation;
    int PROXIMITY_RADIUS = 1000;

    //get info from nearby search and bind into recycler view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerViewRestaurants = view.findViewById(R.id.rv_restaurants);

//        initretrofit();

        return view;
    }

//    private void initretrofit() {
//        String url = "https://maps.googleapis.com/maps/";
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        String type = "restaurant";
//        PlacesService service = retrofit.create(PlacesService.class);
//        String strCurrentLocation = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
//        System.out.println("///strCurrentLocation : " + strCurrentLocation);
//        Call<Restaurant> call = service.getNearbyPlaces(strCurrentLocation, PROXIMITY_RADIUS, type, getString(R.string.google_maps_key));
//        call.enqueue(new Callback<Restaurant>() {
//            @Override
//            public void onResponse(Call<Restaurant> call, retrofit2.Response<Restaurant> response) {
//                try {
//                    for (int i = 0; i < response.body().getResults().size(); i++) {
//                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLatitude();
//                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLongitude();
//                        String placeName = response.body().getResults().get(i).getName();
//                        String vicinity = response.body().getResults().get(i).getVicinity();
//                        MarkerOptions markerOptions = new MarkerOptions();
//                        LatLng latLng = new LatLng(lat, lng);
//                        // . . .
//                        // Bind into recycler view
////                        recyclerViewRestaurants.setAdapter(new RestaurantAdapter(getContext(), response.body().getResults()));
//
//                    }
//                } catch (Exception e) {
//                    Log.d("onResponse", "///There is an error" + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<Restaurant> call, Throwable t) {
//
//            }
//        });
//    }
}