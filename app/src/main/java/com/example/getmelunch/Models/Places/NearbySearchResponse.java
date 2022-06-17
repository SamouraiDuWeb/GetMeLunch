package com.example.getmelunch.Models.Places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NearbySearchResponse implements Serializable {

    @SerializedName("results")
    @Expose
    private List<Restaurant> results = new ArrayList<Restaurant>();

    @SerializedName("error_message")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Restaurant> getResults() {
        return results;
    }

    public void setResults(List<Restaurant> results) {
        this.results = results;
    }
}
