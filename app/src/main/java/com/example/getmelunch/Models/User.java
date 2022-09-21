package com.example.getmelunch.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String uid;

    private String name;
    @Nullable
    private String pictureUrl;
    @Nullable
    private String lunchSpotId;
    @Nullable
    private String lunchSpotName;
    @Nullable
    private String lunchSpotAddress;

    private Boolean isNotificationEnabled;

    private List<String> favoriteRestaurants;

    public User () {}

    public User(String uid, String name, @Nullable String pictureUrl, @Nullable String lunchSpotId, @Nullable String lunchSpotName, @Nullable String lunchSpotAddress, List<String> favoriteRestaurants) {
        this.uid = uid;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.lunchSpotId = lunchSpotId;
        this.lunchSpotName = lunchSpotName;
        this.lunchSpotAddress = lunchSpotAddress;
        this.favoriteRestaurants = favoriteRestaurants;
    }

    public User(String name, String lunchspotName, String pictureUrl) {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(@Nullable String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Nullable
    public String getLunchSpotId() {
        return lunchSpotId;
    }

    public void setLunchSpotId(@Nullable String lunchSpotId) {
        this.lunchSpotId = lunchSpotId;
    }

    @Nullable
    public String getLunchSpotName() {
        return lunchSpotName;
    }

    public void setLunchSpotName(@Nullable String lunchSpotName) {
        this.lunchSpotName = lunchSpotName;
    }

    @Nullable
    public String getLunchSpotAddress() {
        return lunchSpotAddress;
    }

    public void setLunchSpotAddress(@Nullable String lunchSpotAddress) {
        this.lunchSpotAddress = lunchSpotAddress;
    }

    public Boolean getNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setNotificationEnabled(Boolean notificationEnabled) {
        isNotificationEnabled = notificationEnabled;
    }

    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setFavoriteRestaurants(List<String> favoriteRestaurants) {
        this.favoriteRestaurants = favoriteRestaurants;
    }
}
