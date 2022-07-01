package com.example.getmelunch.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.getmelunch.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    BottomNavigationView navView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initBottomNavigation();

        checkCurrentUser();
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

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Welcome " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }
}
