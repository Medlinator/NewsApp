package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of reviews by using an AsyncTask to perform the network request to the given URL.
 */
public class ReviewLoader extends AsyncTaskLoader<List<Review>> {

    /** Tag for log messages. */
    private static final String LOG_TAG = ReviewLoader.class.getName();

    /** Query URL. */
    private String mUrl;

    /**
     * Constructs a new {@link ReviewLoader}.
     *
     * @param context of the activity.
     * @param url to load data from.
     */
    public ReviewLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() { forceLoad(); }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Review> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of reviews.
        List<Review> reviews = QueryUtils.fetchReviewData(mUrl);
        return reviews;
    }
}