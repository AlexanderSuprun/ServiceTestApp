package com.example.servicetestapp.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    public static final long UPDATE_INTERVAL = 10000;
    public static final long FASTEST_UPDATE_INTERVAL = 5000;
    private FusedLocationProviderClient providerClient;
    private LocationRequest request;
    private LocationCallback locationCallback;

    public LocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        providerClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        request.setInterval(UPDATE_INTERVAL);
        request.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            providerClient.requestLocationUpdates(request, locationCallback, Looper.myLooper());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.providerClient.removeLocationUpdates(locationCallback);
    }
}