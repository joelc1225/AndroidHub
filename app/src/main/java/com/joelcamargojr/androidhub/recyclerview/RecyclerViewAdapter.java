package com.joelcamargojr.androidhub.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joelcamargojr.androidhub.model.Article;
import com.joelcamargojr.androidhub.repository.FakeDataSource;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    List<Article> articleList = FakeDataSource.getFakeArticleList();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextview;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
