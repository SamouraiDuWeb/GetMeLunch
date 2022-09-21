package com.example.getmelunch.Ui.Workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.getmelunch.Models.User;
import com.example.getmelunch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesLvAdapter extends ArrayAdapter<User> {

    Context mContext;

    public WorkmatesLvAdapter(@NonNull Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name = getItem(position).getName();
        String lunchspotName = getItem(position).getLunchSpotName();
        String pictureUrl = getItem(position).getPictureUrl();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.workmate_item, parent, false);

        TextView tvName = convertView.findViewById(R.id.item_workmate_lunch_spot);
        TextView tvLunchspotName = convertView.findViewById(R.id.item_workmate_lunch_spot_name);
        ImageView ivPictureUrl = convertView.findViewById(R.id.item_workmate_avatar);

        tvName.setText(name);
        tvLunchspotName.setText(lunchspotName);
        Picasso.get().load(pictureUrl).into(ivPictureUrl);

        return convertView;
    }
}
