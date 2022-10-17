package com.example.getmelunch.Ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Di.Place.PlaceHelper;
import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Models.Places.Restaurant;
import com.example.getmelunch.Models.User;
import com.example.getmelunch.R;
import com.example.getmelunch.databinding.ActivityDetailRestaurantBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DetailRestaurant extends AppCompatActivity {

    private ActivityDetailRestaurantBinding binding;
    PlacesClient placesClient;
    private final UserHelper userHelper = UserHelper.getInstance();
    private final PlaceHelper placeHelper = PlaceHelper.getInstance();
    private SharedPreferences prefs;
    private DetailAdapter adapter;
    private ArrayList<User> workmates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Places.initialize(getApplicationContext(), "AIzaSyBlkyb-l3-n09s91kve6fhDUSJc5mCL7jk");
        placesClient = Places.createClient(this);
        prefs = getSharedPreferences("sharedPreferences", MODE_PRIVATE);

        getWorkmates();

        //Get the restaurant from the intent
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        if (restaurant != null) {
            bindData(restaurant);
            getDetailsApi(restaurant);
            initLikeBtn(restaurant);
            initLunchSpotFab(restaurant);
            getJoiningWorkmates(restaurant);
        }
    }

    private void getJoiningWorkmates(Restaurant restaurant) {

        if (workmates.size() > 0) {
            workmates.clear();
        }
        userHelper.getUserCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String placeId = documentSnapshot.getString("lunchSpotId");
                    if (placeId != null) {
                        if (placeId.equals(restaurant.getPlaceId())) {
                            User user = documentSnapshot.toObject(User.class);
                            if (!Objects.requireNonNull(user).getUid().equals(
                                    userHelper.getCurrentUser().getUid())) {
                                workmates.add(user);
                            }
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void initLunchSpotFab(Restaurant restaurant) {

        userHelper.getUserData().addOnSuccessListener(user -> {
            String lunchSpotId = user.getLunchSpotId();
            if (lunchSpotId != null) {
                if (lunchSpotId.equals(restaurant.getPlaceId())) {
                    binding.fab.setImageDrawable(ContextCompat.getDrawable(
                            getApplicationContext(), R.drawable.ic_baseline_check_circle_24));
                    binding.fab.getDrawable().setTint(getResources()
                            .getColor(R.color.green));
                }
            }
        });
        setFabListener(restaurant);
    }

    private void setFabListener(Restaurant restaurant) {
        binding.fab.setOnClickListener(view ->
                userHelper.getUserData().addOnSuccessListener(user -> {
                    String lunchSpotId = user.getLunchSpotId();
                    isLunchSpot(lunchSpotId == null ||
                            !lunchSpotId.equals(restaurant.getPlaceId()), restaurant);
                }));
    }

    private void isLunchSpot(boolean b, Restaurant restaurant) {
        int drawable;
        int color;
        String msg;
        if (b) {
            // If user has already chosen a lunch spot beforehand, delete it from Firestore
            deletePreviousLunchSpot(restaurant);
            // Add lunch spot to Firestore 'places' collection
            createPlace(restaurant);
            // Update info in Firestore 'users' collection
            userHelper.updateLunchSpotId(restaurant.getPlaceId());
            userHelper.updateLunchSpotName(restaurant.getName());
            userHelper.updateLunchSpotAddress(restaurant.getVicinity());
            // Save chosen lunch spot to shared prefs
            saveLunchSpot(restaurant);

            drawable = R.drawable.ic_baseline_check_circle_24;
            color = R.color.green;
            msg = getResources().getString(R.string.lunch_spot_add);
        } else {
            // Delete lunch spot from Firestore 'places' collection
            placeHelper.getPlaceCollection().document(restaurant.getPlaceId()).delete();
            // Update info in Firestore 'users' collection
            userHelper.updateLunchSpotId(null);
            userHelper.updateLunchSpotName(null);
            userHelper.updateLunchSpotAddress(null);
            // Clear shared prefs
            removeLunchSpot();

            drawable = R.drawable.ic_baseline_check_circle_outline_24;
            color = com.google.android.libraries.places.R.color.quantum_googred500;
            msg = getResources().getString(R.string.lunch_spot_remove);
        }
        userHelper.getUserData().addOnSuccessListener(user -> {
            binding.fab.setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), drawable));
            binding.fab.getDrawable().setTint(getResources().getColor(color));
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
        }).addOnFailureListener(System.out::println);
    }

    private void removeLunchSpot() {
        prefs.edit().clear().apply();
    }

    private void saveLunchSpot(Restaurant restaurant) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
        Gson gson = new Gson();
        String json = gson.toJson(restaurant);
        editor.putString("savedLunchSpot", json);
        editor.apply();
    }

    private void createPlace(Restaurant restaurant) {
        DocumentReference ref = placeHelper.getPlaceCollection().document();
        ref.set(restaurant);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    Restaurant place = doc.toObject(Restaurant.class);
                    Objects.requireNonNull(place).setDocId(doc.getId());
                    if (place.getPlaceId().equals(restaurant.getPlaceId())) {
                        restaurant.setDocId(doc.getId());
                    }
                    addUserId(restaurant);
                }
            }
        });
    }

    private void addUserId(Restaurant restaurant) {
        placeHelper.getPlaceCollection().document(restaurant.getDocId())
                .update("uid", userHelper.getCurrentUser().getUid())
                .addOnSuccessListener(aVoid ->
                        Log.d("TAG", "DocumentSnapshot successfully updated"))
                .addOnFailureListener(e -> Log.w("TAG", "Error updating document", e));
    }

    private void deletePreviousLunchSpot(Restaurant restaurant) {

        placeHelper.getPlaceCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    if (doc.exists() && doc.contains("uid")) {
                        String userId = doc.getString("uid");
                        if (Objects.requireNonNull(userId).equals(userHelper.getCurrentUser().getUid())) {
                            Restaurant place = doc.toObject(Restaurant.class);
                            if (place != null && !(place.getPlaceId().equals(restaurant.getPlaceId()))) {
                                placeHelper.getPlaceCollection().document(doc.getId()).delete();
                            }
                        }
                    }
                }
            }
        });
    }

    private void initLikeBtn(Restaurant restaurant) {
        userHelper.getUserCollection().document(
                userHelper.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            @SuppressWarnings("unchecked")
            List<String> favRestaurants = (List<String>) task.getResult().get("favoriteRestaurants");
            if (favRestaurants != null) {
                for (String restaurantId : favRestaurants) {
                    if (restaurantId.equals(restaurant.getPlaceId())) {
                        binding.mbRestaurantLike.setText("unlike");
                        binding.mbRestaurantLike.setIcon(ContextCompat.getDrawable
                                (getApplicationContext(), R.drawable.ic_baseline_star_24));
                    }
                }
            }
        });
        setLikeBtnListener(restaurant);
    }

    private void setLikeBtnListener(Restaurant restaurant) {

        binding.mbRestaurantLike.setOnClickListener(view ->
                userHelper.getUserCollection().document(
                        userHelper.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                    @SuppressWarnings("unchecked")
                    List<String> favRestaurants = (List<String>) task.getResult()
                            .get("favoriteRestaurants");
                    likeRestaurant(restaurant);
                    if (favRestaurants != null) {
                        for (String restaurantId : favRestaurants) {
                            if (restaurantId.equals(restaurant.getPlaceId())) {
                                unlikeRestaurant(restaurant);
                            }
                        }
                    }
                }));
    }

    private void unlikeRestaurant(Restaurant restaurant) {
        // Update view
        binding.mbRestaurantLike.setText("Like");
        binding.mbRestaurantLike.setIcon(ContextCompat.getDrawable
                (getApplicationContext(), R.drawable.ic_baseline_star_outline_24));
        // Remove place from user fav list in Firestore
        userHelper.getUserCollection().document(userHelper.getCurrentUser().getUid())
                .update("favoriteRestaurants", FieldValue.arrayRemove(restaurant.getPlaceId()));
        Snackbar.make(binding.getRoot(), R.string.fav_remove, Snackbar.LENGTH_SHORT).show();
    }

    private void likeRestaurant(Restaurant restaurant) {
        // Update view
        binding.mbRestaurantLike.setText("Unlike");
        binding.mbRestaurantLike.setIcon(ContextCompat.getDrawable
                (getApplicationContext(), R.drawable.ic_baseline_star_24));
        // Add place to user fav list in Firestore
        userHelper.getUserCollection().document(userHelper.getCurrentUser().getUid())
                .update("favoriteRestaurants", FieldValue.arrayUnion(restaurant.getPlaceId()));
        Snackbar.make(binding.getRoot(), R.string.fav_add, Snackbar.LENGTH_SHORT).show();
    }

    private void getDetailsApi(Restaurant restaurant) {
        // Define place id
        String placeId = restaurant.getPlaceId();

        // Specify fields to return
        List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
        // Construct request object, passing place id x field array
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i("TAG", "CustomPlace found: " + place.getName());
            // Start intent on call btn click
            if (place.getPhoneNumber() != null) {
                binding.mbRestaurantPhone.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel: " + place.getPhoneNumber()));
                    startActivity(intent);
                });
            } else {
                Snackbar.make(binding.getRoot(), "no phone number", Snackbar.LENGTH_SHORT).show();
            }
            // Start intent on website btn click
            if (place.getWebsiteUri() != null) {
                binding.mbRestaurantWebsite.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, place.getWebsiteUri());
                    startActivity(intent);
                });
            } else {
                Snackbar.make(binding.getRoot(), "no website", Snackbar.LENGTH_SHORT).show();
            }

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("TAG", "CustomPlace not found: " + exception.getMessage());
                int statusCode = apiException.getStatusCode();
                Log.e("TAG", "Error: " + statusCode);
            }
        });
    }

    private void getWorkmates() {
        workmates = new ArrayList<>();
        binding.rvRestaurantParticipating.setHasFixedSize(true);
        adapter = new DetailAdapter(this, workmates);
        binding.rvRestaurantParticipating.setAdapter(adapter);
    }

    private void bindData(Restaurant restaurant) {
        //address & name
        binding.tvRestaurantName.setText(restaurant.getName());
        binding.tvRestaurantAddress.setText(restaurant.getVicinity());
        System.out.println("/// " + restaurant.getVicinity() + ", " + restaurant.getName());

        //image
        Picasso.get()
                .load(restaurant.getPhotos().get(0).getPhotoUrl())
                .fit()
                .centerCrop()
                .into(binding.ivRestaurantImage);
        System.out.println(restaurant.getPhotos().get(0).getPhotoUrl());
    }
}
