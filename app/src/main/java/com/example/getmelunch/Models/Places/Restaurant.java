package com.example.getmelunch.Models.Places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("rating")
    @Expose
    private float rating;

    private String phoneNumber;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    @SerializedName("photos")
    @Expose
    private List<PlacePhotos> photos = null;

    @SerializedName("opening_hours")
    @Expose
    private NearbyPlaceOpeningHours openingHours;

    private String docId;

    //constructor
    public Restaurant(String name,String docId, float rating, String phoneNumber, String placeId, Geometry geometry, String vicinity, List<PlacePhotos> photos, NearbyPlaceOpeningHours openingHours) {
        this.name = name;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.placeId = placeId;
        this.geometry = geometry;
        this.vicinity = vicinity;
        this.photos = photos;
        this.openingHours = openingHours;
        this.docId = docId;
    }

    public Restaurant() {
    }

    public List<PlacePhotos> getPhotos() {
        return photos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void setOpeningHours(NearbyPlaceOpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public NearbyPlaceOpeningHours getOpeningHours() {
        return openingHours;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}