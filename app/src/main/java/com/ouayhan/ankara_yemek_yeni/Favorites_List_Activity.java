package com.ouayhan.ankara_yemek_yeni;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ouayhan.ankara_yemek_yeni.data.FavoritesContract;
import com.ouayhan.ankara_yemek_yeni.data.FavoritesHelper;

import java.util.ArrayList;
import java.util.List;

import mRecycler.FavoritesRestaurantAdapter;
import mRecycler.FavoritesRestaurantModel;

public class Favorites_List_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<FavoritesRestaurantModel> listItems;
    private FavoritesRestaurantAdapter adapter;
    SQLiteDatabase db3;


    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final String TAG = "MainActivity";
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites__list_);


        Toolbar toolbar = findViewById(R.id.favori_toolbar);
        toolbar.setTitle("Favori Mekanlarım");
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.favori_toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.favorites_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();

        adapter = new FavoritesRestaurantAdapter(listItems, getApplicationContext(), getFragmentManager());

        recyclerView.setAdapter(adapter);

        loadFavoritesData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkMapServices()){
           getLocationPermission();
        }
    }

    private void loadFavoritesData() {

        try {
            favoritesReadList();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private void favoritesReadList() {

        FavoritesHelper databaseHelper = new FavoritesHelper(this);
        db3 = databaseHelper.getReadableDatabase();


        String[] projection = {FavoritesContract.FavoritesEntry.COLUMN_REST_NAME,
                FavoritesContract.FavoritesEntry.COLUMN_REST_ADDRESS,
                FavoritesContract.FavoritesEntry.COLUMN_REST_PHONE,
                FavoritesContract.FavoritesEntry.COLUMN_IMAGE,
                FavoritesContract.FavoritesEntry.COLUMN_LAT,
                FavoritesContract.FavoritesEntry.COLUMN_LNG,
                FavoritesContract.FavoritesEntry.COLUMN_ICON};



        Cursor c = db3.query(FavoritesContract.FavoritesEntry.TABLE_NAME, projection, null, null, null, null, null, null);



        if (c != null) {
            while (c.moveToNext()) {


                int nameIx = c.getColumnIndex(projection[0]);
                int addresIx = c.getColumnIndex(projection[1]);
                int phoneIx = c.getColumnIndex(projection[2]);
                int imageIx = c.getColumnIndex(projection[3]);
                int LatIx = c.getColumnIndex(projection[4]);
                int LngIx = c.getColumnIndex(projection[5]);
                int iconIx=c.getColumnIndex(projection[6]);

                byte[] byteArray = c.getBlob(imageIx);
                byte[] byteArray2 = c.getBlob(iconIx);

                Bitmap image = null;
                Bitmap image2 = null;

                try {

                    image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    image2 = BitmapFactory.decodeByteArray(byteArray2, 0, byteArray2.length);

                } catch (NullPointerException e) {

                    e.printStackTrace();
                }


                FavoritesRestaurantModel favoritesRestaurantModel = new FavoritesRestaurantModel(c.getString(nameIx), c.getString(addresIx),
                        c.getString(phoneIx), image, c.getString(LatIx), c.getString(LngIx),image2);

                listItems.add(favoritesRestaurantModel);

                adapter.notifyDataSetChanged();

            }
        }

        c.close();
        db3.close();

    }

    // maps kısmı
    ////////////////////
    /////////////////////

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bu uygulama düzgün çalışmak için GPS servisine ihtiyaç duyar, GPS'i açmak ister misiniz?")
                .setCancelable(true)
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    finish();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Favorites_List_Activity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Favorites_List_Activity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {

                } else {
                    getLocationPermission();
                }
            }
        }
    }

}
