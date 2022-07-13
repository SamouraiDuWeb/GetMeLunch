package com.example.getmelunch.Ui;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Models.Places.Restaurant;
import com.example.getmelunch.R;
import com.example.getmelunch.Utils.OnItemClickListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    List<Restaurant> restaurants;
    OnItemClickListener<Restaurant> listener;
    Location currentLocation;
    Context context;
    final UserHelper userHelper = UserHelper.getInstance();

    public void updateRestaurantList(List<Restaurant> restaurants, Location currentLocation,
                                     OnItemClickListener<Restaurant> listener) {
        this.restaurants = restaurants;
        this.currentLocation = currentLocation;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView restaurantRating, restaurantImage, restaurantWorkmatesIcon;
        TextView restaurantName, restaurantAddress, restaurantDistance, restaurantOpen, restaurantWorkmates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> listener.onItemClicked(restaurants.get(getAdapterPosition())));
        }



        public void bindView(Restaurant restaurant) {

            restaurantName = itemView.findViewById(R.id.item_restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.item_restaurant_address);
            restaurantDistance = itemView.findViewById(R.id.item_distance);
            restaurantOpen = itemView.findViewById(R.id.item_opening_hours);
            restaurantWorkmates = itemView.findViewById(R.id.item_nb_workmates_counter);
            restaurantWorkmatesIcon = itemView.findViewById(R.id.item_nb_workmates_icon);
            restaurantImage = itemView.findViewById(R.id.item_restaurant_photo);
            restaurantRating = itemView.findViewById(R.id.item_rating);

            restaurantName.setText(restaurant.getName());
            restaurantAddress.setText(restaurant.getAddress());
            getPhoto(restaurant);
            getRating(restaurant);
            getDistance(restaurant);
            getOpen(restaurant);
            getWorkmates(restaurant);
        }

        private void getWorkmates(Restaurant restaurant) {
            final Integer[] workmateNumber = {0};
            userHelper.getUserCollection().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String placeId = documentSnapshot.getString("lunchSpotId");
                        if (placeId != null) {
                            if (placeId.equals(restaurant.getPlaceId())) {
                                workmateNumber[0]++;
                                if (workmateNumber[0] > 0) {
                                    restaurantWorkmatesIcon.setVisibility(View.VISIBLE);
                                    restaurantWorkmates.setText(context.getString(
                                            R.string.workmate_counter, workmateNumber[0]));
                                }
                            }
                        }
                    }
                }
            });
        }

        private void getOpen(Restaurant restaurant) {
            if (restaurant.getOpeningHours() != null) {
                if (restaurant.getOpeningHours().getOpenNow().toString().equals("true")) {
                    restaurantOpen.setText(R.string.open);
                    restaurantOpen.setTextColor(context.getResources().getColor(R.color.green));
                } else {
                    restaurantOpen.setText(R.string.closed);
                    restaurantOpen.setTextColor(context.getResources().getColor(R.color.red_dark));
                }
            } else {
                restaurantOpen.setText(R.string.no_opening_hours);
                restaurantOpen.setTextColor(context.getResources().getColor(R.color.black));
            }
        }

        private void getDistance(Restaurant restaurant) {
            Location endPoint = new Location("restaurantLocation");
            endPoint.setLatitude(restaurant.getGeometry().getLocation().getLatitude());
            endPoint.setLongitude(restaurant.getGeometry().getLocation().getLongitude());
            long distance = (long) currentLocation.distanceTo(endPoint);
            restaurantDistance.setText(String.format("%s m", distance));
        }

        private void getRating(Restaurant restaurant) {
            if (restaurant.getRating() != 0) {
                if (restaurant.getRating() >= 4)
                    Picasso.get()
                            .load(R.drawable.star_three)
                            .fit()
                            .centerCrop()
                            .into(restaurantImage);
                if (restaurant.getRating() < 4 && restaurant.getRating() >= 3)
                    restaurantRating.setImageResource(R.drawable.star_two);
                if (restaurant.getRating() < 3)
                    restaurantRating.setImageResource(R.drawable.star_one);
            }
        }

        private void getPhoto(Restaurant restaurant) {
            if (restaurant.getPhotos() != null && restaurant.getPhotos().size() > 0) {
                Picasso.get()
                        .load(restaurant.getPhotos().get(0).getPhotoUrl())
                        .fit()
                        .centerCrop()
                        .into(restaurantImage);
            } else {
                Picasso.get()
                        .load(R.drawable.image_not_available)
                        .fit()
                        .centerCrop()
                        .into(restaurantImage);
            }
        }
    }
}
