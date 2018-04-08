package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.android.inventoryapp.data.BookStoreDbContract;

public class BookDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText bookTitleET;
    private EditText quantityET;
    private EditText priceET;
    private EditText supplierNameET;
    private EditText supplierPhoneET;
    private Button decreaseQuantityButton;
    private Button increaseQuantityButton;
    private Button orderButton;
    private Uri mDetailUri;
    private static final int GET_CURRENT_BOOK = 0;
    private boolean hasChanged;

    private View.OnTouchListener detectChangeOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hasChanged = true;
            return false;
        }
    };
    private View.OnClickListener decreaseQuantityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int quantity = new Integer(0);
            if (!TextUtils.isEmpty(quantityET.getText()))
                quantity = Integer.parseInt(quantityET.getText().toString());
            if (quantity == 0) {
                Toast.makeText(BookDetailActivity.this, getString(R.string.quantity_below_zero), Toast.LENGTH_SHORT).show();
                return;
            }
            quantity = quantity - 1;
            hasChanged = true;
            quantityET.setText(String.valueOf(quantity));
        }
    };
    private View.OnClickListener increaseQuantityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int quantity = new Integer(0);
            if (!TextUtils.isEmpty(quantityET.getText()))
                quantity = Integer.parseInt(quantityET.getText().toString());
            quantity = quantity + 1;
            hasChanged = true;
            quantityET.setText(String.valueOf(quantity));

        }
    };
    private View.OnClickListener orderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String phoneNumber = supplierPhoneET.getText().toString();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(BookDetailActivity.this, getString(R.string.enter_phone), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber.trim()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDetailUri == null) {
            MenuItem menuItem = menu.findItem(R.id.menu_detail_delete);
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail_save:
                saveBook();
                return true;
            case R.id.menu_detail_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!hasChanged) {
                    NavUtils.navigateUpFromSameTask(BookDetailActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(BookDetailActivity.this);
                    }
                };
                showUnsavedChangesDialog(clickListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        bookTitleET = findViewById(R.id.et_detail_title);
        quantityET = findViewById(R.id.et_detail_quantity);
        priceET = findViewById(R.id.et_detail_price);
        supplierNameET = findViewById(R.id.et_detail_supplier_name);
        supplierPhoneET = findViewById(R.id.et_detail_supplier_phone);
        decreaseQuantityButton = findViewById(R.id.bt_decrease_quantity);
        increaseQuantityButton = findViewById(R.id.bt_increase_quantity);
        orderButton = findViewById(R.id.bt_detail_order);


        bookTitleET.setOnTouchListener(detectChangeOnTouchListener);
        quantityET.setOnTouchListener(detectChangeOnTouchListener);
        priceET.setOnTouchListener(detectChangeOnTouchListener);
        supplierPhoneET.setOnTouchListener(detectChangeOnTouchListener);
        supplierNameET.setOnTouchListener(detectChangeOnTouchListener);
        decreaseQuantityButton.setOnClickListener(decreaseQuantityListener);
        increaseQuantityButton.setOnClickListener(increaseQuantityListener);
        orderButton.setOnClickListener(orderListener);

        mDetailUri = getIntent().getData();
        if (mDetailUri == null) {
            setTitle(getString(R.string.insert_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.update_book));
            getSupportLoaderManager().initLoader(GET_CURRENT_BOOK, null, this);
        }


    }

    @Override
    public void onBackPressed() {
        if (!hasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(clickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_save_alert));
        builder.setPositiveButton(R.string.yes, clickListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(getString(R.string.delete_book_confirmation));
        dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBook();
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void deleteBook() {
        int deletedRows = getContentResolver().delete(mDetailUri, null, null);
        if (deletedRows > 0)
            Toast.makeText(this, getString(R.string.book_deleted), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveBook() {
        String bookTitle = bookTitleET.getText().toString().trim();
        if (TextUtils.isEmpty(bookTitle)) {
            Toast.makeText(this, getString(R.string.mandatory_book_title), Toast.LENGTH_SHORT).show();
            bookTitleET.requestFocus();
            return;
        }
        Integer quantity = new Integer(0);
        if (!TextUtils.isEmpty(quantityET.getText()))
            quantity = Integer.parseInt(quantityET.getText().toString());
        Integer price = 0;
        if (!TextUtils.isEmpty(priceET.getText()))
            price = Integer.parseInt(priceET.getText().toString());
        String supplierName = supplierNameET.getText().toString().trim();
        if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(this, getString(R.string.mandatory_supplier_name), Toast.LENGTH_SHORT).show();
            supplierNameET.requestFocus();
            return;
        }
        String supplierPhone = supplierPhoneET.getText().toString().trim();
        if (TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, getString(R.string.mandatory_supplier_phone), Toast.LENGTH_SHORT).show();
            supplierPhoneET.requestFocus();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookStoreDbContract.BookEntry.PRODUCT_NAME, bookTitle);
        contentValues.put(BookStoreDbContract.BookEntry.QUANTITY, quantity);
        contentValues.put(BookStoreDbContract.BookEntry.PRICE, price);
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_NAME, supplierName);
        contentValues.put(BookStoreDbContract.BookEntry.SUPPLIER_PHONE, supplierPhone);
        if (mDetailUri == null) {
            try {
                Uri responseUri = getContentResolver().insert(BookStoreDbContract.BookEntry.CONTENT_URI, contentValues);
                if (responseUri == null)
                    Toast.makeText(this, getString(R.string.error_add_book), Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, getString(R.string.book_added), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                int rowsUpdated = getContentResolver().update(mDetailUri, contentValues, null, null);
                if (rowsUpdated == 0)
                    Toast.makeText(this, getString(R.string.error_update_book), Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, getString(R.string.book_updated), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case GET_CURRENT_BOOK:
                String[] sqlProjection = new String[]{BookStoreDbContract.BookEntry._ID, BookStoreDbContract.BookEntry.PRODUCT_NAME,
                        BookStoreDbContract.BookEntry.PRICE, BookStoreDbContract.BookEntry.QUANTITY, BookStoreDbContract.BookEntry.SUPPLIER_NAME,
                        BookStoreDbContract.BookEntry.SUPPLIER_PHONE};
                CursorLoader cursorLoader = new CursorLoader(BookDetailActivity.this, mDetailUri, sqlProjection, null, null, null);
                return cursorLoader;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case GET_CURRENT_BOOK:
                data.moveToFirst();
                int quantity = data.getInt(data.getColumnIndex(BookStoreDbContract.BookEntry.QUANTITY));
                String title = data.getString(data.getColumnIndex(BookStoreDbContract.BookEntry.PRODUCT_NAME));
                int price = data.getInt(data.getColumnIndex(BookStoreDbContract.BookEntry.PRICE));
                String supplierName = data.getString(data.getColumnIndex(BookStoreDbContract.BookEntry.SUPPLIER_NAME));
                String supplierPhone = data.getString(data.getColumnIndex(BookStoreDbContract.BookEntry.SUPPLIER_PHONE));

                bookTitleET.setText(title);
                priceET.setText(String.valueOf(price));
                quantityET.setText(String.valueOf(quantity));
                supplierNameET.setText(supplierName);
                supplierPhoneET.setText(supplierPhone);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case GET_CURRENT_BOOK:
                bookTitleET.setText("");
                priceET.setText("");
                quantityET.setText("");
                supplierPhoneET.setText("");
                supplierPhoneET.setText("");
                break;
            default:
                break;
        }
    }
}