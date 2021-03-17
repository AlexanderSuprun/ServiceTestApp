package com.example.servicetestapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicetestapp.R;
import com.example.servicetestapp.app.ServiceTestApp;
import com.example.servicetestapp.utils.AppPrefsManager;
import com.example.servicetestapp.utils.LocationPoint;
import com.example.servicetestapp.utils.LocatonRecyclerAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int LOCATION_REQUEST_CODE = 100;
    private List<LocationPoint> locationPointList;
    private AppPrefsManager prefsManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_activity_main);
        prefsManager = ((ServiceTestApp) getApplication()).getPrefsManager();
        locationPointList = prefsManager.getLocations();
        recyclerView.setAdapter(new LocatonRecyclerAdapter(locationPointList, getBaseContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //permission granted
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageRationale();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
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

                    }
                })
                .create()
                .show();
    }
}