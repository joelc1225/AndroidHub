package com.joelcamargojr.androidhub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joelcamargojr.androidhub.model.Article;

import org.parceler.Parcels;

import java.util.ArrayList;

import timber.log.Timber;

public class ReadFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Timber.d("BUNDLE NOT NULL!");
            ArrayList<Article> articles = Parcels.unwrap(bundle.getParcelable("articleList"));
        }
        return inflater.inflate(R.layout.read_fragment, container, false);
    }
}
