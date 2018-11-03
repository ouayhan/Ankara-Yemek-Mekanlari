package com.ouayhan.ankara_yemek_yeni.data;

import android.provider.BaseColumns;

/**
 * Created by Özgür Uğur Ayhan on 02.12.2017.
 */

public class FavoritesContract {

    public static final class FavoritesEntry implements BaseColumns{

        public static final String TABLE_NAME = "Favorilerim";

        public static final String COLUMN_REST_NAME = "name";

        public static final String COLUMN_REST_ADDRESS = "addres";

        public static final String COLUMN_REST_PHONE = "phone";

        public static final String COLUMN_IMAGE="image";

        public static final String COLUMN_LAT = "Lat";

        public static final String COLUMN_LNG = "Lng";

        public static final String COLUMN_ICON="icon";
    }
}
