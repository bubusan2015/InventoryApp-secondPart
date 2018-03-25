package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.BookStoreDbContract;
import com.example.android.inventoryapp.Data.BookStoreDbHelper;

public class MainActivity extends AppCompatActivity {
    TextView bookItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookItems = findViewById(R.id.tv_book_items);
        insertBook();
        queryBooks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryBooks();
    }

    private void insertBook() {
        BookStoreDbHelper bookStoreDbHelper = new BookStoreDbHelper(this);
        SQLiteDatabase db = bookStoreDbHelper.getWritableDatabase();
        ContentValues newBookContentValues = new ContentValues();
        newBookContentValues.put(BookStoreDbContract.BookEntry.PRODUCT_NAME, "Shogun");
        newBookContentValues.put(BookStoreDbContract.BookEntry.PRICE, 29);
        newBookContentValues.put(BookStoreDbContract.BookEntry.QUANTITY, 100);
        newBookContentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_NAME, "Theora LTD");
        newBookContentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_PHONE, "00400728189854");
        long result = db.insert(BookStoreDbContract.BookEntry.TABLE_NAME, null, newBookContentValues);
        if (result == -1)
            Toast.makeText(this, getString(R.string.error_add_book), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, getString(R.string.book_added), Toast.LENGTH_SHORT).show();
        db.close();
        bookStoreDbHelper.close();
    }

    private void queryBooks() {
        BookStoreDbHelper bookStoreDbHelper = new BookStoreDbHelper(this);
        SQLiteDatabase db = bookStoreDbHelper.getReadableDatabase();
        String[] columnsProjection = new String[]{
                BookStoreDbContract.BookEntry._ID, BookStoreDbContract.BookEntry.PRODUCT_NAME, BookStoreDbContract.BookEntry.PRICE, BookStoreDbContract.BookEntry.QUANTITY,
                BookStoreDbContract.BookEntry.SUPPLIER_NAME, BookStoreDbContract.BookEntry.SUPPLIER_PHONE
        };
        Cursor booksCursor = db.query(BookStoreDbContract.BookEntry.TABLE_NAME, columnsProjection, null, null, null, null, BookStoreDbContract.BookEntry._ID + " ASC");
        try {
            bookItems.setText("Rows in " + BookStoreDbContract.BookEntry.TABLE_NAME + ": " + String.valueOf(booksCursor.getCount()) + "\n\n");
            bookItems.append(BookStoreDbContract.BookEntry._ID + " - "
                    + BookStoreDbContract.BookEntry.PRODUCT_NAME + " - "
                    + BookStoreDbContract.BookEntry.PRICE + " - "
                    + BookStoreDbContract.BookEntry.QUANTITY + " - "
                    + BookStoreDbContract.BookEntry.SUPPLIER_NAME + " - "
                    + BookStoreDbContract.BookEntry.SUPPLIER_PHONE + " - "
            );
            while (booksCursor.moveToNext()) {
                bookItems.append("\n" + String.valueOf(booksCursor.getInt(booksCursor.getColumnIndex(BookStoreDbContract.BookEntry._ID)))
                        + " - " + booksCursor.getString(booksCursor.getColumnIndex(BookStoreDbContract.BookEntry.PRODUCT_NAME))
                        + " - " + String.valueOf(booksCursor.getInt(booksCursor.getColumnIndex(BookStoreDbContract.BookEntry.PRICE)))
                        + " - " + String.valueOf(booksCursor.getInt(booksCursor.getColumnIndex(BookStoreDbContract.BookEntry.QUANTITY)))
                        + " - " + booksCursor.getString(booksCursor.getColumnIndex(BookStoreDbContract.BookEntry.SUPPLIER_NAME))
                        + " - " + booksCursor.getString(booksCursor.getColumnIndex(BookStoreDbContract.BookEntry.SUPPLIER_PHONE))
                );
            }

        } finally {
            db.close();
            bookStoreDbHelper.close();
        }
    }
}
