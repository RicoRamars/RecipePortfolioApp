package com.example.ricoramars.recipeportfolioapp;

import android.graphics.Bitmap;

public class Recipe {
    private Integer mId;
    private String name, description;
    private Bitmap image;

    public Recipe(Integer mId, String name, String description, Bitmap image) {
        this.mId = mId;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
