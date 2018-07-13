package com.joelcamargojr.androidhub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joelcamargojr.androidhub.model.Article;
import com.joelcamargojr.androidhub.recyclerview.ListenRecyclerviewAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;

import timber.log.Timber;

public class ListenFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gets arguments for fragment
        Bundle bundle = getArguments();

        // inflates view
        View view = inflater.inflate(R.layout.listen_fragment, container, false);

        // Checks if arguments passed in are valid before setup
        if (bundle != null) {
            Timber.d("BUNDLE NOT NULL!");
            ArrayList<Article> articles = Parcels.unwrap(bundle.getParcelable("articleList3"));
            RecyclerView recyclerView = view.findViewById(R.id.recy_listen_frag);
            ListenRecyclerviewAdapter adapter = new ListenRecyclerviewAdapter(articles, getContext());
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
        } else {
            Timber.d("BUNDLE IS NULL!!!!!");
        }


        return view;


    }
}
