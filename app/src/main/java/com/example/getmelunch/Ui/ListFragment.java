package com.example.getmelunch.Ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.getmelunch.Di.Place.PlacesService;
import com.example.getmelunch.Di.Place.RetrofitBuilder;
import com.example.getmelunch.Models.Places.NearbySearchResponse;
import com.example.getmelunch.Models.Places.Restaurant;
import com.example.getmelunch.R;
import com.example.getmelunch.Utils.OnItemClickListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment implements OnItemClickListener<Restaurant> {

    private List<Restaurant> recyclerViewRestaurants;
    private AutocompleteSessionToken token;
    private RestaurantAdapter adapter;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private RetrofitBuilder retrofitBuilder;
    private String radius = "" + 1000;
    PlacesService placesService;

    public ListFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerViewRestaurants = new ArrayList<>();

        Places.initialize(getContext(), getString(R.string.google_maps_key));
        placesService = new RetrofitBuilder().getRetrofitApi();
        token = AutocompleteSessionToken.newInstance();

        setUpLocationUpdate();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpLocationUpdate() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("TAG", "onLocationResult: " + location.getLatitude() + ","
                            + location.getLongitude());

                }
                super.onLocationResult(locationResult);
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                Looper.getMainLooper()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "startLocationUpdate: success");
            } else {
                Log.d("TAG", "startLocationUpdate: failed");
            }
        });
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location ->
                currentLocation = location);
        if (currentLocation == null) {
        }
        getPlaces("restaurant");
    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d("TAG", "stopLocationUpdate: success");
    }

    private void getPlaces(String placeName) {
        if ((ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) && currentLocation != null) {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                    + "&radius=" + radius + "&type=" + placeName + "&key=" + getString(R.string.google_maps_key);

            Call<NearbySearchResponse> call = placesService.getNearbyPlaces(url);
            call.enqueue(new Callback<NearbySearchResponse>() {
                @Override
                public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {
                    System.out.println("///" + response.body().getResults().size());
                    if (response.isSuccessful()) {
                        if (response.body().getResults().size() > 0) {
                            adapter.updateRestaurantList(response.body().getResults(), currentLocation, ListFragment.this);
                        } else {
                            Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<NearbySearchResponse> call, Throwable t) {
                    Log.d("TAG", "///: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onItemClicked(Restaurant restaurant) {
        Intent intent = new Intent(requireActivity(), DetailRestaurant.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationProviderClient != null) {
            stopLocationUpdate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null) {
            startLocationUpdate();
        }
    }

}