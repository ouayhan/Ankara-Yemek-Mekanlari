package com.ouayhan.ankara_yemek_yeni.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Özgür Uğur Ayhan on 02.12.2017.
 */

public class FavoritesHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Favorilerim";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVORITES_CREATE =
            "CREATE TABLE IF NOT EXISTS " + FavoritesContract.FavoritesEntry.TABLE_NAME + " ("
                    + FavoritesContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FavoritesContract.FavoritesEntry.COLUMN_REST_NAME + " TEXT,"
                    + FavoritesContract.FavoritesEntry.COLUMN_REST_ADDRESS + " TEXT,"
                    + FavoritesContract.FavoritesEntry.COLUMN_REST_PHONE + " TEXT,"
                    + FavoritesContract.FavoritesEntry.COLUMN_IMAGE + " TEXT,"
                    + FavoritesContract.FavoritesEntry.COLUMN_LAT + " TEXT,"
                    + FavoritesContract.FavoritesEntry.COLUMN_LNG + " TEXT,"
                    + FavoritesContract.FavoritesEntry.COLUMN_ICON + " TEXT)";


    public FavoritesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_FAVORITES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
