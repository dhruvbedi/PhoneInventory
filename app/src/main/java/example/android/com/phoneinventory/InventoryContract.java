package example.android.com.phoneinventory;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bmada on 6/23/2017.
 */

public final class InventoryContract {
    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "example.android.com.phoneinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    public static final class NewEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "Name";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_PRICE = "priceString";
        public final static String COLUMN_PICTURE = "picture";
    }

}
