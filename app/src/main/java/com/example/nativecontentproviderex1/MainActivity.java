package com.example.nativecontentproviderex1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_LOADER_ID = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAndRequestPermissionsIfNeeded();

        setContentView(R.layout.activity_main);

        FButton btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), ContentActivity.class);

                startActivityForResult(in, 103);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 103) {
            Log.d("MainActivity", "Tro ve tu Activity 2");
        }

    }

    private void checkAndRequestPermissionsIfNeeded() {
        String[] params = null;
        String readContacts = Manifest.permission.READ_CONTACTS;

        int hasReadContact = ActivityCompat.checkSelfPermission(this, readContacts);

        List<String> permissions = new ArrayList<>();


        if (hasReadContact != PackageManager.PERMISSION_GRANTED)
            permissions.add(readContacts);


        if (!permissions.isEmpty()) {
            params = permissions.toArray(new String[permissions.size()]);
        }

        if (params != null && params.length > 0) {
            ActivityCompat.requestPermissions(MainActivity.this, params, 102);
        } else {

            getSupportLoaderManager().initLoader(CONTACTS_LOADER_ID,
                    null,
                    this);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getSupportLoaderManager().initLoader(CONTACTS_LOADER_ID,
                    null,
                    this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.

        if (id == CONTACTS_LOADER_ID) {
            return contactsLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //The framework will take care of closing the
        // old cursor once we return.
        List<String> contacts = contactsFromCursor(cursor);

        Log.d("MainActivity2", contacts + "");

        //Log.i
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
    }

    private Loader<Cursor> contactsLoader() {
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI; // The content URI of the phone contacts

        String[] projection = {                                  // The columns to return for each row
                ContactsContract.Contacts.DISPLAY_NAME
        };

        String selection = null;                                 //Selection criteria
        String[] selectionArgs = {};                             //Selection criteria
        String sortOrder = null;                                 //The sort order for the returned rows

        return new CursorLoader(
                getApplicationContext(),
                contactsUri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    private List<String> contactsFromCursor(Cursor cursor) {
        List<String> contacts = new ArrayList<String>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //String sdt = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHONETIC_NAME));
                contacts.add(name);
            } while (cursor.moveToNext());
        }

        return contacts;
    }

}