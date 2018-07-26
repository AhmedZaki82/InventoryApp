package com.example.android.inventoryapp.data;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.BookContract.BookEntry;
/**
 * Created by Tsultrim on 7/4/18.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE + " LONG NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
