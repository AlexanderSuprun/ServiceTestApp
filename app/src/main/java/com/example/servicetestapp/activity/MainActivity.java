package com.example.servicetestapp.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicetestapp.R;
import com.example.servicetestapp.app.ServiceTestApp;
import com.example.servicetestapp.service.LocationService;
import com.example.servicetestapp.utils.AppPrefsManager;
import com.example.servicetestapp.utils.LocationPoint;
import com.example.servicetestapp.utils.LocationRecyclerAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int LOCATION_REQUEST_CODE = 100;
    public static final String BROADCAST_ACTION = "com.example.servicetestapp.BROADCAST_ACTION";
    public static final String EXTRA_LOCATION = "com.example.servicetestapp.EXTRA_LOCATION";
    private AppPrefsManager prefsManager;
    private BroadcastReceiver receiver;
    private LocationService locationService;
    private ServiceConnection serviceConnection;
    private List<LocationPoint> locationPointList;
    private LocationRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Location lastSavedLocation = new Location("");
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = ((ServiceTestApp) getApplication()).getPrefsManager();
        locationPointList = prefsManager.getLocations();

        if (locationPointList.size() > 0) {
            lastSavedLocation.setLongitude(locationPointList.get(locationPointList.size() - 1).getLongitude());
            lastSavedLocation.setLatitude(locationPointList.get(locationPointList.size() - 1).getLatitude());
        }

        recyclerView = findViewById(R.id.rv_activity_main);
        adapter = new LocationRecyclerAdapter(locationPointList, getBaseContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(adapter);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                locationService = ((LocationService.ServiceBinder) service).getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location receivedLocation = intent.getParcelableExtra(EXTRA_LOCATION);
                if (lastSavedLocation.distanceTo(receivedLocation) > 10) {
                    lastSavedLocation = receivedLocation;
                    locationPointList.add(new LocationPoint(receivedLocation.getLongitude(),
                            receivedLocation.getLatitude()));
                    adapter.notifyItemInserted(adapter.getItemCount());
                    recyclerView.scrollToPosition(adapter.getItemCount());
                    prefsManager.saveNewLocation(new LocationPoint(receivedLocation.getLongitude(),
                            receivedLocation.getLatitude()));
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(BROADCAST_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                bindService(new Intent(MainActivity.this, LocationService.class),
                        serviceConnection, BIND_AUTO_CREATE);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageRationale();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBound) {
            locationService.hideNotification();
        }
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.getItemCount());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            locationService.showNotification();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(serviceConnection);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindService(new Intent(MainActivity.this, LocationService.class),
                        serviceConnection, BIND_AUTO_CREATE);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageRationale();
            }
        }
    }

    private void showMessageRationale() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.rationale_message)
                .setPositiveButton(getString(R.string.button_title_allow), new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                    }
                })
                .setNegativeButton(getString(R.string.button_title_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}