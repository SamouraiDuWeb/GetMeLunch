package com.example.getmelunch.Models.Places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NearbyPlaceOpeningHours implements Serializable {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

}
