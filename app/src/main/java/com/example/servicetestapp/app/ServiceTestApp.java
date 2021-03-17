package com.example.servicetestapp.app;

import android.app.Application;

import com.example.servicetestapp.utils.AppPrefsManager;

public class ServiceTestApp extends Application {

    private AppPrefsManager prefsManager;

    @Override
    public void onCreate() {
        super.onCreate();
        prefsManager = new AppPrefsManager(getApplicationContext());
    }

    public AppPrefsManager getPrefsManager() {
        return prefsManager;
    }
}
