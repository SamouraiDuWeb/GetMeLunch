package com.example.getmelunch.Ui.Workmates;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.getmelunch.Di.Place.PlaceHelper;
import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Models.User;
import com.example.getmelunch.databinding.FragmentWorkmatesBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class WorkmatesFragment extends Fragment {

    ArrayList<User> users;
    private FragmentWorkmatesBinding binding;
    com.example.getmelunch.Ui.Workmates.WorkmatesAdapter adapter;
    RecyclerView listView;
    private final UserHelper userHelper = UserHelper.getInstance();
    private final PlaceHelper placeHelper = PlaceHelper.getInstance();
    private ProgressDialog progressDialog;
    ArrayList<User> user2 = new ArrayList<User>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("fetching data...");
        progressDialog.show();

        initData();

        EventChangeListener();

        System.out.println("/// " + users);


        return binding.getRoot();
    }

    private void initData() {

        listView = binding.lvRestaurants;
        RecyclerView.LayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(llm);
        listView.setHasFixedSize(true);
        users = new ArrayList<>();
        adapter = new WorkmatesAdapter(getContext(), users);
        listView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void EventChangeListener() {

        userHelper.getUserCollection()
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
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
                        System.out.println("/// " + user.getName());
                    }
                    System.out.println("/// " + users);
                    adapter.notifyDataSetChanged();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
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