package com.example.android.inventoryapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class BookStoreProvider extends ContentProvider {

    private BookStoreDbHelper bookStoreDbHelper;
    private static final int BOOKS_MATCH = 100;
    private static final int BOOK_MATCH_FOR_ID = 101;
    private static final int BOOK_BRIEF_MATCH = 102;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(BookStoreDbContract.CONTENT_AUTHORITY, BookStoreDbContract.PATH_BOOKS, BOOKS_MATCH);
        uriMatcher.addURI(BookStoreDbContract.CONTENT_AUTHORITY, BookStoreDbContract.PATH_BOOKS + "/#", BOOK_MATCH_FOR_ID);
        uriMatcher.addURI(BookStoreDbContract.CONTENT_AUTHORITY, BookStoreDbContract.PATH_BOOKS_BRIEF_QUERY, BOOK_BRIEF_MATCH);
    }

    @Override
    public boolean onCreate() {
        bookStoreDbHelper = new BookStoreDbHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase db = bookStoreDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (match) {
            case BOOKS_MATCH:
                cursor = db.query(BookStoreDbContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case BOOK_MATCH_FOR_ID:
                long idValue = ContentUris.parseId(uri);
                String mySelection = BookStoreDbContract.BookEntry._ID + " =?";
                String[] mySelectionArgs = {String.valueOf(idValue)};
                cursor = db.query(BookStoreDbContract.BookEntry.TABLE_NAME, projection, mySelection, mySelectionArgs, null, null, null);
                break;
            case BOOK_BRIEF_MATCH:
                cursor = db.rawQuery("select 1 as " + BookStoreDbContract.BookEntry._ID + ",sum(" + BookStoreDbContract.BookEntry.QUANTITY + ") as " + BookStoreDbContract.BookEntry.QUANTITY + ", count(distinct " + BookStoreDbContract.BookEntry.SUPPLIER_NAME
                        + ") as " + BookStoreDbContract.BookEntry.DISTINCT_SUPPLIERS + " from " + BookStoreDbContract.BookEntry.TABLE_NAME, null);
                cursor.setNotificationUri(getContext().getContentResolver(), BookStoreDbContract.BookEntry.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unkown URI " + uri.toString());

        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS_MATCH:
                return BookStoreDbContract.BookEntry.CONTENT_DIR_TYPE;
            case BOOK_MATCH_FOR_ID:
                return BookStoreDbContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS_MATCH:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    public Uri insertBook(Uri uri, ContentValues values) throws IllegalArgumentException {
        if (values.containsKey(BookStoreDbContract.BookEntry.PRODUCT_NAME)) {
            String productName = values.getAsString(BookStoreDbContract.BookEntry.PRODUCT_NAME);
            if (productName == null || TextUtils.isEmpty(productName))
                throw new IllegalArgumentException("Product name is mandatory " + uri);
        } else {
            throw new IllegalArgumentException("Product name is mandatory" + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.PRICE)) {
            Integer productPrice = values.getAsInteger(BookStoreDbContract.BookEntry.PRICE);
            if (productPrice == null || productPrice < 0)
                throw new IllegalArgumentException("Price must be greater or equal to 0" + uri);
        } else {
            throw new IllegalArgumentException("Price is mandatory" + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.QUANTITY)) {
            Integer quantity = values.getAsInteger(BookStoreDbContract.BookEntry.QUANTITY);
            if (quantity == null || quantity < 0)
                throw new IllegalArgumentException("Price must be greater or equal to 0" + uri);
        } else {
            throw new IllegalArgumentException("Price is mandatory" + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookStoreDbContract.BookEntry.SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName))
                throw new IllegalArgumentException("Supplier name is mandatory " + uri);
        } else {
            throw new IllegalArgumentException("Supplier name is mandatory" + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(BookStoreDbContract.BookEntry.SUPPLIER_PHONE);
            if (TextUtils.isEmpty(supplierPhone))
                throw new IllegalArgumentException("Supplier phone is mandatory " + uri);
        } else {
            throw new IllegalArgumentException("Supplier phone is mandatory" + uri);
        }

        SQLiteDatabase db = bookStoreDbHelper.getWritableDatabase();
        long rowId = db.insert(BookStoreDbContract.BookEntry.TABLE_NAME, null, values);
        if (rowId > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase db = bookStoreDbHelper.getWritableDatabase();
        int deletedRows;
        switch (match) {
            case BOOKS_MATCH:
                deletedRows = db.delete(BookStoreDbContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                if (deletedRows > 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return deletedRows;
            case BOOK_MATCH_FOR_ID:
                selection = BookStoreDbContract.BookEntry._ID + " =? ";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = db.delete(BookStoreDbContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                if (deletedRows > 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return deletedRows;
            default:
                throw new IllegalArgumentException("Delete not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        if (values.containsKey(BookStoreDbContract.BookEntry.PRODUCT_NAME)) {
            String productName = values.getAsString(BookStoreDbContract.BookEntry.PRODUCT_NAME);
            if (productName == null || TextUtils.isEmpty(productName))
                throw new IllegalArgumentException("Product name is mandatory " + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.PRICE)) {
            Integer productPrice = values.getAsInteger(BookStoreDbContract.BookEntry.PRICE);
            if (productPrice == null || productPrice <= 0)
                throw new IllegalArgumentException("Price must be a positive value" + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.QUANTITY)) {
            Integer productQuantity = values.getAsInteger(BookStoreDbContract.BookEntry.QUANTITY);
            if (productQuantity == null || productQuantity < 0)
                throw new IllegalArgumentException("Quantity must be a positive value" + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.SUPPLIER_NAME)) {
            String productName = values.getAsString(BookStoreDbContract.BookEntry.SUPPLIER_NAME);
            if (TextUtils.isEmpty(productName))
                throw new IllegalArgumentException("Supplier name is mandatory " + uri);
        }

        if (values.containsKey(BookStoreDbContract.BookEntry.PRODUCT_NAME)) {
            String productName = values.getAsString(BookStoreDbContract.BookEntry.PRODUCT_NAME);
            if (TextUtils.isEmpty(productName))
                throw new IllegalArgumentException("Product name is mandatory " + uri);
        }
        String bookSelection;
        switch (match) {
            case BOOKS_MATCH:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_MATCH_FOR_ID:
                bookSelection = BookStoreDbContract.BookEntry._ID + " =? ";
                long bookId = ContentUris.parseId(uri);
                return updateBook(uri, values, bookSelection, new String[]{String.valueOf(bookId)});
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    public int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0)
            return 0;
        SQLiteDatabase db = bookStoreDbHelper.getWritableDatabase();
        int modifiedRows = db.update(BookStoreDbContract.BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (modifiedRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return modifiedRows;
    }
}
