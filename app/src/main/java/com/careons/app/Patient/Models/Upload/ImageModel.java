package com.careons.app.Patient.Models.Upload;

import android.graphics.drawable.Drawable;

public class ImageModel {

    private Drawable drawable;

    public ImageModel() {
        // Default Constructor
    }

    public ImageModel(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
