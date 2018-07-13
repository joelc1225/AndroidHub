package com.joelcamargojr.androidhub.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.model.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import timber.log.Timber;

public class WatchRecyclerviewAdapter extends RecyclerView.Adapter<WatchRecyclerviewAdapter.ViewHolder> {

    private ArrayList<Article> articles;
    private Context context;

    // Constructor
    public WatchRecyclerviewAdapter(ArrayList<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Gets current Article and parses out info from it
        Article currentArticle = articles.get(position);
        String title = currentArticle.getTitle();
        String imageUrl = currentArticle.getImageUrl();

        // checks if string url is valid before trying to load into picasso
        if (!TextUtils.isEmpty(imageUrl)) {
            Timber.d("Has imageUrl: " + imageUrl);
            Picasso.get().load(imageUrl)
                    .into(holder.imageView);
        }

        holder.titleTextview.setText(title);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextview;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextview = itemView.findViewById(R.id.titleTv);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}