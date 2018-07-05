package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Tsultrim on 7/4/18.
 */

public class BookContract {

    private BookContract() {}


    public static final class BookEntry implements BaseColumns {

        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_BOOK_NAME ="Product";

        public final static String COLUMN_BOOK_PRICE = "Price";

        public final static String COLUMN_QUANTITY = "Quantity";

        public final static String COLUMN_SUPPLIER_NAME = "Supplier";

        public final static String COLUMN_SUPPLIER_PHONE = "Phone";

    }
}
