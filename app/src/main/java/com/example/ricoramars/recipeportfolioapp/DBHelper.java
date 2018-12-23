package com.example.ricoramars.recipeportfolioapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipe.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + RecipeContract.RecipeEntry.TABLE_NAME +
                    "(" +
                    RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + RecipeContract.RecipeEntry.COLUMN_NAME_NAME + " TEXT, "
                    + RecipeContract.RecipeEntry.COLUMN_IMAGE + " BLOB, "
                    + RecipeContract.RecipeEntry.COLUMN_NAME_DESCRIPTION + " TEXT )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
