package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class BookStoreDbContract {
    private BookStoreDbContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";
    public static final String PATH_BOOKS_BRIEF_QUERY = "books/briefing";

    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String PRODUCT_NAME = "product_name";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String SUPPLIER_NAME = "supplier_name";
        public static final String SUPPLIER_PHONE = "supplier_phone";

        public static final String DISTINCT_SUPPLIERS = "distinct_supplier";

        // URIs
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public static final Uri CONTENT_BRIEF_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS_BRIEF_QUERY);
        // URI's MIME type
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_BOOKS;
    }
}