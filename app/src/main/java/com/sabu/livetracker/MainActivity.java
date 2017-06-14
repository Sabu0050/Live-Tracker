package com.sabu.livetracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(Build.VERSION.SDK_INT <23){
            startListening();
        }else {

            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }else{
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(location != null) {

                    updateLocation(location);
                }

            }

        }
    }

    public void updateLocation(Location location){
        Log.i("Location",location.toString());

        TextView latTextView = (TextView) findViewById(R.id.latTextView);

        TextView longTextView = (TextView) findViewById(R.id.lonTextView);

        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);

        latTextView.setText("Latitute:" + location.getLatitude());

        longTextView.setText("Longtitute:" + location.getLongitude());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            String address = "Could not find any Address";

            List<Address> addressList = geocoder.getFromLocation(location.getLatitude() , location.getLongitude(), 1);

            if(addressList != null && addressList.size()>0){
                Log.i("PlaceInfo",addressList.get(0).toString());

                address = "";

                if(addressList.get(0).getSubThoroughfare() != null){

                    address+= addressList.get(0).getSubThoroughfare() + " ";
                }

                if(addressList.get(0).getThoroughfare() != null){
                    address+= addressList.get(0).getThoroughfare() + "\n";
                }
                if(addressList.get(0).getLocality() != null){
                    address+= addressList.get(0).getLocality() + "\n";
                }
                if(addressList.get(0).getPostalCode() != null){
                    address+= addressList.get(0).getPostalCode() + "\n";
                }
                if(addressList.get(0).getCountryName() != null){
                    address+= addressList.get(0).getCountryName() + ".";
                }

                addressTextView.setText("Address: \n"+address);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }


    }
}
