package com.example.getmelunch.Models.Places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private String lat;
    private String lng;
    private String address;
    private int count;
    private long rating;
    private Boolean openNow;
    private String phoneNumber;
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

    public List<PlacePhotos> getPhotos() {
        return photos;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
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

    /**
     * @return The vicinity
     */
    public String getVicinity() {
        return vicinity;
    }

    /**
     * @param vicinity The vicinity
     */
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    /**
     * @return The geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * @param geometry The geometry
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }


    public NearbyPlaceOpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(NearbyPlaceOpeningHours openingHours) {
        this.openingHours = openingHours;
    }
}