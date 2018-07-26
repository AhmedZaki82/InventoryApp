package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSuppNameEditText;
    private EditText mSuppPhoneEditText;
    private Button mPlusButton;
    private Button mMinusButton;
    private Button mDeleteButton;
    private TextView mRequired;
    private ImageButton mcall;
    private TextView getmRequiredText;
    private static final int EXISTING_Book_LOADER = 0;
    private Uri mCurrentBooktUri;
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            getmRequiredText.setVisibility(View.GONE);
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDeleteButton = (Button) findViewById(R.id.btn_delete);
        mcall = (ImageButton) findViewById(R.id.btn_call);
        final Intent intent = getIntent();

        mCurrentBooktUri = intent.getData();

        if (mCurrentBooktUri == null) {
            mDeleteButton.setVisibility(View.GONE);
            mcall.setVisibility(View.GONE);

            setTitle(getString(R.string.add_a_book));
        } else {

            setTitle(getString(R.string.edit_book));

            getLoaderManager().initLoader(EXISTING_Book_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edt_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edt_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edt_book_quantity);
        mSuppNameEditText = (EditText) findViewById(R.id.edt_supplier);
        mSuppPhoneEditText = (EditText) findViewById(R.id.edt_supplier_phone);
        mPlusButton = (Button) findViewById(R.id.btn_plus);
        mMinusButton = (Button) findViewById(R.id.btn_minus);
        mDeleteButton = (Button) findViewById(R.id.btn_delete);
        mRequired = (TextView) findViewById(R.id.txt_required);
        getmRequiredText = (TextView) findViewById(R.id.txt_required);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSuppNameEditText.setOnTouchListener(mTouchListener);
        mSuppPhoneEditText.setOnTouchListener(mTouchListener);

        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantityEditText.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);

                if (quantity > 0) {

                    quantity -= 1;

                    mQuantityEditText.setText(Integer.toString(quantity));
                } else {

                    Toast.makeText(getBaseContext(),getString(R.string.negative),Toast.LENGTH_LONG).show();

                }
            }
        });

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String quantityString = mQuantityEditText.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);

                quantity += 1;

                mQuantityEditText.setText(Integer.toString(quantity));

            }
        });

        mRequired.setVisibility(View.GONE);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        final String phone;
        mcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel",mSuppPhoneEditText.getText().toString(),null));
                startActivity(intent);
            }
        });

    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBooktUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBooktUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void saveBook() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        double price = Double.parseDouble(priceString);
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String suppNameString = mSuppNameEditText.getText().toString().trim();
        String phoneString = mSuppPhoneEditText.getText().toString().trim();
        long phone = Long.parseLong(phoneString);

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, suppNameString);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, phone);


        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBooktUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBooktUri, values, null, null);

            mDeleteButton.setVisibility(View.VISIBLE);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                String nameString = mNameEditText.getText().toString().trim();
                String priceString = mPriceEditText.getText().toString().trim();
                String quantityString = mQuantityEditText.getText().toString().trim();
                String suppNameString = mSuppNameEditText.getText().toString().trim();
                String phoneString = mSuppPhoneEditText.getText().toString().trim();

                if (TextUtils.isEmpty(nameString)   || TextUtils.isEmpty(priceString)
                        || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(suppNameString)
                        || TextUtils.isEmpty(phoneString)){
                    mRequired.setVisibility(View.VISIBLE);
                    Toast.makeText(this,getString(R.string.fill), Toast.LENGTH_LONG).show();
                }else {
                    saveBook();
                    finish();
                }

                return true;

            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this,mCurrentBooktUri,
                projection,
                null,
                null,
                null);    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int suppNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int suppPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String suppName = cursor.getString(suppNameColumnIndex);
            long suppPhone = cursor.getLong(suppPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSuppNameEditText.setText(suppName);
            mSuppPhoneEditText.setText(Long.toString(suppPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSuppNameEditText.setText("");
        mSuppPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
