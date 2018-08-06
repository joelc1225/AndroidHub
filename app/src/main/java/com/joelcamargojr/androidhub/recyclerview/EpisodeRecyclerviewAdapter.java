package com.joelcamargojr.androidhub.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.Utils.DateUtils;
import com.joelcamargojr.androidhub.Utils.TimeUtils;
import com.joelcamargojr.androidhub.model.Episode;

import java.util.ArrayList;

import static com.joelcamargojr.androidhub.Utils.StringUtils.stripHtml;

public class EpisodeRecyclerviewAdapter extends RecyclerView.Adapter<EpisodeRecyclerviewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Episode> episodes;

    public EpisodeRecyclerviewAdapter(Context context, ArrayList<Episode> episodes) {
        this.context = context;
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.episode_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gets current Episode item
        Episode currentEpisode = episodes.get(position);

        // gets the date and audio length to convert before populating to views
        long dateLong = currentEpisode.date;
        int audioLength = currentEpisode.audio_length;
        String dateString = DateUtils.convertLongDateToString(dateLong);
        String audioLengthString = TimeUtils.secondsToMinutes(audioLength);

        holder.titleTV.setText(currentEpisode.title);
        holder.descriptionTV.setText(stripHtml(currentEpisode.description));
        holder.dateTV.setText(dateString);
        holder.lengthTV.setText(audioLengthString);
        holder.progressBar.setProgress(20);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        TextView descriptionTV;
        TextView dateTV;
        TextView lengthTV;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.episode_title_TV);
            descriptionTV = itemView.findViewById(R.id.episode_descrip_TV);
            dateTV = itemView.findViewById(R.id.episode_date_TV);
            lengthTV = itemView.findViewById(R.id.episode_length_TV);
            progressBar = itemView.findViewById(R.id.episode_progressBar);

        }
    }

}
