package com.example.getmelunch.Ui.Workmates;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Models.User;
import com.example.getmelunch.R;
import com.example.getmelunch.Utils.Constants;
import com.example.getmelunch.Utils.OnItemClickListener;
import com.example.getmelunch.databinding.WorkmateItemBinding;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder>{

    List<User> workmates;
    Context context;
    final UserHelper userHelper = UserHelper.getInstance();
    OnItemClickListener<User> listener;
    Location currentLocation;

    public WorkmatesAdapter(Context context, List<User> workmates, OnItemClickListener<User> listener) {
        this.context = context;
        this.workmates = workmates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        WorkmateItemBinding binding = WorkmateItemBinding.inflate(inflater, parent, false);
        context = parent.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder holder, int position) {
        holder.bindView(workmates.get(position));
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final WorkmateItemBinding binding;

        public ViewHolder(@NonNull WorkmateItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;

            itemBinding.workmateItem.setOnClickListener(v -> listener.onItemClicked(workmates.get(getAdapterPosition())));
        }

        public void bindView(User workmate) {
            // Photo
            getPhoto(workmate);
            // Text
            getLunchSpotText(workmate);
        }

        private void getLunchSpotText(User workmate) {
            userHelper.getUserCollection()
                    .whereEqualTo("uid", workmate.getUid())
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.w("TAG", "Listen failed", error);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                            User user = doc.toObject(User.class);
                            if (user.getLunchSpotId() != null) {
                                binding.itemWorkmateLunchSpot.setText(context.getString(
                                        R.string.decided, user.getName()));
                                binding.itemWorkmateLunchSpotName.setVisibility(View.VISIBLE);
                                binding.itemWorkmateLunchSpotName.setText(context.getString(
                                        R.string.decided_lunch_spot, user.getLunchSpotName()));
                            } else {
                                binding.itemWorkmateLunchSpot.setText(context.getString(
                                        R.string.not_decided, user.getName()));
                                binding.itemWorkmateLunchSpot.setTextColor(context.getResources()
                                        .getColor(com.google.android.libraries.places.R.color.quantum_grey300));
                            }
                        }
                    });
        }

        private void getPhoto(User workmate) {
            if (workmate.getPictureUrl() != null) {
                Picasso.get()
                        .load(workmate.getPictureUrl())
                        .into(binding.itemWorkmateAvatar);
            } else {
                Picasso.get()
                        .load(R.drawable.ic_baseline_person_24)
                        .into(binding.itemWorkmateAvatar);
            }
        }
    }
}
