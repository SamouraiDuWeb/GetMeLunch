package com.example.getmelunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextSelection;
import android.widget.Toast;

import com.example.getmelunch.Di.PlacesService;
import com.example.getmelunch.Di.RetrofitBuilder;
import com.example.getmelunch.Models.Restaurant;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private double latitude, longitude;
    String url;
    private int PROXIMITY_RADIUS = 10000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

//    private void initPlaces() throws IOException, JSONException {
//        if (!Places.isInitialized()) {
//            Places.initialize(getContext(), getString(R.string.google_maps_key));
//        }
//        PlacesClient placesClient = Places.createClient(getContext());
//        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteSupportFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
//        autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(
//                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
//        autocompleteSupportFragment.setCountries("FR");
//        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
//        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onError(@NonNull Status status) {
//                Log.e(TAG, "onError: " + status.getStatusMessage());
//            }
//
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                Log.d(TAG, "onPlaceSelected: " + place.getName());
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
//            }
//        });
//        //NearbySearch from currentLocation
//        String currentlat = String.valueOf(currentLocation.getLatitude());
//        String currentlng = String.valueOf(currentLocation.getLongitude());
//        System.out.println("///current lat : " + currentlat + "," + currentlng);
//        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentlat + "," + currentlng + "&radius=1500&type=restaurant&key=" + getString(R.string.google_maps_key);
//        System.out.println("///url : " + url);
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        Request request = new Request.Builder()
//                .url(url)
//                .method("GET", null)
//                .build();
//        Response response = client.newCall(request).execute();
//        String jsonData = response.body().string();
//        Log.d(TAG, "initPlaces: " + jsonData);
//        JSONObject jsonObject = new JSONObject(jsonData);
//        JSONArray jsonArray = jsonObject.getJSONArray("results");
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject place = jsonArray.getJSONObject(i);
//            String name = place.getString("name");
//            JSONObject geometry = place.getJSONObject("geometry");
//            JSONObject location = geometry.getJSONObject("location");
//            double lat = location.getDouble("lat");
//            double lng = location.getDouble("lng");
//            LatLng latLng = new LatLng(lat, lng);
//            mMap.addMarker(new MarkerOptions().position(latLng).title(name));
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                    //                        initPlaces();
                    if (!Places.isInitialized()) {
                        Places.initialize(getContext(), getString(R.string.google_maps_key));
                    }
//                    initRetrofitPlaces();
                }
            }
        });
    }

//    private void initRetrofitPlaces() {
//        build_retrofit_and_get_response("restaurant");
//    }
//
//    private void build_retrofit_and_get_response(String type) {
//
//        //https://github.com/GabrielAranias/Go4Lunch_v2/blob/master/app/src/main/java/com/gabriel/aranias/go4lunch_v2/service/place/RetrofitApi.java
//
//        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
//                + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
//                + "&radius=" + PROXIMITY_RADIUS + "&type=" + type + "&key=" + getString(R.string.google_maps_key);
//        PlacesService retrofitApi = RetrofitBuilder.getRetrofitApi();
//        retrofitApi.getNearbyPlaces(url).enqueue(
//                new Callback<PlacesService.PlacesResponse>() {
//                    @Override
//                    public void onResponse(Call<PlacesService.PlacesResponse> call, Response<PlacesService.PlacesResponse> response) {
//                        if (response.isSuccessful()) {
//                            PlacesService.PlacesResponse placesResponse = response.body();
//                            if (placesResponse != null) {
//                                List<PlacesService.Place> places = placesResponse.getPlaces();
//                                for (PlacesService.Place place : places) {
//                                    String name = place.getName();
//                                    double lat = place.getLat();
//                                    double lng = place.getLng();
//                                    LatLng latLng = new LatLng(lat, lng);
//                                    mMap.addMarker(new MarkerOptions().position(latLng).title(name));
//                                }
//                            }
//                        }
//                    }
//                }
//        );

//        call.enqueue(new Callback<Restaurant>() {
//            @Override
//            public void onResponse(Call<Restaurant> call, retrofit2.Response<Restaurant> response) {
//                try {
//                    mMap.clear();
//                    // This loop will go through all the results and add marker on each location.
//                    for (int i = 0; i < response.body().getResults().size(); i++) {
//                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLatitude();
//                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLongitude();
//                        String placeName = response.body().getResults().get(i).getName();
//                        String vicinity = response.body().getResults().get(i).getVicinity();
//                        MarkerOptions markerOptions = new MarkerOptions();
//                        LatLng latLng = new LatLng(lat, lng);
//                        // Position of Marker on Map
//                        markerOptions.position(latLng);
//                        // Adding Title to the Marker
//                        markerOptions.title(placeName + " : " + vicinity);
//                        // Adding Marker to the Camera.
//                        Marker m = mMap.addMarker(markerOptions);
//                        // Adding colour to the marker
//                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        // move map camera
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

