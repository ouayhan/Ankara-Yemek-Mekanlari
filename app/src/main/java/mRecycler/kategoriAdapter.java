package mRecycler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ouayhan.ankara_yemek_yeni.R;
import com.ouayhan.ankara_yemek_yeni.Restaurant_List_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class kategoriAdapter extends RecyclerView.Adapter<kategoriAdapter.MyViewHolder> {

    ArrayList<kategoriModel> mDataList;
    LayoutInflater inflater;
    Context context;

    public kategoriAdapter(Context context, ArrayList<kategoriModel> data) {

        this.context = context;
        inflater = LayoutInflater.from(context);

        this.mDataList = data;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = inflater.inflate(R.layout.kategori_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        kategoriModel selectedItem = mDataList.get(position);
        holder.setData(selectedItem, position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView katResim, katIkon;
        TextView katAd;
        TextView quantity;
        int restQuantity;
        TextView toplam;
        TextView mekan;

        Context context;


        public MyViewHolder(View itemView, Context context) {
            super(itemView);

            this.context = context;
            katResim = (ImageView) itemView.findViewById(R.id.kategori_Image);
            katIkon = (ImageView) itemView.findViewById(R.id.kategori_Icon);
            katAd = (TextView) itemView.findViewById(R.id.kategori_Mekan);
            quantity = (TextView) itemView.findViewById(R.id.mekan_Quantity);
            toplam = (TextView) itemView.findViewById(R.id.toplam_text);
            mekan = (TextView) itemView.findViewById(R.id.mekan_text);


            itemView.setOnClickListener(this);
        }

        public void setData(kategoriModel selectedItem, int position) {

            this.katAd.setText(selectedItem.getKategoriAd());
            getRestQuantity(position);
            this.katResim.setImageResource(selectedItem.getKategoriResim());
            this.katIkon.setImageResource(selectedItem.getKategoriIkon());

        }

        public void getRestQuantity(int position) {

            String[] katURI = {"https://api.myjson.com/bins/2g63e",
                    "https://api.myjson.com/bins/niz9",
                    "https://api.myjson.com/bins/4iz2w",
                    "https://api.myjson.com/bins/3za7q",
                    "https://api.myjson.com/bins/1dw6pt",
                    "https://api.myjson.com/bins/90jjt"};


            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET,
                    katURI[position], null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject s) {
                            try {

                                JSONArray array = s.getJSONArray("Restaurants");
                                restQuantity = array.length();
                                quantity.setText(String.valueOf(restQuantity));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(context, "Ağ Bağlantısı Zayıf", Toast.LENGTH_LONG).show();
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);

        }

        @Override
        public void onClick(View v) {

            String URL_DATA = "";
            int imageResource = 0;

            int position = getAdapterPosition();

            switch (position) {
                case 5:
                    URL_DATA = "https://api.myjson.com/bins/90jjt";//sushi
                    imageResource = R.drawable.sushi;
                    break;
                case 2:
                    URL_DATA = "https://api.myjson.com/bins/4iz2w";//vegan
                    imageResource = R.drawable.salad;
                    break;
                case 0:
                    URL_DATA = "https://api.myjson.com/bins/2g63e";//balık
                    imageResource = R.drawable.fish;
                    break;
                case 3:
                    URL_DATA = "https://api.myjson.com/bins/3za7q";//fast food
                    imageResource = R.drawable.hamburger;
                    break;
                case 1:
                    URL_DATA = "https://api.myjson.com/bins/niz9";//et
                    imageResource = R.drawable.ham_leg;
                    break;
                case 4:
                    URL_DATA = "https://api.myjson.com/bins/1dw6pt";//tavuk
                    imageResource = R.drawable.chicken;
                    break;
            }

            Intent intent = new Intent(context, Restaurant_List_Activity.class);
            intent.putExtra("adres", URL_DATA);
            intent.putExtra("ikon_resmi", imageResource);
            context.startActivity(intent);

        }
    }
}
