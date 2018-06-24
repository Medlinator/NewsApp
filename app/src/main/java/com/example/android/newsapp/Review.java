package com.example.android.newsapp;

/**
 * A {@link Review} object contains information related to a video game review by The Guardian.
 */
public class Review {

    /** Thumbnail for the review. */
    private String mThumbnail;

    /** Title of the review. */
    private String mTitle;

    /** Author of the review. */
    private String mAuthor;

    /** Date of the review. */
    private String mDate;

    /** Rating of the review. */
    private String mRating;

    /** Website URL for the review. */
    private String mUrl;

    /**
     * Constructs a new {@link Review} object.
     *
     * @param thumbnail is the thumbnail associated with the review.
     * @param title is the title of the review.
     * @param author is the author of the review.
     * @param date is the date the review was published.
     * @param rating is the rating given by the reviewer.
     * @param url is the website URL for the full review.
     */
    public Review(String thumbnail, String title, String author, String date, String rating, String url) {
        mThumbnail = thumbnail;
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mRating = rating;
        mUrl = url;
    }

    /**
     * Returns the thumbnail URL for the review.
     */
    public String getThumbnail() { return mThumbnail; }

    /**
     * Returns the title of the review.
     */
    public String getTitle() { return mTitle; }

    /**
     * Returns the author of the review.
     */
    public String getAuthor() { return mAuthor; }

    /**
     * Returns the date of the review.
     */
    public String getDate() { return mDate; }

    /**
     * Returns the rating of the review.
     */
    public String getRating() { return mRating; }

    /**
     * Returns the website URL for the full review.
     */
    public String getUrl() { return mUrl; }
}