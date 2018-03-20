package example.android.com.phoneinventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import example.android.com.phoneinventory.InventoryContract.NewEntry;

/**
 * Created by bmada on 6/23/2017.
 */

public class InventoryDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Inventory.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String PhoneSQL = "CREATE TABLE " + NewEntry.TABLE_NAME + " ("
                + NewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewEntry.COLUMN_PRICE + " FLOAT NOT NULL DEFAULT 0.00,"
                + NewEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + NewEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + NewEntry.COLUMN_PICTURE + " TEXT ) ;";
        db.execSQL(PhoneSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
