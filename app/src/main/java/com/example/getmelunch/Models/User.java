package com.example.getmelunch.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String name;
    @Nullable
    private String urlImage;
    private String placeId;

    public User() {
    }

    public User(String id, String name, String urlImage, String placeId) {
        this.id = id;
        this.name = name;
        this.urlImage = urlImage;
        this.placeId = placeId;
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

    @Nullable
    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(@Nullable String urlImage) {
        this.urlImage = urlImage;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
