package com.example.android.inventoryapp.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookStoreDbContract;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductsListCursorAdapter extends CursorAdapter {
    public ProductsListCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.products_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView productName = view.findViewById(R.id.tv_list_item_product_name);
        TextView productPrice = view.findViewById(R.id.tv_list_item_product_price);
        TextView productQuantity = view.findViewById(R.id.tv_list_item_product_quantity);
        Button saleButton = view.findViewById(R.id.product_sale_button);

        productName.setText(cursor.getString(cursor.getColumnIndex(BookStoreDbContract.BookEntry.PRODUCT_NAME)));
        long price = cursor.getInt(cursor.getColumnIndex(BookStoreDbContract.BookEntry.PRICE));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        productPrice.setText(numberFormat.format(price));
        final int quantity = cursor.getInt(cursor.getColumnIndex(BookStoreDbContract.BookEntry.QUANTITY));
        productQuantity.setText(String.valueOf(quantity));

        long id = cursor.getLong(cursor.getColumnIndex(BookStoreDbContract.BookEntry._ID));
        final Uri currentUri = ContentUris.withAppendedId(BookStoreDbContract.BookEntry.CONTENT_URI, id);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(BookStoreDbContract.BookEntry.QUANTITY, quantity - 1);
                if (quantity == 0)
                    Toast.makeText(context, context.getString(R.string.sale_forbiden), Toast.LENGTH_SHORT).show();
                else
                    context.getContentResolver().update(currentUri, contentValues, null, null);
            }
        });
    }
}