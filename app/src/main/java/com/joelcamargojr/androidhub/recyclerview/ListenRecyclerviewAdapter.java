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
import com.joelcamargojr.androidhub.model.Episode;
import com.joelcamargojr.androidhub.model.Podcast;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

public class ListenRecyclerviewAdapter extends RecyclerView.Adapter<ListenRecyclerviewAdapter.ViewHolder> {

    private Podcast podcast;
    private Context context;
    private Episode currentEpisode;

    // Constructor
    public ListenRecyclerviewAdapter(Podcast podcast, Context context) {
        this.podcast = podcast;
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

        currentEpisode = podcast.episodeArrayList.get(position);
        String title = currentEpisode.title;
        String imageUrl = podcast.getImage();

        // checks if string url is valid before trying to load into picasso
        if (!TextUtils.isEmpty(imageUrl)) {
            Timber.d("Has imageUrl: " + imageUrl);
            Picasso.get().load(imageUrl)
                    .into(holder.imageView);
        }

        holder.sourceNameTextview.setText(podcast.title);
        holder.titleTextview.setText(currentEpisode.title);
    }

    @Override
    public int getItemCount() {
        return podcast.getEpisodeArrayList().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextview;
        public ImageView imageView;
        public TextView sourceNameTextview;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextview = itemView.findViewById(R.id.titleTv);
            imageView = itemView.findViewById(R.id.imageView);
            sourceNameTextview = itemView.findViewById(R.id.source_name_textview);
        }
    }
}