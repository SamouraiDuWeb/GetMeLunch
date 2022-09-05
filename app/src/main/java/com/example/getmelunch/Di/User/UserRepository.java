package com.example.getmelunch.Di.User;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.getmelunch.Models.User;
import com.example.getmelunch.Utils.Constants;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public final class UserRepository {

    private static volatile UserRepository instance;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    // Get collection reference
    public CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    // Create user in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String pictureUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, pictureUrl, null, null, null);

            Task<DocumentSnapshot> userData = getUserData();

            userData.addOnSuccessListener(
                    documentSnapshot -> {
                        if (documentSnapshot!= null) {
                            userToCreate.setLunchSpotId((String) documentSnapshot
                                    .get("lunchSpotId"));
                        }
                    });
            this.getUserCollection().document(uid).set(userToCreate);

        }
    }

    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUserCollection().document(uid).get();
        } else {
            return null;
        }
    }

    private String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // Update username
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUserCollection().document(uid).update("username", username);
        } else {
            return null;
        }
    }

    // Delete user from Firestore
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            this.getUserCollection().document(uid).delete();
        }
    }

    public void updateLunchSpotId(String lunchSpotId) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            this.getUserCollection().document(uid)
                    .update("lunchSpotId", lunchSpotId);
        }
    }

    public void updateLunchSpotName(String lunchSpotName) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            this.getUserCollection().document(uid)
                    .update("lunchSpotName", lunchSpotName);
        }
    }

    public void updateLunchSpotAddress(String lunchSpotAddress) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            this.getUserCollection().document(uid)
                    .update("lunchSpotAddress", lunchSpotAddress);
        }
    }
}
