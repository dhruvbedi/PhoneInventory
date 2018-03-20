package example.android.com.phoneinventory;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuantityEdit extends AppCompatActivity {
    String valueString;
    String priceString;
    String Name;
    int Value;
    int Price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quantity_edit);
        Value = getIntent().getIntExtra("QUANTA",10);
        Price = getIntent().getIntExtra("PRICE",110);
        valueString = getIntent().getStringExtra("QUANTA");
        priceString = getIntent().getStringExtra("PRICE");
        Name = getIntent().getStringExtra("NAAM");
        TextView TextViewName = (TextView) findViewById(R.id.nameTextEdit);
        TextView TextViewQuantity = (TextView) findViewById(R.id.quantityTextEdit);
        TextView TextViewPrice = (TextView) findViewById(R.id.priceTextEdit);
        TextViewName.setText(Name);
        TextViewQuantity.setText(valueString);
        TextViewPrice.setText(priceString);
    }
    public void plusOne(View view) {
        Value = Value + 1;
        TextView mQuantityTextView = (TextView) findViewById(R.id.quantityTextEdit);
        valueString = "" + Value;
        mQuantityTextView.setText(valueString);
    }
    public void minusOne(View view) {
        Value = Value - 1;
        valueString = "" + Value;
        if (Value <= 0) {
            Toast.makeText(this, "Would result in negative valueString", Toast.LENGTH_SHORT).show();
            Value = 0;
        }
        TextView mQuantityTextView = (TextView) findViewById(R.id.quantityTextEdit);
        mQuantityTextView.setText(valueString);
    }
    public void order(View view) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(date);
        String subject = "[SUPPLY] Order at " + formattedDate;
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (Value <= 0) {
            Value += 1;
        }
        String body = "Name  " + Name + "\nOrder Cost " + Value * Price + "\n Quantity " + valueString + "\nPrice " + priceString;
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(intent);
    }
}
