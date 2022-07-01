package com.example.getmelunch;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.getmelunch.Di.Place.PlacesService;
import com.example.getmelunch.Di.Place.RetrofitBuilder;
import com.example.getmelunch.Models.Places.NearbySearchResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

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
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FirebaseAuth firebaseAuth;
    Context context;
    private Marker currentMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        context = this.getContext();
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    @SuppressLint("MissingPermission")
    private void setUpMap() {

        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        setUpLocation();
    }

    private void setUpLocation() {
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
        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {

            currentLocation = location;
            if (currentLocation != null) {
                moveCameraToLocation(currentLocation);
                initRetrofitPlaces("restaurant");
            }
            System.out.println("/// Current Location: " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
        }).addOnFailureListener(e -> {
            System.out.println("/// Current Location: " + e.getMessage());
        });

    }

    private void moveCameraToLocation(Location currentLocation) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(
                currentLocation.getLatitude(), currentLocation.getLongitude()), 17);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("youu");

//        if (currentMarker != null) {
//            currentMarker.remove();
//        }
        mMap.addMarker(markerOptions);

//        currentMarker = mMap.addMarker(markerOptions);
//        Objects.requireNonNull(currentMarker).setTag(703);
        mMap.animateCamera(cameraUpdate);


    }

    private void initRetrofitPlaces(String type) {
        //https://github.com/GabrielAranias/Go4Lunch_v2/blob/master/app/src/main/java/com/gabriel/aranias/go4lunch_v2/service/place/RetrofitApi.java

        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                + "&radius=" + PROXIMITY_RADIUS + "&type=" + type + "&key=" + context.getString(R.string.google_maps_key);
        PlacesService retrofitApi = RetrofitBuilder.getRetrofitApi();
        retrofitApi.getNearbyPlaces(url).enqueue(new Callback<NearbySearchResponse>() {

            @Override
            public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {
                Gson gson = new Gson();
                String res = gson.toJson(response.body());
                Log.d(TAG, "///onResponse: " + res);
                if (response.errorBody() == null) {
                    if (response.body() != null) {
                        if (response.body().getResults() != null &&
                                response.body().getResults().size() > 0) {
                            mMap.clear();
                            for (int i = 0; i < response.body().getResults().size(); i++) {
                                String name = response.body().getResults().get(i).getName();
                                String vicinity = response.body().getResults().get(i).getVicinity();
                                double lat = response.body().getResults().get(i).getGeometry().getLocation().getLatitude();
                                double lng = response.body().getResults().get(i).getGeometry().getLocation().getLongitude();
                                LatLng latLng = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name + " : " + vicinity));
                            }
                        } else {
                            mMap.clear();
                            PROXIMITY_RADIUS += 1000;
                            initRetrofitPlaces(type);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NearbySearchResponse> call, Throwable t) {

            }
        });
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

