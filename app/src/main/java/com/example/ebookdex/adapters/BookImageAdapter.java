package com.example.ebookdex.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebookdex.BookDetailsActivity;
import com.example.ebookdex.MainActivity;
import com.example.ebookdex.R;
import com.example.ebookdex.models.BookImageModels;

import java.util.ArrayList;

public class BookImageAdapter extends RecyclerView.Adapter<BookImageAdapter.ViewHolder> {
    private static final String TAG = "BookImageAdapter";

    private Context mContext;

    public BookImageAdapter(Context context) {
        mContext = context;
    }

    private ArrayList<BookImageModels> mBooksImage= new ArrayList<BookImageModels>();
    @NonNull
    @Override
    public BookImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item,parent,false);
       ViewHolder holder=new ViewHolder(view);
       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookImageAdapter.ViewHolder holder, int position) {

        Bitmap bitmap= mBooksImage.get(position).getImageUrl();
        Log.d(TAG, "onBindViewHolder: "+ mBooksImage.get(position).getImageUrl());
        holder.mImageView_1.setImageBitmap(bitmap);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(new Intent(mContext,BookDetailsActivity.class));

            }
        });



    }

    @Override
    public int getItemCount() {
        return mBooksImage.size();
    }

    public void addAll(ArrayList<BookImageModels> bookImageModels)
    {
        Log.d(TAG, "addAll: run");
        mBooksImage=bookImageModels;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView_1;
        CardView mCardView;






        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView_1=itemView.findViewById(R.id.image_1);
            mCardView=itemView.findViewById(R.id.cardview_id);



        }
    }
}
