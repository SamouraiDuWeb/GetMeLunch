package com.example.getmelunch.Ui.List;

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
import com.example.getmelunch.databinding.RestaurantItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    List<Restaurant> restaurants;
    OnItemClickListener<Restaurant> listener;
    Location currentLocation;
    Context context;
    final UserHelper userHelper = UserHelper.getInstance();

    public RestaurantAdapter updateRestaurantList(List<Restaurant> restaurants, Location currentLocation,
                                     OnItemClickListener<Restaurant> listener) {
        this.restaurants = restaurants;
        this.currentLocation = currentLocation;
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RestaurantItemBinding binding = RestaurantItemBinding.inflate(inflater, parent, false);
        context = parent.getContext();

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {
        holder.bindView(restaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RestaurantItemBinding binding;

        public ViewHolder(@NonNull RestaurantItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;

            itemView.setOnClickListener(v -> listener.onItemClicked(restaurants.get(getAdapterPosition())));
        }



        public void bindView(Restaurant restaurant) { ;

            binding.itemRestaurantName.setText(restaurant.getName());
            binding.itemRestaurantAddress.setText(restaurant.getVicinity());
            getPhoto(restaurant);
            getRating(restaurant);
            getDistance(restaurant);
//            getOpen(restaurant);
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
                                    binding.itemNbWorkmatesIcon.setVisibility(View.VISIBLE);
                                    binding.itemNbWorkmatesCounter.setText(context.getString(
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
                    binding.itemOpeningHours.setText(R.string.open);
                    binding.itemOpeningHours.setTextColor(context.getResources().getColor(R.color.green));
                } else {
                    binding.itemOpeningHours.setText(R.string.closed);
                    binding.itemOpeningHours.setTextColor(context.getResources().getColor(R.color.red_dark));
                }
            } else {
                binding.itemOpeningHours.setText(R.string.no_opening_hours);
                binding.itemOpeningHours.setTextColor(context.getResources().getColor(R.color.black));
            }
        }

        private void getDistance(Restaurant restaurant) {
            Location endPoint = new Location("restaurantLocation");
            endPoint.setLatitude(restaurant.getGeometry().getLocation().getLat());
            endPoint.setLongitude(restaurant.getGeometry().getLocation().getLng());
            long distance = (long) currentLocation.distanceTo(endPoint);
            binding.itemDistance.setText(String.format("%s m", distance));
        }

        private void getRating(Restaurant restaurant) {
            if (restaurant.getRating() != 0) {
                if (restaurant.getRating() >= 4)
                    Picasso.get()
                            .load(R.drawable.star_three)
                            .fit()
                            .centerCrop()
                            .into(binding.itemRestaurantPhoto);
                if (restaurant.getRating() < 4 && restaurant.getRating() >= 3)
                    binding.itemRating.setImageResource(R.drawable.star_two);
                if (restaurant.getRating() < 3)
                    binding.itemRating.setImageResource(R.drawable.star_one);
            }
        }

        private void getPhoto(Restaurant restaurant) {
            if (restaurant.getPhotos() != null && restaurant.getPhotos().size() > 0) {
                Picasso.get()
                        .load(restaurant.getPhotos().get(0).getPhotoUrl())
                        .fit()
                        .centerCrop()
                        .into(binding.itemRestaurantPhoto);
            } else {
                Picasso.get()
                        .load(R.drawable.image_not_available)
                        .fit()
                        .centerCrop()
                        .into(binding.itemRestaurantPhoto);
            }
        }
    }
}
