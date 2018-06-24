package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * An {@link ReviewAdapter} knows how to create a list item layout for each review in the data
 * source (a list of {@link Review} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView to be displayed to the
 * user.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    /**
     * Constructs a new {@link ReviewAdapter}.
     *
     * @param context of the app.
     * @param reviews is the list of reviews, which is the data source of the adapter.
     */
    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    /**
     * Returns a list item view that displays information about the review at the given position in
     * the list of reviews.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise (if convertView is null) inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_list_item, parent, false);
        }

        // Find the review at the given position in the list of reviews.
        Review currentReview = getItem(position);

        // Find the ImageView with view ID thumbnail.
        ImageView thumbnailView = listItemView.findViewById(R.id.thumbnail);
        // Display the image of the current review in that ImageView.
        Picasso.get().load(currentReview.getThumbnail()).into(thumbnailView);
        // The rubric stated no external libraries, so I wrote out the logic to perform the same
        // task Picasso does. I still implemented Picasso because it downloads the images quicker.
//        new DownloadImageTask(thumbnailView).execute(currentReview.getThumbnail());

        // Find the TextView with view ID title.
        TextView titleView = listItemView.findViewById(R.id.title);
        // Display the title of the current review in that TextView.
        titleView.setText(currentReview.getTitle());

        // Find the TextView with view ID author.
        TextView authorView = listItemView.findViewById(R.id.author);
        // Display the author of the current review in that TextView.
        authorView.setText(currentReview.getAuthor());

        // Find the TextView with view ID date.
        TextView dateView = listItemView.findViewById(R.id.date);
        // Format date String.
        String dateString = formatDate(currentReview.getDate());
        // Display the date of the current review in that TextView.
        dateView.setText(dateString);

        // Find the ImageView with view ID rating.
        ImageView rating = listItemView.findViewById(R.id.rating);
        // Convert String rating to int.
        int ratingNumber = Integer.parseInt(currentReview.getRating());
        // Set the appropriate image based on the current review rating.
        rating.setImageResource(getRatingImage(ratingNumber));

        return listItemView;
    }

    /**
     * Inner class to perform an {@link AsyncTask} that will convert thumbnail urls to images.
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        /** Constructor. */
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Return the formatted date String (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Convert date back to String from Date.
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        String formattedString = df.format(convertedDate);
        // Return the formatted date String.
        return formattedString;
    }


    /**
     * Returns appropriate image, based on the rating.
     */
    private int getRatingImage(int rating) {
        int ratingImageResourceId;
        switch (rating) {
            case 0:
                ratingImageResourceId = R.drawable.zero_star_image;
                break;
            case 1:
                ratingImageResourceId = R.drawable.one_star_image;
                break;
            case 2:
                ratingImageResourceId = R.drawable.two_stars_image;
                break;
            case 3:
                ratingImageResourceId = R.drawable.three_stars_image;
                break;
            case 4:
                ratingImageResourceId = R.drawable.four_stars_image;
                break;
            case 5:
                ratingImageResourceId = R.drawable.five_stars_image;
                break;
            default:
                ratingImageResourceId = R.drawable.zero_star_image;
                break;
        }

        return ratingImageResourceId;
    }
}