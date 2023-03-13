package edu.tnut.appgps;

import static edu.tnut.appgps.R.id.tv_lable1_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address,tv_waypointcount;
    Switch sw_locationupdates, sw_gps;
    Button bnt_newwaypoint,bnt_showwaypoint, bnt_showmap;

    boolean updateOn = false;
    Location currentLocation;
    List<Location> saveLocation;

    LocationRequest locationRequest;

    LocationCallback locationcallBack;

    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_lat = findViewById(R.id.tv_lat);

        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_altitude);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        bnt_newwaypoint = findViewById(R.id.bnt_newwaypoint);
        bnt_showwaypoint = findViewById(R.id.bnt_showwaypoint);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        tv_waypointcount = findViewById(tv_lable1_1);
        bnt_showmap= findViewById(R.id.bnt_showmap);



        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationcallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                UpdateUIValues(locationResult.getLastLocation());
            }
        };
        bnt_newwaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myclass Myclass= (myclass)getApplicationContext();
                saveLocation  = Myclass.getMyLocation();
                saveLocation.add(currentLocation);
            }
        });
        bnt_showwaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,showsavelocation.class);
                startActivity(i);
            }
        });
        bnt_showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MapsActivity2.class);
                startActivity(i);
            }
        });


        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("using GPS sensor");

                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("using tower + wf");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    startlocationUpdates();
                } else {
                    stoplocationUpdates();
                }
            }
        });
        updateGPS();

    }

    private void stoplocationUpdates() {
        tv_updates.setText("location is not tracked");
        tv_lat.setText("not tracking location");
        tv_lon.setText("not tracking location");
        tv_speed.setText("not tracking location");
        tv_address.setText("not tracking location");
        tv_accuracy.setText("not tracking location");
        tv_altitude.setText("not tracking location");
        tv_sensor.setText("not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationcallBack);

    }

    @SuppressLint("MissingPermission")
    private void startlocationUpdates() {
        tv_updates.setText("location is being tracked ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationcallBack, null);
        updateGPS();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                updateGPS();

            }
            else {
                Toast.makeText(this, "This aoo requires permission to be granted in order to work", Toast.LENGTH_SHORT).show();
                finish();
            }
           break;
        }
    }

    @SuppressLint("MissingPermission")
    private void updateGPS(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    UpdateUIValues(location);
                    currentLocation=location;

                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void UpdateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("not available");
        }
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }

        else {
            tv_speed.setText("not available");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);;
        try{
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception ex){
            tv_address.setText("unable to get street address");

        }
        myclass Myclass= (myclass)getApplicationContext();
        saveLocation  = Myclass.getMyLocation();

        tv_waypointcount.setText(Integer.toString(saveLocation.size()));


    }

}