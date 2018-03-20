package example.android.com.phoneinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import example.android.com.phoneinventory.InventoryContract.NewEntry;

/**
 * Created by bmada on 6/23/2017.
 */

public class InventoryProvider extends ContentProvider {
    private InventoryDBHelper mDbHelper;
    public static final int ITEM = 50;
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    public static final int ITEM_ID = 51;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, ITEM);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                cursor = database.query(NewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = NewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(NewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return NewEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return NewEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                Uri newUri = insertStock(uri, contentValues);
                return newUri;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertStock(Uri uri, ContentValues values) {
        String name = values.getAsString(NewEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Phone requires a Name");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(NewEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.failed_to_insert_row) + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.e(LOG_TAG, getContext().getString(R.string.successfully_inserted_row_for) + uri);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case ITEM:
                rowsUpdated = database.delete(NewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = NewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.delete(NewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("deletion is not supported for " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return updateStock(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = NewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsUpdated = updateStock(uri, contentValues, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            default:
                throw new IllegalArgumentException("update is not supported for " + uri);
        }
    }

    private int updateStock(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rows = database.update(NewEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rows == -1) {
            Log.e(LOG_TAG, "FAIL " + uri);
            return 0;
        }
        return rows;
    }
}