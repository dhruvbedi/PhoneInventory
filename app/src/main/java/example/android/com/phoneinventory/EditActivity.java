package example.android.com.phoneinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileDescriptor;
import java.io.IOException;

import example.android.com.phoneinventory.InventoryContract.NewEntry;


public class EditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri mCurrentItemUri;
    private EditText EditTextName;
    private EditText EditTextPrice;
    private EditText EditTextQuantity;
    private boolean mItemHasChanged = false;
    private ImageView image;
    private Uri imageUri = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
        image = (ImageView) findViewById(R.id.selectedImageView);
        EditTextName = (EditText) findViewById(R.id.phoneName );
        EditTextPrice = (EditText) findViewById(R.id.phonePrice);
        EditTextQuantity = (EditText) findViewById(R.id.phoneQuantity);
        EditTextName.setOnTouchListener(mTouchListener);
        EditTextPrice.setOnTouchListener(mTouchListener);
        EditTextQuantity.setOnTouchListener(mTouchListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void orderMore(View view) {
        Intent intent = new Intent(this, QuantityEdit.class);
        String quantity = EditTextQuantity.getText().toString().trim();
        String price = EditTextPrice.getText().toString().trim();
        String name = EditTextName.getText().toString().trim();
        intent.putExtra("QUANTA", quantity);
        intent.putExtra("NAAM", name);
        intent.putExtra("PRICE", price);
        startActivity(intent);
    }

    public void ImageClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = getBitmapFromUri(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            } else {
                image.setImageResource(R.drawable.ic_add_circle_white_48dp);
            }
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    private void saveItem() {

        String stringName = EditTextName.getText().toString().trim();
        String stringPrice = EditTextPrice.getText().toString().trim();
        String quantityString = EditTextQuantity.getText().toString().trim();
        String stringPhoto = getPhotoUri();
        if (imageUri != null) {
            stringPhoto = imageUri.toString();
        }
        if (mCurrentItemUri == null &&
                (TextUtils.isEmpty(stringName) || TextUtils.isEmpty(stringPrice) || TextUtils.isEmpty(quantityString))) {
            Toast.makeText(this, "All the entries must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(NewEntry.COLUMN_NAME, stringName);
        values.put(NewEntry.COLUMN_PRICE, stringPrice);
        values.put(NewEntry.COLUMN_QUANTITY, quantityString);
        values.put(NewEntry.COLUMN_PICTURE, stringPhoto);
        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(NewEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this,getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                NewEntry._ID,
                NewEntry.COLUMN_NAME,
                NewEntry.COLUMN_PRICE,
                NewEntry.COLUMN_QUANTITY,
                NewEntry.COLUMN_PICTURE};
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_QUANTITY);
            int photoColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_PICTURE);
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String photo = cursor.getString(photoColumnIndex);
            EditTextName.setText(name);
            EditTextPrice.setText(price);
            EditTextQuantity.setText(Integer.toString(quantity));
            if (!TextUtils.isEmpty(photo)) {
                image.setImageURI(Uri.parse(photo));
            } else {
                image.setImageResource(R.drawable.ic_add_circle_white_48dp);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        EditTextName.setText("");
        EditTextPrice.setText("");
        EditTextQuantity.setText("");
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Edit Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private String getPhotoUri() {
        String uri = null;

        if (mCurrentItemUri != null) {
            // Query the database for the photo uri at ProductEntry.COLUMN_PRODUCT_PHOTO
            Cursor cursor = getContentResolver().query(mCurrentItemUri,
                    new String[]{NewEntry.COLUMN_PICTURE}, null, null, null);

            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            if (cursor.moveToFirst()) {
                // Find the columns of product attributes that we're interested in
                int photoColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_PICTURE);

                // Extract out the valueString from the Cursor for the given column index
                uri = cursor.getString(photoColumnIndex);
            }
        }
        return uri;
    }

}