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

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesRecyclerviewAdapter extends RecyclerView.Adapter<FavoritesRecyclerviewAdapter.ViewHolder> {

    private Context mContext;
    private LiveData<List<Episode>> mEpisodes;

    // Constructor
    public FavoritesRecyclerviewAdapter(LiveData<List<Episode>> episodes, Context mContext) {
        this.mContext = mContext;
        mEpisodes = episodes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_episode_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesRecyclerviewAdapter.ViewHolder holder, int position) {

        final Episode currentEpisode = Objects.requireNonNull(mEpisodes.getValue()).get(position);

        // Using saved drawable instead of imageUrl for highRes
        Picasso.get().load(R.drawable.fragmented_image).fit()
                .into(holder.imageView);

        holder.sourceNameTextview.setText(mContext.getString(R.string.label_fragmented_podcast));
        holder.titleTextview.setText(currentEpisode.title);

        // Sets the click listener to open the episode player activity
        holder.constraintLayout.setOnClickListener(v -> {
            Intent goToPlayerIntent = new Intent(mContext, EpisodePlayerActivity.class);

            goToPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putParcelable("episode", Parcels.wrap(currentEpisode));
            goToPlayerIntent.putExtra("bundle", bundle);
            mContext.startActivity(goToPlayerIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mEpisodes.getValue().size();
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
