package com.example.getmelunch.Ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.getmelunch.Di.Place.PlacesService;
import com.example.getmelunch.Di.Place.RetrofitBuilder;
import com.example.getmelunch.Models.Places.Geometry;
import com.example.getmelunch.Models.Places.NearbySearchResponse;
import com.example.getmelunch.Models.Places.PlaceLocation;
import com.example.getmelunch.Models.Places.Restaurant;
import com.example.getmelunch.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = MapsFragment.class.getSimpleName();
    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private double latitude, longitude;
    String url;
    private int PROXIMITY_RADIUS = 10000;
    private Context context;
    private Place currentPlace;
    private Marker currentMarker;
    private Restaurant currentRestaurant;
    private String searchUrl;

    private PlacesService service;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        context = this.getContext();

        Places.initialize(context.getApplicationContext(), String.valueOf(R.string.google_maps_key));
        service = RetrofitBuilder.getRetrofitApi();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
        autoCompleteSearch();
        mMap.setOnInfoWindowClickListener(currentMarker ->
                getDetailRestaurant((Restaurant) currentMarker.getTag()));
    }

    private void autoCompleteSearch() {
        Places.initialize(context.getApplicationContext(), "AIzaSyBlkyb-l3-n09s91kve6fhDUSJc5mCL7jk");
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        System.out.println("/// autocompletefragment : " + autocompleteFragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.TYPES));
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "onError: " + status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                searchUrl = getUrl(place.getLatLng().latitude, place.getLatLng().longitude, place.getName());
                Call<NearbySearchResponse> call = service.getNearbySearch(searchUrl);
                call.enqueue(new Callback<NearbySearchResponse>() {
                    @Override
                    public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {
                        if (response.isSuccessful()) {
                            NearbySearchResponse nearbySearchResponse = response.body();
                            if (nearbySearchResponse.getResults().size() > 0) {
                                currentRestaurant = nearbySearchResponse.getResults().get(0);
                                currentMarker.setTag(currentRestaurant);
                                currentMarker.showInfoWindow();
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(currentRestaurant.
                                                getGeometry().getLocation().getLat(),
                                                currentRestaurant.getGeometry().getLocation().getLng()))
                                        .title(currentRestaurant.getName()))
                                        .setTag(currentRestaurant);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentRestaurant.getGeometry().toLatLng(), 15));
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<NearbySearchResponse> call, Throwable t) {
                        Log.i(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void getNearbyPlacesData(String url) {

        Call<NearbySearchResponse> call = service.getNearbyPlaces(url);
        System.out.println("/// " + call.request().url());

        call.enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().getResults().size(); i++) {

                        LatLng latLng = new LatLng(response.body().getResults().get(i).getGeometry().getLocation().getLat(),
                                response.body().getResults().get(i).getGeometry().getLocation().getLng());

                        Restaurant currentPlace = response.body().getResults().get(i);
                        String name = currentPlace.getName();
                        String vicinity = currentPlace.getVicinity();

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .snippet(vicinity);

                        Marker mapMarker = mMap.addMarker(markerOptions);
                        if (mapMarker != null) {
                            mapMarker.showInfoWindow();
                            mapMarker.setTag(currentPlace);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NearbySearchResponse> call, Throwable t) {
                Log.i(TAG, "///: " + t.getMessage());
            }
        });
    }

    private String getUrl(double latitude, double longitude, String restaurant) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + restaurant);
        googlePlacesUrl.append("&key=" + "AIzaSyBlkyb-l3-n09s91kve6fhDUSJc5mCL7jk");
        return googlePlacesUrl.toString();
    }

    @SuppressLint("MissingPermission")
    private void setUpMap() {

        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        setUpLocation();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void getDetailRestaurant(Restaurant restaurant) {
        Intent intent = new Intent(getContext(), DetailRestaurant.class);
        intent.putExtra("restaurant", (Serializable) restaurant);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void setUpLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    System.out.println("///: " + location.getLatitude() + " " + location.getLongitude());
                    currentLocation = location;
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                    url = getUrl(latitude, longitude, "restaurant");
                    mMap.clear();
                    currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here"));
                    moveCameraToLocation(currentLocation);
                    getNearbyPlacesData(url);
                }
            }
        });
    }

    private void moveCameraToLocation(Location currentLocation) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(
                currentLocation.getLatitude(), currentLocation.getLongitude()), 17);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("you are here");

        mMap.addMarker(markerOptions);

        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

