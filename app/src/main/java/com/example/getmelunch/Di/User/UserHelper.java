package com.example.getmelunch.Di.User;

import android.content.Context;

import com.example.getmelunch.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import java.util.Objects;

public class UserHelper {
    private static volatile UserHelper instance;
    private final UserRepository userRepository;

    private UserHelper() {
        userRepository = UserRepository.getInstance();
    }

    public static UserHelper getInstance() {
        UserHelper result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserHelper();
            }
            return instance;
        }
    }

    public CollectionReference getUserCollection() {
        return userRepository.getUserCollection();
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLoggedIn() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        // Delete user account from auth
        return userRepository.deleteUser(context).addOnCompleteListener(task -> {
            // Once done, delete user data from Firestore
            userRepository.deleteUserFromFirestore();
        });
    }

    public void createUser() {
        userRepository.createUser();
    }

    public Task<User> getUserData() {
        // Get user from Firestore x cast it to User object
        return Objects.requireNonNull(userRepository.getUserData()).continueWith(task ->
                task.getResult().toObject(User.class));
    }

    public Task<Void> updateUsername(String username) {
        return userRepository.updateUsername(username);
    }

    public void updateLunchSpotId(String lunchSpotId) {
        userRepository.updateLunchSpotId(lunchSpotId);
    }

    public void updateLunchSpotName(String lunchSpotName) {
        userRepository.updateLunchSpotName(lunchSpotName);
    }

    public void updateLunchSpotAddress(String lunchSpotAddress) {
        userRepository.updateLunchSpotAddress(lunchSpotAddress);
    }
}
