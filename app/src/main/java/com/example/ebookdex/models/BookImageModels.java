package com.example.ebookdex.models;

import android.graphics.Bitmap;

public class BookImageModels {

    private Bitmap ImageUrl;

    public BookImageModels(Bitmap imageUrl) {
        ImageUrl = imageUrl;
    }

    public Bitmap getImageUrl() {
        return ImageUrl;
    }
}
