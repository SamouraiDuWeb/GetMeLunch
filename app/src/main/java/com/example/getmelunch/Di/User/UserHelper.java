package com.example.getmelunch.Di.User;

import com.google.firebase.firestore.CollectionReference;

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
}
