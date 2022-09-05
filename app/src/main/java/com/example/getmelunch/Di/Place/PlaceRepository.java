package com.example.getmelunch.Di.Place;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceRepository {

    private static volatile PlaceRepository instance;

    private PlaceRepository() {
    }

    public static PlaceRepository getInstance() {
        PlaceRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (PlaceRepository.class) {
            if (instance == null) {
                instance = new PlaceRepository();
            }
            return instance;
        }
    }

    public CollectionReference getPlaceCollection() {
        return FirebaseFirestore.getInstance().collection("places");
    }
}
