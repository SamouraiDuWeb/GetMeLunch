package com.example.getmelunch.Ui.Workmates;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.getmelunch.Di.Place.PlaceHelper;
import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Models.User;
import com.example.getmelunch.R;
import com.example.getmelunch.Utils.OnItemClickListener;
import com.example.getmelunch.databinding.FragmentWorkmatesBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkmatesFragment extends Fragment {

    ArrayList<User> users ;
    private FragmentWorkmatesBinding binding;
    OnItemClickListener<User> listener;
    WorkmatesLvAdapter adapter;
    private final UserHelper userHelper = UserHelper.getInstance();
    private final PlaceHelper placeHelper = PlaceHelper.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        users = new ArrayList<>();
        adapter = new WorkmatesLvAdapter(this.requireContext(), R.id.lv_restaurants, users);
        ListView listView = binding.lvRestaurants;
        listView.setAdapter(adapter);

        EventChangeListener();

        System.out.println("/// " + users);


        return inflater.inflate(R.layout.fragment_workmates, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void EventChangeListener() {
        userHelper.getUserCollection()
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("TAG", "Firestore error: " + error.getMessage());
                        return;
                    }
                    users.clear();
                    for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                        User user = dc.getDocument().toObject(User.class);
                        switch (dc.getType()) {
                            case ADDED:
                                users.add(user);
//                                if (!user.getUid().equals(userHelper.getCurrentUser().getUid())) {
//                                    users.add(user);
//                                }
                                break;
                            case REMOVED:
                                users.remove(user);
                                break;
                            case MODIFIED:
                                users.remove(user);
                                users.add(user);
                                break;
                        }
                        adapter.notifyDataSetChanged();

                    }
                    System.out.println("/// " + users);
                });
    }

//    private void initData() {
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//
//        users = new ArrayList<>();
//
//        binding.lvRestaurants.setAdapter(adapter);
//    }

//    private void EventChangeListener() {
//        userHelper.getUserCollection()
//                .orderBy("name", Query.Direction.ASCENDING)
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) {
//                        Log.e("TAG", "Firestore error: " + error.getMessage());
//                        return;
//                    }
//                    users.clear();
//                    for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
//                        User user = dc.getDocument().toObject(User.class);
//                        switch (dc.getType()) {
//                            case ADDED:
//                                users.add(user);
////                                if (!user.getUid().equals(userHelper.getCurrentUser().getUid())) {
////                                    users.add(user);
////                                }
//                                break;
//                            case REMOVED:
//                                users.remove(user);
//                                break;
//                            case MODIFIED:
//                                users.remove(user);
//                                users.add(user);
//                                break;
//                        }
//                        System.out.println("/// " + users.size());
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//    }
}