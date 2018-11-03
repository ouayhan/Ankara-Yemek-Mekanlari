package com.ouayhan.ankara_yemek_yeni;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ouayhan.ankara_yemek_yeni.data.FavoritesContract;
import com.ouayhan.ankara_yemek_yeni.data.FavoritesHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class Restaurant_Detail extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final String TAG = "Restaurant_Detail";
    TextView isim, adres, sumText;
    ImageView small_img, large_img;
    Button phone_img, map_img, kalp_img;
    ConstraintLayout constraintLayout;
    Boolean mLocationPermissionGranted = false;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant__detail);


        constraintLayout = findViewById(R.id.root_layout);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);


        phone_img = findViewById(R.id.phone_ikon);
        map_img = findViewById((R.id.harita_ikon));
        kalp_img = findViewById(R.id.kalp_ikon);

        isim = findViewById(R.id.restoran_isim);
        isim.setText(getIntent().getStringExtra("restoran_ismi"));

        adres = findViewById(R.id.restoran_adres);
        adres.setText(getIntent().getStringExtra("restoran_adresi"));

        Bitmap bb = null;
        String filename2 = getIntent().getStringExtra("restoran_resmi");
        try {
            FileInputStream is = this.openFileInput(filename2);
            bb = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        large_img = findViewById(R.id.large_resim);
        large_img.setImageBitmap(bb);

        Bitmap b = null;
        String filename = getIntent().getStringExtra("ikon_resmi");
        try {
            FileInputStream is = this.openFileInput(filename);
            b = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        small_img = findViewById(R.id.small_ikon);
        small_img.setImageBitmap(b);


        sumText = findViewById(R.id.summaryText);
        sumText.setText(getIntent().getStringExtra("Summary"));
        sumText.setMovementMethod(new ScrollingMovementMethod());


        phone_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URI = "tel:" + getIntent().getStringExtra("phone_no");
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse(URI));
                v.getContext().startActivity(phoneIntent);
            }
        });

        map_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkMapServices()) {
                    if (mLocationPermissionGranted) {
                         startIntent();
                    } else {
                        getLocationPermission();
                    }
                }
            }
        });

        kalp_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    favoritesMakeList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void startIntent(){
        Intent restaurantPickIntent = new Intent(getApplicationContext(), RestaurantPickMapActivity.class);
        restaurantPickIntent.putExtra("latitude", Double.parseDouble(getIntent().getStringExtra("Lat")));
        restaurantPickIntent.putExtra("longitude", Double.parseDouble(getIntent().getStringExtra("Lng")));
        restaurantPickIntent.putExtra("restaurant_name", getIntent().getStringExtra("restoran_ismi"));
        startActivity(restaurantPickIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gotohome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.gotohome) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue, SQLiteDatabase sqldb) {
        Cursor cursor;
        String Query = "Select * from " + TableName + " where " + dbfield + " = \"" + fieldValue + "\";";


        cursor = sqldb.rawQuery(Query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        cursor.moveToLast();
        cursor.close();
        return true;

    }

    private void favoritesMakeList() {

        large_img.buildDrawingCache();
        Bitmap img = large_img.getDrawingCache();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        small_img.buildDrawingCache();
        Bitmap img_small = small_img.getDrawingCache();
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        img_small.compress(Bitmap.CompressFormat.PNG, 50, outputStream2);
        byte[] byteArray2 = outputStream2.toByteArray();

        FavoritesHelper databaseHelper = new FavoritesHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if (CheckIsDataAlreadyInDBorNot(FavoritesContract.FavoritesEntry.TABLE_NAME, FavoritesContract.FavoritesEntry.COLUMN_REST_NAME, getIntent().getStringExtra("restoran_ismi"), db)) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Zaten Listede Var", Snackbar.LENGTH_SHORT)
                    .setAction("FAVORİLERE GİT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), Favorites_List_Activity.class);
                            startActivity(intent);
                        }
                    });
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
            db.close();
            return;
        }

        ContentValues yeniKayit = new ContentValues();

        yeniKayit.put(FavoritesContract.FavoritesEntry.COLUMN_REST_NAME, getIntent().getStringExtra("restoran_ismi"));
        yeniKayit.put(FavoritesContract.FavoritesEntry.COLUMN_REST_ADDRESS, getIntent().getStringExtra("restoran_adresi"));
        yeniKayit.put(FavoritesContract.FavoritesEntry.COLUMN_REST_PHONE, getIntent().getStringExtra("phone_no"));
        yeniKayit.put(FavoritesContract.FavoritesEntry.COLUMN_IMAGE, byteArray);
        yeniKayit.put(FavoritesContract.FavoritesEntry.COLUMN_LAT, getIntent().getStringExtra("Lat"));
        yeniKayit.put(FavoritesContract.FavoritesEntry.COLUMN_LNG, getIntent().getStringExtra("Lng"));
        yeniKayit.put(FavoritesContract.FavoritesEntry.COLUMN_ICON, byteArray2);


        db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, yeniKayit);
        db.close();

        Snackbar snackbar = Snackbar.make(constraintLayout, "Favorilere Eklendi", Snackbar.LENGTH_SHORT)
                .setAction("FAVORİLERE GİT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), Favorites_List_Activity.class);
                        startActivity(intent);
                    }
                });

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.color_favori_bar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }


    ///////////////////////////////////////////////////////////


    //////////////////////////////////////

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
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            startIntent();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Restaurant_Detail.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Restaurant_Detail.this, available, ERROR_DIALOG_REQUEST);
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
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    startIntent();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

}
