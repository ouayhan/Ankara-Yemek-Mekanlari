package mRecycler;



/**
 * Created by Özgür Uğur Ayhan on 13.10.2017.
 */

public class RestaurantModel {

    private String restaurantName;
    private String restaurantAdress;
    private String phone;
    private String Lat;
    private String Lng;
    private String imageUrl;
    private String summary;



    public RestaurantModel(String restaurantName, String restaurantAdress, String phone, String imageUrl, String Lat, String Lng, String summary) {
        this.restaurantName = restaurantName;
        this.restaurantAdress = restaurantAdress;
        this.imageUrl = imageUrl;
        this.phone=phone;
        this.Lat=Lat;
        this.Lng=Lng;
        this.summary=summary;

    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public String getSummary() {
        return summary;
    }

}
