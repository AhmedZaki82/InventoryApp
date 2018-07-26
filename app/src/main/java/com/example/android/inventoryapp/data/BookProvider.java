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
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Tsultrim on 7/18/18.
 */

public class BookProvider extends ContentProvider {

    private static BookDbHelper mDbHelper;
    public static final int BOOK = 100;
    public static final int BOOK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    static {
        sUriMatcher.addURI(BookContract.BookEntry.CONTENT_AUTHORITY, BookContract.BookEntry.BOOK_PATH, BOOK);
        Log.i("BookProvider.class","two types of urimatcher" + sUriMatcher);
        sUriMatcher.addURI(BookContract.BookEntry.CONTENT_AUTHORITY, BookContract.BookEntry.BOOK_PATH +"/#", BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(BookContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case BOOK:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("unknown URI: "+uri +" with match "+match);
        }    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case BOOK:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert unknown URI: " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a name");
        }

        // Price
        // If the price is provided, check that it's greater than or equal to 0
        Double price = values.getAsDouble(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Book requires valid price");
        }

        //Quantity
        // Check that the quantity is greater than or equal to 0
        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Book requires valid quantity");
        }

        // Supplier
        // Check that the supplier is not null
        String supplier = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Book requires a supplier");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
// Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK:

                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
                case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link BookEntry#COLUMN_BOOK_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // Price
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_PRICE)) {
            // Check that the price is greater than or equal to 0
            Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        //Quantity
        if (values.containsKey(BookContract.BookEntry.COLUMN_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }

        // If the {@link BookEntry#COLUMN_SUPPLIER} key is present,
        // check that the supplier value is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Book requires a supplier");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
