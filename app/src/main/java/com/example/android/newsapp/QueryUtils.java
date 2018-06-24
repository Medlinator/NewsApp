package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving review data from The Guardian.
 */
public class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query The Guardian data set and return a list of {@link Review} objects.
     */
    public static List<Review> fetchReviewData(String requestUrl) {

        // Create URL object.
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Review}s.
        List<Review> reviews = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Review}s.
        return reviews;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL.", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the review JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Review} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Review> extractFeatureFromJson(String reviewJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(reviewJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding reviews to.
        List<Review> reviews = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string.
            JSONObject baseJsonResponse = new JSONObject(reviewJSON);

            // Extract the JSONObject containing the results.
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or reviews).
            JSONArray reviewArray = response.getJSONArray("results");

            // For each review in the reviewArray, create an {@link Review} object.
            for (int i = 0; i < reviewArray.length(); i++) {

                // Get a single review at position i within the list of reviews.
                JSONObject currentReview = reviewArray.getJSONObject(i);

                // For a given review, extract the JSONObject associated with the
                // key called "fields", which holds the rating and thumbnail url
                // for that review.
                JSONObject fields = currentReview.getJSONObject("fields");

                // Extract the value for the key called "thumbnail".
                String thumbnail = fields.getString("thumbnail");

                // Extract the value for the key called "webTitle".
                String title = currentReview.getString("webTitle");

                // For a given review, extract the JSONObject associated with the
                // key called "tags", which holds the author's name for that review.
                JSONArray tags = currentReview.getJSONArray("tags");
                // If no author cited, default is "No author cited."
                String author = "No author cited.";
                // If there is an author cited assign it to the author variable.
                if (tags.length() >= 1) {
                    author = tags.getJSONObject(0).getString("webTitle");
                }

                // Extract the value for the key called "webPublicationDate".
                String date = currentReview.getString("webPublicationDate");

                // Extract the value for the key called "starRating".
                String rating = fields.getString("starRating");

                // Extract the value for the key called "webUrl".
                String url = currentReview.getString("webUrl");

                // Create a new {@link Review} object with the thumbnail, title, author,
                // date, rating, and url from the JSON response.
                Review review = new Review(thumbnail, title, author, date, rating, url);

                // Add the new {@link Review} to the list of reviews.
                reviews.add(review);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the review JSON results", e);
        }

        // Return the list of reviews.
        Log.e("QueryUtils", "Successful");
        for(int i=0;i<reviews.size();i++){
            System.out.println(reviews.get(i).getThumbnail());
            System.out.println(reviews.get(i).getTitle());
            System.out.println(reviews.get(i).getAuthor());
            System.out.println(reviews.get(i).getDate());
            System.out.println(reviews.get(i).getRating());
            System.out.println(reviews.get(i).getUrl());
        }
        return reviews;
    }
}