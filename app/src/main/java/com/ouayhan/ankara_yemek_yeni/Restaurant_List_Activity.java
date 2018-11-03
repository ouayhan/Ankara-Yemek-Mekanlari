package com.ouayhan.ankara_yemek_yeni;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mRecycler.RestaurantAdapter;
import mRecycler.RestaurantModel;

public class Restaurant_List_Activity extends AppCompatActivity implements SearchView.OnQueryTextListener {



    private String URL_DATA = "";
    private Integer imageResource;
    public static int ikon_resource;

    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;

    private List<RestaurantModel> listItems;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant__list_);

        Intent intent = getIntent();

        URL_DATA = intent.getStringExtra("adres");
        imageResource = intent.getIntExtra("image", 0);
        ikon_resource = intent.getIntExtra("ikon_resmi", 0);

        Toolbar toolbar = findViewById(R.id.List_toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        listItems = new ArrayList<>();

        new MyAsyncTask().execute(URL_DATA);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.gotofavorites, menu);
        MenuItem menuItem = menu.findItem(R.id.aramaYap);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setBackgroundColor(getResources().getColor(R.color.SearchViewBg));
        searchView.setQueryHint("Mekan Ara");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.favorilereGit) {

                Intent intent = new Intent(getApplicationContext(), Favorites_List_Activity.class);
                startActivity(intent);
            }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private void loadRecyclerViewData(String uri_data) {

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET,
                uri_data, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject s) {
                        try {
                            //JSONObject jsonObject = new JSONObject(s);

                            JSONArray array = s.getJSONArray("Restaurants");


                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                RestaurantModel restaurantModel = new RestaurantModel(
                                        new String(o.getString("Name").getBytes("UTF-8"), "UTF-8"),
                                        new String(o.getString("address").getBytes("UTF-8"), "UTF-8"),
                                        o.getString("phone"),
                                        o.getString("image"),
                                        o.getString("Lat"),
                                        o.getString("Lng"),
                                        o.getString("Summary")
                                );
                                listItems.add(restaurantModel);
                            }
                            Collections.sort(listItems, new Comparator<RestaurantModel>() {
                                @Override
                                public int compare(RestaurantModel o1, RestaurantModel o2) {
                                    return o1.getRestaurantName().compareToIgnoreCase(o2.getRestaurantName());
                                }
                            });
                            adapter = new RestaurantAdapter(listItems, getApplicationContext(), getFragmentManager());
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<RestaurantModel> newList = new ArrayList<>();

        for (RestaurantModel restaurant : listItems) {
            String name = restaurant.getRestaurantName().toLowerCase();
            if (name.contains(newText)) {
                newList.add(restaurant);
            }
        }
        adapter.setFilter(newList);
        return true;
    }


    class MyAsyncTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... voids) {
            loadRecyclerViewData(voids[0]);
            return null;
        }

    }


}
