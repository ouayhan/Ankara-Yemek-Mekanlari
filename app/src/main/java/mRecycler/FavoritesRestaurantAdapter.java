package mRecycler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ouayhan.ankara_yemek_yeni.MainActivity;
import com.ouayhan.ankara_yemek_yeni.MapActivity;
import com.ouayhan.ankara_yemek_yeni.R;
import com.ouayhan.ankara_yemek_yeni.RestaurantPickMapActivity;
import com.ouayhan.ankara_yemek_yeni.data.FavoritesContract;
import com.ouayhan.ankara_yemek_yeni.data.FavoritesHelper;

import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Özgür Uğur Ayhan on 13.10.2017.
 */

public class FavoritesRestaurantAdapter extends RecyclerView.Adapter<FavoritesRestaurantAdapter.ViewHolder>  {


    private List<FavoritesRestaurantModel> listItems;
    private FavoritesRestaurantModel restaurantModel;
    private FragmentManager fm;

    FavoritesHelper databaseHelper;
    SQLiteDatabase db;


    private Context context;
    private Cursor mCursor;
    private Long id;


    public FavoritesRestaurantAdapter(List<FavoritesRestaurantModel> listItems, Context context, FragmentManager fragMan) {
        this.listItems = listItems;
        this.context = context;
        this.fm = fragMan;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list_item, parent, false);

        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        restaurantModel = listItems.get(position);

        databaseHelper = new FavoritesHelper(context);
        db = databaseHelper.getWritableDatabase();

        mCursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null, null);

        holder.textViewName.setText(restaurantModel.getRestaurantName());
        holder.textViewAdress.setText(restaurantModel.getRestaurantAdress());
        holder.imageViewRestPicture.setImageBitmap(restaurantModel.getImageBitmap());
        holder.icon_Lokanta.setImageBitmap(restaurantModel.getRestIcon());
        holder.setPosition(position);

    }

    public void deleteItem(int position) {

        mCursor.moveToPosition(position);

        id = mCursor.getLong(mCursor.getColumnIndex(FavoritesContract.FavoritesEntry._ID));

        db.delete(FavoritesContract.FavoritesEntry.TABLE_NAME,
                FavoritesContract.FavoritesEntry._ID + "=" + id, null);

        swapCursor(getAllItems());


        listItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listItems.size());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    private Cursor getAllItems() {

        return db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null, null);

    }


    public void swapCursor(Cursor newCursor) {

        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public TextView textViewAdress;
        public ImageView imageViewRestPicture;
        public ImageView sepetResmi, phone_Resim, haritaIkon, icon_Lokanta;

        int position;
        Context context;

        public ViewHolder(final View itemView,final Context context)  {
            super(itemView);

            this.context = context;
            textViewName = itemView.findViewById(R.id.nameOfRestaurant);
            textViewAdress = itemView.findViewById(R.id.adresOfRestaurant);
            imageViewRestPicture = itemView.findViewById(R.id.imageOfRestaurant);
            sepetResmi = itemView.findViewById(R.id.sepetResmi);
            phone_Resim = itemView.findViewById(R.id.phoneResim);
            haritaIkon = itemView.findViewById(R.id.mapResim);
            icon_Lokanta=itemView.findViewById(R.id.kategori_Icon);

            sepetResmi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(position);
                }
            });

            phone_Resim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URI = "tel:" + listItems.get(position).getPhone();
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse(URI));
                    context.startActivity(phoneIntent);
                }
            });

            haritaIkon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startIntent();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void startIntent(){
            Intent restaurantPickIntent = new Intent(context, RestaurantPickMapActivity.class);
            restaurantPickIntent.putExtra("latitude", Double.parseDouble(listItems.get(position).getLat()));
            restaurantPickIntent.putExtra("longitude", Double.parseDouble(listItems.get(position).getLng()));
            restaurantPickIntent.putExtra("restaurant_name", listItems.get(position).getRestaurantName());
            context.startActivity(restaurantPickIntent);
        }


        public void setPosition(int pos) {
            position = pos;
        }

    }



}
