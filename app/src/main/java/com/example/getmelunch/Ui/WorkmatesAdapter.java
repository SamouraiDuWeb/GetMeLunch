package com.example.getmelunch.Ui;

import static androidx.recyclerview.widget.RecyclerView.*;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Models.User;
import com.example.getmelunch.R;
import com.example.getmelunch.Utils.OnItemClickListener;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder>{

    List<User> workmates;
    ListView listView;
    Context context;
    final UserHelper userHelper = UserHelper.getInstance();
    OnItemClickListener<User> listener;
    Location currentLocation;

    public WorkmatesAdapter updateWorkmatesList(List<User> workmates, Location currentLocation,
                                                OnItemClickListener<User> listener) {
        this.workmates = workmates;
        this.currentLocation = currentLocation;
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.workmates_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(workmates.get(getAdapterPosition()));
                }
            });
        }
    }
}
