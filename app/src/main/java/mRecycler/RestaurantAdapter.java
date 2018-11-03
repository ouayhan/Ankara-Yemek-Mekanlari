package mRecycler;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouayhan.ankara_yemek_yeni.R;
import com.ouayhan.ankara_yemek_yeni.Restaurant_Detail;
import com.ouayhan.ankara_yemek_yeni.Restaurant_List_Activity;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Özgür Uğur Ayhan on 13.10.2017.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<RestaurantModel> listItems;
    private RestaurantModel restaurantModel;


    private Context context;

    public RestaurantAdapter(List<RestaurantModel> listItems, Context context, FragmentManager fragMan) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_item, parent, false);

        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        restaurantModel = listItems.get(position);

        holder.setData(restaurantModel, position);

    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView textViewName;
        public TextView textViewAdress;
        public ImageView imageViewRestPicture, ikonPicture;
        int position;

        String phoneNo, Lat, Lng;
        String name;
        String adress;
        String ozet;

        Context context;


        public void setData(RestaurantModel model, int pos) {


            position = pos;
            Lat = model.getLat();
            Lng = model.getLng();
            name = model.getRestaurantName();
            phoneNo = model.getPhone();
            adress = model.getRestaurantAdress();
            ozet = model.getSummary();

            textViewName.setText(model.getRestaurantName());
            textViewAdress.setText(model.getRestaurantAdress());
            ikonPicture.setImageResource(Restaurant_List_Activity.ikon_resource);

            Picasso.with(itemView.getContext())
                    .load(model.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(imageViewRestPicture);

        }


        public ViewHolder(final View itemView, Context context) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.kategori_Mekan);
            textViewAdress = itemView.findViewById(R.id.kategori_Adres);
            imageViewRestPicture = itemView.findViewById(R.id.kategori_Image);
            ikonPicture = itemView.findViewById(R.id.kategori_Icon);
            this.context = context;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, Restaurant_Detail.class);
            intent.putExtra("restoran_ismi", name);
            intent.putExtra("restoran_adresi", adress);
            intent.putExtra("phone_no", phoneNo);
            intent.putExtra("Lat", Lat);
            intent.putExtra("Lng", Lng);
            intent.putExtra("Summary", ozet);


            Bitmap img = ((BitmapDrawable) ikonPicture.getDrawable()).getBitmap();
            String filename = "bitmap.png";
            FileOutputStream stream;
            try {
                stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                //   img.recycle();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.putExtra("ikon_resmi", filename);

            Bitmap img2 = ((BitmapDrawable) imageViewRestPicture.getDrawable()).getBitmap();
            String filename2 = "bitmap2.png";
            FileOutputStream stream2;
            try {
                stream2 = context.openFileOutput(filename2, Context.MODE_PRIVATE);
                img2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
                stream2.close();
                //  img2.recycle();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.putExtra("restoran_resmi", filename2);


            context.startActivity(intent);

        }
    }// end of view holder


    public void setFilter(ArrayList<RestaurantModel> newList) {
        listItems = new ArrayList<>();
        listItems.addAll(newList);
        notifyDataSetChanged();
    }


}//end of adapter
