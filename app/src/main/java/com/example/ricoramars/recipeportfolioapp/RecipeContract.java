package com.example.ricoramars.recipeportfolioapp;

import android.net.Uri;
import android.provider.BaseColumns;

public final class RecipeContract {
    public static final String AUTHORITY = "com.example.konata.swipeviewcontentprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/" + RecipeContract.RecipeEntry.TABLE_NAME);

    private RecipeContract() {
    }

    public static class RecipeEntry implements BaseColumns {
        public static final String TABLE_NAME = "Recipes";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";
    }

}