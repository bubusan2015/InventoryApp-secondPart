package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookStoreDbContract;
import com.example.android.inventoryapp.data.BookStoreDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.deal_fragment_placeholder, new BriefingFragment());
        transaction.replace(R.id.products_fragment_placeholder, new ProductsListFragment());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_insert_dummy:
                insertDummyData();
                return true;
            case R.id.main_menu_delete_all:
                deleteAllData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllData() {
        getContentResolver().delete(BookStoreDbContract.BookEntry.CONTENT_URI, null, null);
    }

    private void insertDummyData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookStoreDbContract.BookEntry.PRODUCT_NAME, "Shogun vol 1");
        contentValues.put(BookStoreDbContract.BookEntry.QUANTITY, 32);
        contentValues.put(BookStoreDbContract.BookEntry.PRICE, 5);
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_NAME, "Theora LTD");
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_PHONE, "0040722487555");
        getContentResolver().insert(BookStoreDbContract.BookEntry.CONTENT_URI, contentValues);
        contentValues.clear();

        contentValues.put(BookStoreDbContract.BookEntry.PRODUCT_NAME, "Shogun vol 2");
        contentValues.put(BookStoreDbContract.BookEntry.QUANTITY, 30);
        contentValues.put(BookStoreDbContract.BookEntry.PRICE, 5);
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_NAME, "Theora LTD");
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_PHONE, "0040722487555");
        getContentResolver().insert(BookStoreDbContract.BookEntry.CONTENT_URI, contentValues);
        contentValues.clear();

        contentValues.put(BookStoreDbContract.BookEntry.PRODUCT_NAME, "Digital Fortress");
        contentValues.put(BookStoreDbContract.BookEntry.QUANTITY, 100);
        contentValues.put(BookStoreDbContract.BookEntry.PRICE, 15);
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_NAME, "Theora LTD");
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_PHONE, "0040722487555");
        getContentResolver().insert(BookStoreDbContract.BookEntry.CONTENT_URI, contentValues);
        contentValues.clear();

        contentValues.put(BookStoreDbContract.BookEntry.PRODUCT_NAME, "The Hobbit");
        contentValues.put(BookStoreDbContract.BookEntry.QUANTITY, 50);
        contentValues.put(BookStoreDbContract.BookEntry.PRICE, 29);
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_NAME, "Theora LTD");
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_PHONE, "0040722487555");
        getContentResolver().insert(BookStoreDbContract.BookEntry.CONTENT_URI, contentValues);
        contentValues.clear();

        contentValues.put(BookStoreDbContract.BookEntry.PRODUCT_NAME, "The catcher in the rye");
        contentValues.put(BookStoreDbContract.BookEntry.QUANTITY, 40);
        contentValues.put(BookStoreDbContract.BookEntry.PRICE, 4);
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_NAME, "Theora LTD");
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_PHONE, "0040722487555");
        getContentResolver().insert(BookStoreDbContract.BookEntry.CONTENT_URI, contentValues);
        contentValues.clear();

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
        try {
            long result = db.insert(BookStoreDbContract.BookEntry.TABLE_NAME, null, newBookContentValues);
            if (result == -1)
                Toast.makeText(this, getString(R.string.error_add_book), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.book_added), Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        db.close();
        bookStoreDbHelper.close();
    }
}