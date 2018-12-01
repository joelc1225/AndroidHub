package com.joelcamargojr.androidhub.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.activities.EpisodePlayerActivity;
import com.joelcamargojr.androidhub.model.Episode;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

public class MainRecyclerviewAdapter extends RecyclerView.Adapter<MainRecyclerviewAdapter.ViewHolder> {

    private Context mContext;
    private MutableLiveData<ArrayList<Episode>> mEpisodesArrayList;

    // Constructor
    public MainRecyclerviewAdapter(MutableLiveData<ArrayList<Episode>> episodes, Context mContext) {
        this.mContext = mContext;
        mEpisodesArrayList = episodes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_episode_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Episode currentEpisode = Objects.requireNonNull(mEpisodesArrayList.getValue()).get(position);

        // Using saved drawable instead of imageUrl for highRes
        Picasso.get().load(R.drawable.fragmented_image)
                .into(holder.imageView);

        holder.sourceNameTextview.setText(mContext.getString(R.string.label_fragmented_podcast));
        holder.titleTextview.setText(currentEpisode.title);

        // Sets the click listener to open the episode player activity
        holder.constraintLayout.setOnClickListener(v -> {
            Intent goToPlayerIntent = new Intent(mContext, EpisodePlayerActivity.class);

            // TODO ADDED FLAG TO FIX REVIEW REQUIREMENT
            goToPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putParcelable("episode", Parcels.wrap(currentEpisode));
            goToPlayerIntent.putExtra("bundle", bundle);
            mContext.startActivity(goToPlayerIntent);
        });
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(mEpisodesArrayList.getValue()).size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextview;
        ImageView imageView;
        TextView sourceNameTextview;
        ConstraintLayout constraintLayout;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextview = itemView.findViewById(R.id.titleTv);
            imageView = itemView.findViewById(R.id.imageView);
            sourceNameTextview = itemView.findViewById(R.id.source_name_textview);
            constraintLayout = itemView.findViewById(R.id.constraint_layout_article_listitem);
        }
    }
}