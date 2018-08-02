package com.joelcamargojr.androidhub.data;

import com.joelcamargojr.androidhub.model.Podcast;

public interface PodcastDataSource {

    Podcast getFragmentedPodcast(String podcastId);
}
