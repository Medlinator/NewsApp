package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Review>> {

    /** URL for review data from The Guardian. */
    private static final String THE_GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search";

    /**
     * Constant value for the review loader ID.
     */
    private static final int REVIEW_LOADER_ID = 1;

    /** Adapter for the list of reviews. */
    private ReviewAdapter mAdapter;

    /** TextView that is displayed when the list is empty. */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_activity);

        // Find a reference to the {@link ListView} in the layout.
        ListView reviewListView = findViewById(R.id.list);

        mAdapter = new ReviewAdapter(this, new ArrayList<Review>());

        // Set the adapter on the {@link ListView} so the list can be populated in the user
        // interface.
        reviewListView.setAdapter(mAdapter);

        // Set the adapter on the {@link ListView} so the list can be populated in the user
        // interface.
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current review that was clicked on.
                Review currentReview = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor).
                Uri reviewUri = Uri.parse((currentReview.getUrl()));

                // Create a new intent to view the review URI.
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, reviewUri);

                // Send the intent to launch a new activity.
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }
            }
        });

        mEmptyStateTextView = findViewById(R.id.empty_view);
        reviewListView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the ConnectivityManager to check state of network connectivity.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network.
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data.
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface.
            loaderManager.initLoader(REVIEW_LOADER_ID, null, this);
        } else {
            // Otherwise, display error. First, hide loading indicator so error message will be
            // visible.
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message.
            mEmptyStateTextView.setText((getString(R.string.no_internet_connection)));
        }
    }

    @Override
    public Loader<List<Review>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minRating = sharedPrefs.getString(
                getString(R.string.settings_minimum_rating_key),
                getString(R.string.settings_minimum_rating_default)
        );

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(THE_GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("q", "games");
        uriBuilder.appendQueryParameter("section", "games");
        uriBuilder.appendQueryParameter("tag", "tone/reviews");
        uriBuilder.appendQueryParameter("star-rating", minRating);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,starRating");
        uriBuilder.appendQueryParameter("page", "1");
        uriBuilder.appendQueryParameter("page-size", "20");
        uriBuilder.appendQueryParameter("api-key", "f625187f-4a30-47c0-bf6a-a6f1d12dc4c5");
        uriBuilder.appendQueryParameter("order-by", orderBy);

        // Return the completed uri `https://content.guardianapis.com/search?q=games&section=games&tag=tone/reviews&star-rating=1|2|3|4|5&show-tags=contributor&show-fields=thumbnail,starRating&page=1&page-size=20&api-key=f625187f-4a30-47c0-bf6a-a6f1d12dc4c5&order-by=newest
        return new ReviewLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
        // Hide loading indicator because the data has been loaded.
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No reviews found.".
        mEmptyStateTextView.setText(R.string.no_reviews);

        // Clear the adapter of previous review data.
        mAdapter.clear();

        // If there is a valid list of {@link Review}s, then add them to the adapter's data set.
        // This will trigger the ListView to update.
        if (reviews != null && !reviews.isEmpty()) {
            mAdapter.addAll(reviews);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}