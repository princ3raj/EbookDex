package com.example.ebookdex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.LoaderManager;
import com.example.ebookdex.loaders.BookImageLoaders;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import com.example.ebookdex.adapters.BookImageAdapter;
import com.example.ebookdex.loaders.BookImageLoaders;
import com.example.ebookdex.models.BookImageModels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<BookImageModels>> {


    public static final String TAG = MainActivity.class.getName();

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;


    /**
     * URL for earthquake data from the USGS dataset
     */
    //  private static final String GOOGLE_BOOKS_REQUEST_URL =
    //         "https://www.googleapis.com/books/v1/volumes?q=harry+potter&maxResults=20";
   private String requesturl = "https://www.googleapis.com/books/v1/volumes?q=searchQuery&maxResults=20";


    private RecyclerView mImageRecyclerView;
    private RecyclerView.LayoutManager mImageLayoutManager;
    private BookImageAdapter mBookImageAdapter;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //Recyclerview setup

        mImageRecyclerView=(RecyclerView)findViewById(R.id.book_recycler_view);
        mImageRecyclerView.setHasFixedSize(true);
//        mImageLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mImageRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mBookImageAdapter=new BookImageAdapter(this);
        mImageRecyclerView.setAdapter(mBookImageAdapter);


        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        

    }


    @Override
    public Loader<ArrayList<BookImageModels>> onCreateLoader(int id, Bundle args) {

        Log.d(TAG, "onCreateLoader: runloader");
        return new BookImageLoaders(this,requesturl);
        

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BookImageModels>> loader, ArrayList<BookImageModels> data) {

        Log.d(TAG, "onLoadFinished: finishloader");
        mBookImageAdapter.addAll(data);
        mBookImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<BookImageModels>> loader) {

    }
}
