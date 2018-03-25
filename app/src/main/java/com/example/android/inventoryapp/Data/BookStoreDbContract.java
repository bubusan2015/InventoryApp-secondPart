package com.example.android.inventoryapp.Data;

import android.provider.BaseColumns;

public class BookStoreDbContract {
    private BookStoreDbContract() {    }

    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String PRODUCT_NAME = "product_name";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String SUPPLIER_NAME = "supplier_name";
        public static final String SUPPLIER_PHONE = "supplier_phone";
    }
}
