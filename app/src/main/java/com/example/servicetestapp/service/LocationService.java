package com.example.servicetestapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.servicetestapp.R;
import com.example.servicetestapp.activity.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Foreground service which tracks location.
 */

public class LocationService extends Service {

    public static final String TAG = "TAG_SERVICE";

    public static final long UPDATE_INTERVAL = 10000;
    public static final long FASTEST_UPDATE_INTERVAL = 5000;
    public static final String CHANNEL_ID = "service_test_app_channel_id";
    public static final CharSequence CHANNEL_NAME = "service_test_app_channel_name";
    public static final int NOTIFICATION_ID = 1;
    private final ServiceBinder binder = new ServiceBinder();
    private FusedLocationProviderClient providerClient;
    private LocationCallback locationCallback;
    private boolean isNotificationVisible = false;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        providerClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        LocationRequest request = new LocationRequest();
        request.setInterval(UPDATE_INTERVAL);
        request.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                sendBroadcast(new Intent(MainActivity.BROADCAST_ACTION)
                        .putExtra(MainActivity.EXTRA_LOCATION, locationResult.getLastLocation()));
                if (isNotificationVisible) {
                    createNotification(locationResult.getLastLocation().getLongitude(),
                            locationResult.getLastLocation().getLatitude());
                }
            }
        };
        try {
            providerClient.requestLocationUpdates(request, locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void showNotification() {
        createNotification(0.0, 0.0);
        isNotificationVisible = true;
    }

    public void hideNotification() {
        stopForeground(true);
        isNotificationVisible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.providerClient.removeLocationUpdates(locationCallback);
    }

    //Creates notification
    //Pass 0.0 to create notification without coordinates
    private void createNotification(double longitude, double latitude) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(channel);
        }

        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, showTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_content_text))
                .setSmallIcon(R.drawable.ic_location_white_18dp)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        startForeground(NOTIFICATION_ID, builder.build());

        if (longitude != 0.0 && latitude != 0.0) {
            builder.setContentText(getString(R.string.coordinates, longitude, latitude));
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public class ServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}