package com.example.getmelunch.Ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmelunch.Models.User;
import com.example.getmelunch.databinding.WorkmateItemBinding;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private final ArrayList<User> workmates;
    private Context context;

    public DetailAdapter(Context context, ArrayList<User> workmates) {
        this.context = context;
        this.workmates = workmates;
    }

    @NonNull
    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        WorkmateItemBinding binding = WorkmateItemBinding.inflate(inflater, parent, false);
        context = parent.getContext();

        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull DetailAdapter.ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final WorkmateItemBinding binding;

        public ViewHolder(@NonNull WorkmateItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }
}
