package com.ouayhan.ankara_yemek_yeni.NearByPlaces;

import android.os.AsyncTask;


import com.google.android.gms.maps.GoogleMap;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GetDirectionsData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;



    private String dura;
    private String dist;

    private WeakReference<TaskCompleted> mCallBack;



    public GetDirectionsData(TaskCompleted myTaskCompleted) {
        this.mCallBack = new WeakReference<>(myTaskCompleted);
    }

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];


        DownloadUrl downloadURL = new DownloadUrl();
        try {
            googleDirectionsData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }


    @Override
    protected void onPostExecute(String s) {

        HashMap<String, String> directionsList = null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        dura = directionsList.get("duration");
        dist = directionsList.get("distance");


        final TaskCompleted callback = mCallBack.get();

        if (callback != null) {
            callback.onTaskComplete(directionsList.get("duration"),directionsList.get("distance"));
        }

    }

}
