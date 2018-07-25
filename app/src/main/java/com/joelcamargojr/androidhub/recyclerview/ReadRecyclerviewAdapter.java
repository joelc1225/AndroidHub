package com.joelcamargojr.androidhub.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.model.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import timber.log.Timber;

public class ReadRecyclerviewAdapter extends RecyclerView.Adapter<ReadRecyclerviewAdapter.ViewHolder>
        implements View.OnClickListener {

    private ArrayList<Article> articles;
    private Context context;
    RecyclerView recyclerView;

    // Constructor
    public ReadRecyclerviewAdapter(ArrayList<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_item, parent, false);

        // instantiates our recyclerView and sets a clickListener on it
        recyclerView = parent.getRootView().findViewById(R.id.recy_read_frag);
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        // Gets current Article and parses out info from it
        Article currentArticle = articles.get(position);
        final String title = currentArticle.getTitle();
        String imageUrl = currentArticle.getImageUrl();

        // checks if string url is valid before trying to load into picasso
        if (!TextUtils.isEmpty(imageUrl)) {
            Timber.d("Has imageUrl: " + imageUrl);
            Picasso.get().load(imageUrl)
                    .into(holder.imageView);
        }

        holder.titleTextview.setText(title);

        for (int i = 0; i < articles.size(); i++) {
            ViewCompat.setTransitionName(holder.imageView, title + i);
        }

        // sets a click listener on the source name if the user wants to explore the specific source
        holder.sourceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Source Title" + position, Toast.LENGTH_SHORT ).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    // Defines onClick for each list item in our recyclerView
    @Override
    public void onClick(View v) {
        int itemPosition = recyclerView.getChildLayoutPosition(v);
        Article item = articles.get(itemPosition);
        Toast.makeText(context, item.getTitle(), Toast.LENGTH_SHORT).show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextview;
        public ImageView imageView;
        public RecyclerView recyclerView;
        public TextView sourceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextview = itemView.findViewById(R.id.titleTv);
            imageView = itemView.findViewById(R.id.imageView);
            recyclerView = itemView.findViewById(R.id.recy_read_frag);
            sourceTextView = itemView.findViewById(R.id.source_name_textview);
        }
    }
}