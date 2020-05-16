package com.example.ebookdex.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.ebookdex.models.BookImageModels;
import com.example.ebookdex.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

public class BookImageLoaders extends AsyncTaskLoader<ArrayList<BookImageModels>>
{
    private String mUrl;

    public BookImageLoaders(@NonNull Context context, String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<BookImageModels> loadInBackground() {

        if(mUrl==null){
            return null;
        }
        ArrayList<BookImageModels> news= QueryUtils.fetchBookData(mUrl);
        return news;

    }
}