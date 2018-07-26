package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
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

        public static final String CONTENT_AUTHORITY = "com.example.android.books";
        public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
        public static final String BOOK_PATH = "books";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,BOOK_PATH);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + BASE_URI + "/" + BOOK_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + BASE_URI + "/" + BOOK_PATH;
    }
}
