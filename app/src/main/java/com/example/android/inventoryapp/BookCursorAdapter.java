package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract;

/**
 * Created by Tsultrim on 7/20/18.
 */

public class BookCursorAdapter extends CursorAdapter {
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView bookName = (TextView) view.findViewById(R.id.book_name);
        TextView bookPrice = (TextView) view.findViewById(R.id.book_price);
        final TextView bookQuantity = (TextView) view.findViewById(R.id.book_quantity);
        Button sale = (Button) view.findViewById(R.id.sale);

        int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QUANTITY);

        final int rowId = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int bookNumbers = cursor.getInt(quantityColumnIndex);

        bookName.setText(name);
        bookPrice.setText(String.valueOf(price));
        bookQuantity.setText(String.valueOf(bookNumbers));

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = bookQuantity.getText().toString();

                int quantity = Integer.parseInt(text);

                if (quantity == 0) {
                    Toast.makeText(context, R.string.no_more_stock, Toast.LENGTH_SHORT).show();
                } else if (quantity > 0) {
                    quantity = quantity - 1;

                    String quantityString = Integer.toString(quantity);

                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_QUANTITY, quantityString);

                    Uri currentInventoryUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, rowId);

                    int rowsAffected = context.getContentResolver().update(currentInventoryUri, values, null, null);

                    if (rowsAffected != 0) {
                        /* update text view if database update is successful */
                        if (bookNumbers <= 1) {
                            bookQuantity.setText(quantity + "");
                        }
                        }
                     else {
                        Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
