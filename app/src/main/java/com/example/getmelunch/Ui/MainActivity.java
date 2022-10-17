package com.example.getmelunch.Ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.getmelunch.Di.User.UserHelper;
import com.example.getmelunch.Di.User.UserRepository;
import com.example.getmelunch.Models.User;
import com.example.getmelunch.R;
import com.example.getmelunch.Ui.List.ListFragment;
import com.example.getmelunch.Ui.Map.MapsFragment;
import com.example.getmelunch.Ui.Workmates.WorkmatesFragment;
import com.example.getmelunch.Utils.Constants;
import com.example.getmelunch.Utils.PermissionUtils;
import com.example.getmelunch.databinding.ActivityMainBinding;
import com.example.getmelunch.databinding.HeaderNavigationDrawerBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    BottomNavigationView navView;
    private HeaderNavigationDrawerBinding headerBinding;
    private final UserHelper userHelper = UserHelper.getInstance();
    private boolean permissionDenied;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        checkCurrentUser();

        enableDeviceLocation();
        getSampleData();
        initBottomNavigation();
        initNavigationDrawer();
//        createUsersTest();

    }

    // set profile pic
    private void createUsersTest() {
        ArrayList<User> l = new ArrayList<>();
        l.add(new User("1", "Jonathan", ""));
        l.add(new User("2", "Mélissa", ""));
        l.add(new User("3", "Carol", ""));
        l.add(new User("4", "Adam", ""));

        db = FirebaseFirestore.getInstance();

        for (int i = 0; i < l.size(); i++) {
            db.collection("users").add(l.get(i)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    private void getSampleData() {
        userHelper.getUserCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String name = documentSnapshot.getString("name");
                    System.out.println("/// " + name);
                }
            } else {
                System.out.println("/// " + task.getException());
            }
        });
    }

    private void initView() {
        View headerView = binding.mainNavigationDrawer.getHeaderView(0);
        headerBinding = HeaderNavigationDrawerBinding.bind(headerView);

        setSupportActionBar(binding.mainToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.i_m_hungry));
    }

    private void initMapFragment() {
        MapsFragment mapsFragment = new MapsFragment();
        binding.mainToolbar.setTitle(R.string.i_m_hungry);
        showFragment(mapsFragment);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void initNavigationDrawer() {
        binding.mainToolbar.setNavigationOnClickListener(v -> binding.drawerLayout.open());
        try {
            getCurrentUserData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        binding.mainNavigationDrawer.setNavigationItemSelectedListener(item -> {
            final int yourLunchId = R.id.nd_your_lunch;
            final int settingId = R.id.nd_settings;
            final int logOutId = R.id.nd_logout;
            switch (item.getItemId()) {
                case yourLunchId:
//                    getLunchDetails();
                    break;
                case settingId:
                    startActivity(new Intent(this, SettingActivity.class));
                    break;
                case logOutId:
                    logOut();
                    break;
            }
            binding.drawerLayout.closeDrawers();
            return true;
        });
    }

    private void logOut() {
        userHelper.signOut(this).addOnCompleteListener(task -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void getCurrentUserData() {
        FirebaseUser user = userHelper.getCurrentUser();
        if (user.getPhotoUrl() != null) {
            setProfilePicture(user.getPhotoUrl());
        } else {
            setDefaultProfilePicture();
        }
        setUserTextInfo(user);
       // Update username if changed via settings
//        if (SettingActivity.nameIsUpdated()) {
//            updateName();
//        }
    }

    private void setProfilePicture(Uri photoUrl) {

        try {
            Picasso.get()
                    .load(photoUrl)
                    .fit()
                    .centerCrop()
                    .into(headerBinding.headerAvatar);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void setDefaultProfilePicture() {

        Picasso.get()
                .load(R.drawable.ic_baseline_person_24)
                .fit()
                .centerCrop()
                .into(headerBinding.headerAvatar);
    }

    private void setUserTextInfo(FirebaseUser user) {
        String username = TextUtils.isEmpty(user.getDisplayName()) ?
                "John Doe" : user.getDisplayName();
        String email = TextUtils.isEmpty(user.getEmail()) ?
                "john.doe@example.com" : user.getEmail();

        headerBinding.headerUserName.setText(username);
        headerBinding.headerUserEmail.setText(email);
    }

    private void initBottomNavigation() {

        navView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragment()).commit();

        navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.map_fragment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragment()).commit();
                    return true;
                case R.id.list_fragment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListFragment()).commit();
                    return true;
                case R.id.workmates_fragment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkmatesFragment()).commit();
                    return true;
            }
            return false;
        });
    }

    private void checkCurrentUser() {

        if (!userHelper.isCurrentUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Welcome " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void enableDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initMapFragment();
        } else {
            PermissionUtils.requestPermission(this, Constants.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
//            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
//            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableDeviceLocation();
        } else {
            permissionDenied = true;
            System.out.println("///result " + grantResults[0]);
        }
    }

//    private ActivityResultLauncher<String> requestPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // Permission is granted. Continue the action or workflow in your
//                    // app.
//                    enableDeviceLocation();
//                    return;
//                } else {
//                    permissionDenied = true;
//                    // Explain to the user that the feature is unavailable because the
//                    // features requires a permission that the user has denied. At the
//                    // same time, respect the user's decision. Don't link to system
//                    // settings in an effort to convince the user to change their
//                    // decision.
//                }
//            });

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog
            showMissingPermissionError();
            permissionDenied = false;
        }
    }
    
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}
