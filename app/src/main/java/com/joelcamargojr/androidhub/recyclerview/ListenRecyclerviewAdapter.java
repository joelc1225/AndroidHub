package com.joelcamargojr.androidhub.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.activities.EpisodePlayerActivity;
import com.joelcamargojr.androidhub.activities.PodcastDetailActivity;
import com.joelcamargojr.androidhub.model.Episode;
import com.joelcamargojr.androidhub.model.Podcast;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

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

        // Using saved drawable instead of imageUrl for highRes
        Picasso.get().load(R.drawable.fragmented_image)
                .into(holder.imageView);

        holder.sourceNameTextview.setText(podcast.title + " >");
        holder.titleTextview.setText(currentEpisode.title);

        // sets click different listeners on views
        holder.sourceNameTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSourcePodcast = new Intent(context, PodcastDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("podcast", Parcels.wrap(podcast));
                goToSourcePodcast.putExtra("bundle", bundle);
                context.startActivity(goToSourcePodcast);
            }
        });

        // Sets the click listener to open the episode player activity
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPlayerIntent = new Intent(context, EpisodePlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("episode", Parcels.wrap(currentEpisode));
                goToPlayerIntent.putExtra("bundle", bundle);
                context.startActivity(goToPlayerIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return podcast.episodeArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextview;
        public ImageView imageView;
        public TextView sourceNameTextview;
        public ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextview = itemView.findViewById(R.id.titleTv);
            imageView = itemView.findViewById(R.id.imageView);
            sourceNameTextview = itemView.findViewById(R.id.source_name_textview);
            constraintLayout = itemView.findViewById(R.id.constraint_layout_article_listitem);
        }
    }
}