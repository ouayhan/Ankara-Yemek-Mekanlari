package mRecycler;

import android.graphics.Bitmap;

/**
 * Created by Özgür Uğur Ayhan on 13.10.2017.
 */

public class FavoritesRestaurantModel {

    private String restaurantName;
    private String restaurantAdress;
    private String phone;
    private String Lat;
    private String Lng;
    private Bitmap restImage;
    private Bitmap restIcon;

    public FavoritesRestaurantModel(String restaurantName, String restaurantAdress, String phone, Bitmap image, String Lat, String Lng, Bitmap image2) {
        this.restaurantName = restaurantName;
        this.restaurantAdress = restaurantAdress;
        this.restImage = image;
        this.phone=phone;
        this.Lat=Lat;
        this.Lng=Lng;
        this.restIcon=image2;
    }

    public String getPhone() {
        return phone;
    }

    public Bitmap getImageBitmap() {
        return restImage;
    }

    public Bitmap getRestIcon() {
        return restIcon;
    }

    public String getLat() {
        return Lat;
    }

    public String getLng() {
        return Lng;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantAdress() {
        return restaurantAdress;
    }



}
