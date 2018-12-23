package com.example.ricoramars.recipeportfolioapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class RecipeFragment extends Fragment
{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_RECIPE_TITLE = "ARG_RECIPE_TITLE";
    private static final String ARG_RECIPE_INSTRUCTION = "ARG_RECIPE_INSTRUCTION";
    private static final String ARG_RECIPE_IMAGE = "ARG_RECIPE_IMAGE";

    public RecipeFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecipeFragment newInstance(String recipeName, String recipeInstruction, Bitmap b) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();

        args.putString(ARG_RECIPE_TITLE, recipeName);
        args.putString(ARG_RECIPE_INSTRUCTION, recipeInstruction);
        args.putByteArray(ARG_RECIPE_IMAGE, getBitmapAsByteArray(b));

        fragment.setArguments(args);
        return fragment;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 12, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        TextView textView = rootView.findViewById(R.id.recipe_name);
        TextView textView2 = rootView.findViewById(R.id.recipe_instruction);
        ImageView imageView = rootView.findViewById(R.id.recipe_image);

        textView.setText( getArguments().getString(ARG_RECIPE_TITLE) );
        textView2.setText( getArguments().getString(ARG_RECIPE_INSTRUCTION) );


        byte[] imgByte = getArguments().getByteArray(ARG_RECIPE_IMAGE);
        Bitmap b = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        imageView.setImageBitmap( b );

        return rootView;
    }
}
