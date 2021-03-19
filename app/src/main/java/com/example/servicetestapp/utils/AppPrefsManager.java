package com.example.servicetestapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages {@link SharedPreferences}
 */

public class AppPrefsManager {

    public static final String PREFS_NAME = "com.example.servicetestapp.PREFS_NAME";
    public static final String PREFS_LOCATIONS = "com.example.servicetestapp.PREFS_LOCATIONS";
    private final Context context;
    private List<LocationPoint> locationPoints;

    public AppPrefsManager(Context context) {
        this.context = context;
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveNewLocation(LocationPoint location) {
        locationPoints = getLocations();
        if (locationPoints.size() > 50) {
            locationPoints.remove(0);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < locationPoints.size(); i++) {
            stringBuilder.append(locationPoints.get(i).toString()).append(",");
        }
        stringBuilder.append(location.toString()).append(",");
        getPrefs().edit().putString(PREFS_LOCATIONS, stringBuilder.toString()).apply();
    }

    public List<LocationPoint> getLocations() {
        String locations = getPrefs().getString(PREFS_LOCATIONS, null);
        locationPoints = new ArrayList<>();
        if (locations != null) {
            String stringPoints;
            for (int i = 0; i < locations.split(",").length; i++) {
                stringPoints = locations.split(",")[i];
                locationPoints.add(new LocationPoint(Double.parseDouble(stringPoints.split(" ")[0]),
                        Double.parseDouble(stringPoints.split(" ")[1])));
            }
        }
        return locationPoints;
    }
}
