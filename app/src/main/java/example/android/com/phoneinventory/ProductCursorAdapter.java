package example.android.com.phoneinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import example.android.com.phoneinventory.InventoryContract.NewEntry;

/**
 * Created by dell on 6/23/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.saleButton = (Button) view.findViewById(R.id.SaleButton);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = new ViewHolder();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(NewEntry._ID));
        Uri productUri = ContentUris.withAppendedId(NewEntry.CONTENT_URI, id);
        holder.title = (TextView) view.findViewById(R.id.name);
        holder.quantity= (TextView) view.findViewById(R.id.qty);
        holder.price = (TextView) view.findViewById(R.id.price);
        int nameColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_NAME);
        int qtyColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(NewEntry.COLUMN_PRICE);
        String proName = cursor.getString(nameColumnIndex);
        int proQty = cursor.getInt(qtyColumnIndex);
        int proPrice = cursor.getInt(priceColumnIndex);
        holder.saleButton = (Button) view.findViewById(R.id.SaleButton);

        holder.saleButton.setOnClickListener(clickView -> {
            if (proQty > 0) {
                ContentValues values = new ContentValues();
                values.put(NewEntry.COLUMN_QUANTITY, proQty - 1);
                int updatedRows = context.getContentResolver()
                        .update(productUri, values, null, null);
                if (updatedRows == 0) {
                    Toast.makeText(context, R.string.prodFail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.proSold, Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent i = new Intent(context, QuantityEdit.class);
                i.putExtra("QUANTA", cursor.getString(qtyColumnIndex));
                i.putExtra("NAAM", proName);
                i.putExtra("PRICE", cursor.getString(priceColumnIndex));
                context.startActivity(i);
                Toast.makeText(context, R.string.prodNotavail, Toast.LENGTH_SHORT).show();
            }
        });

        holder.title.setText(proName);
        String qtystr = "Quantity: ";
        qtystr = qtystr + proQty;
        holder.quantity.setText(qtystr);
        String pricestr = "Price:  ";
        pricestr = pricestr + proPrice;
        holder.price.setText(pricestr);
    }

    private class ViewHolder {
        TextView title;
        TextView quantity;
        TextView price;
        Button saleButton;
    }
}
