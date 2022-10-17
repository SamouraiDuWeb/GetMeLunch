package com.example.getmelunch.Ui.Workmates;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Models.User;
import com.example.getmelunch.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    ArrayList<User> users;
    Context context;
    private final UserHelper userHelper = UserHelper.getInstance();

    public WorkmatesAdapter(Context inputContext, ArrayList<User> u) {

        context = inputContext;
        users = u;
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder holder, int index) {
        User user = users.get(index);

        holder.tvName.setText(user.getName());
        if (user.getPictureUrl() != null) {
            Picasso.get()
                    .load(user.getPictureUrl())
                    .into(holder.pictureView);
        } else {
            Picasso.get()
                    .load(R.drawable.ic_baseline_person_24)
                    .into(holder.pictureView);
        }

        userHelper.getUserCollection()
                .whereEqualTo("uid", user.getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("TAG", "Listen failed", error);
                        return;
                    }
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                        User usertemp = doc.toObject(User.class);
                        if (usertemp.getLunchSpotId() != null) {
                            // Workmate has chosen a lunch spot
                            holder.tvName.setText(context.getString(
                                    R.string.decided, usertemp.getName()));
                            holder.tvLunchspotName.setVisibility(View.VISIBLE);
                            holder.tvLunchspotName.setText(context.getString(
                                    R.string.decided_lunch_spot, usertemp.getLunchSpotName()));
                        } else {
                            // Workmate hasn't decided yet
                            holder.tvLunchspotName.setText(context.getString(
                                    R.string.not_decided, usertemp.getName()));
                            holder.tvLunchspotName.setTextColor(context.getResources()
                                    .getColor(com.google.android.libraries.places.R.color.quantum_grey));
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvLunchspotName;
        ImageView pictureView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

             tvName = itemView.findViewById(R.id.item_workmate_lunch_spot);
             pictureView = itemView.findViewById(R.id.item_workmate_avatar);
             tvLunchspotName = itemView.findViewById(R.id.item_workmate_lunch_spot_name);

        }
    }
}
