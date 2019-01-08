package com.example.ricoramars.recipeportfolioapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;


public class RecipeProvider extends ContentProvider
{
    // database
    private DBHelper dbHelper;

    public static final int REMINDERS = 100;
    public static final int REMINDERS_ID = 101;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher()
    {
        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
/*
 All paths added to the UriMatcher have a corresponding int.
 For each kind of uri you may want to access, add the corresponding match with addURI.
 The two calls below add matches for the task directory and a single item by ID.
*/
        sURIMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RecipeEntry.TABLE_NAME, REMINDERS);
        sURIMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RecipeEntry.TABLE_NAME + "/#", REMINDERS_ID);
        return sURIMatcher;
    }


    @Override
    public boolean onCreate()
    {
        this.dbHelper = new DBHelper(getContext());
        Init();
        return true;
    }

    private void Init()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_NAME);
        dbHelper.onCreate(db);

        Context context = getContext();

        //Add default items.
        if (this.count() == 0)
        {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.hamburger);
            this.save(new Recipe(0, context.getResources().getString(R.string.recipe_hamburger_name), context.getResources().getString(R.string.recipe_hamburger_description), image));
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.turkey);
            this.save(new Recipe(0, context.getResources().getString(R.string.recipe_turkey_name), context.getResources().getString(R.string.recipe_turkey_description), image));
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.chicken);
            this.save(new Recipe(0, context.getResources().getString(R.string.recipe_chicken_name), context.getResources().getString(R.string.recipe_chicken_description), image));
        }

        db.close();
    }

    private int count() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + RecipeContract.RecipeEntry.TABLE_NAME, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();

        return count;
    }
    private void save(Recipe recipe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RecipeContract.RecipeEntry.COLUMN_NAME_NAME, recipe.getName());
        values.put(RecipeContract.RecipeEntry.COLUMN_NAME_DESCRIPTION, recipe.getDescription());
        values.put(RecipeContract.RecipeEntry.COLUMN_IMAGE, getBitmapAsByteArray(recipe.getImage()));

        // Inserting Row
        db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
        db.close();
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 12, outputStream);return outputStream.toByteArray();
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = this.dbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sURIMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case REMINDERS:
                retCursor =  db.query(RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REMINDERS_ID:
                // URI: content://<authority>/reminders/#
                String id = uri.getPathSegments().get(1);

                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                retCursor =  db.query(RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sURIMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case REMINDERS:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(RecipeContract.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sURIMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Delete a single row of data
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case REMINDERS_ID:
                // Get the task ID from the URI path
                // URI: content://<authority>/reminders/#
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }

    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        //Keep track of if an update occurs
        int tasksUpdated;

        // match code
        int match = sURIMatcher.match(uri);

        switch (match) {
            case REMINDERS_ID:
                //update a single task by getting the id
                // URI: content://<authority>/reminders/#
                String id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = dbHelper.getWritableDatabase().update(RecipeContract.RecipeEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }

}
