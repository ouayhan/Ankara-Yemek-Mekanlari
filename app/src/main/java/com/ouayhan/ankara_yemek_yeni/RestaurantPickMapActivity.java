package com.ouayhan.ankara_yemek_yeni;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ouayhan.ankara_yemek_yeni.NearByPlaces.GetDirectionsData;
import com.ouayhan.ankara_yemek_yeni.NearByPlaces.TaskCompleted;

import java.util.List;

public class RestaurantPickMapActivity extends FragmentActivity implements TaskCompleted {
    private GoogleMap mMap;
    LatLng restCoords;
    Double latitude, longitude, current_latitude, current_longitude;
    String restName;

    ImageView yolTarifi;
    TextView time, road, nameRest;
    CardView cardView;


    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_pick_map);

        getDeviceLocation();

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        restName = intent.getStringExtra("restaurant_name");
        restCoords = new LatLng(latitude, longitude);

        yolTarifi = findViewById(R.id.yolAraba);
        time = findViewById(R.id.zaman);
        road = findViewById(R.id.mesafe);
        nameRest = findViewById(R.id.restName);
        cardView = findViewById(R.id.CardView);


        nameRest.setText(restName);

        yolTarifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getDirectionsPage();
            }
        });

    }

    private void getDirectionsPage(){
        Uri.Builder directionsBuilder = new Uri.Builder()
                .scheme("https")
                .authority("www.google.com")
                .appendPath("maps")
                .appendPath("dir")
                .appendPath("")
                .appendQueryParameter("api", "1")
                .appendQueryParameter("destination", latitude + "," + longitude);

        startActivity(new Intent(Intent.ACTION_VIEW, directionsBuilder.build()));
    }


    private void getTimeAndDistance() {

        Object dataTransfer[] = new Object[3];
        String url = getDirectionsUrl();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetDirectionsData getDirectionsData = new GetDirectionsData(this);


        getDirectionsData.execute(dataTransfer);

    }


    private String getDirectionsUrl() {

        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + current_latitude + "," + current_longitude);
        googleDirectionsUrl.append("&destination=" + latitude + "," + longitude);
        googleDirectionsUrl.append("&key=" + "AIzaSyCAx07hiN_Dv11IzoN6UuvFmWN3d-FNABE");

        return googleDirectionsUrl.toString();
    }



    private void initMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;


                    if (ActivityCompat.checkSelfPermission(RestaurantPickMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMapToolbarEnabled(true);


                    final Marker marker = mMap.addMarker(new MarkerOptions().position(restCoords).title(restName));
                    marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restCoords, 15f));

                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        getTimeAndDistance();
                    }
                });


            }
        });

    }



    private void getDeviceLocation() {

        initMap();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        try {


                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                LocationCallback mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        List<Location> locationList = locationResult.getLocations();
                        if (locationList.size() > 0) {
                            //The last location in the list is the newest
                            Location location = locationList.get(locationList.size() - 1);
                            Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                            current_latitude = location.getLatitude();
                            current_longitude = location.getLongitude();

                        }
                    }
                };

                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());



        } catch (SecurityException e) {
            e.getMessage();
        }

    }

    @Override
    public void onTaskComplete(String res1, String res2) {

        time.setText(res1);
        road.setText(res2);
    }

}
