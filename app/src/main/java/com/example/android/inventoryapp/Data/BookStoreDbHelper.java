package com.example.android.inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookStoreDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bookStore.db";
    private static final int DATABASE_VERSION = 1;
    public BookStoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE "+ BookStoreDbContract.BookEntry.TABLE_NAME + " ("+
                BookStoreDbContract.BookEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookStoreDbContract.BookEntry.PRODUCT_NAME + " TEXT NOT NULL, " +
                BookStoreDbContract.BookEntry.PRICE + " INTEGER, " +
                BookStoreDbContract.BookEntry.QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                BookStoreDbContract.BookEntry.SUPPLIER_NAME + " TEXT NOT NULL," +
                BookStoreDbContract.BookEntry.SUPPLIER_PHONE + " TEXT );";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DROP_BOOKS_TABLE ="DROP TABLE IF EXISTS "+ BookStoreDbContract.BookEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_BOOKS_TABLE);
        onCreate(db);
    }
}
