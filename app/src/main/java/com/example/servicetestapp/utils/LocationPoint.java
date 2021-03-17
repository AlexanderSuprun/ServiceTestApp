package com.example.servicetestapp.utils;

import androidx.annotation.NonNull;

public class LocationPoint {

    private double latitude;
    private double longitude;

    public LocationPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return latitude + " " + longitude;
    }
}
