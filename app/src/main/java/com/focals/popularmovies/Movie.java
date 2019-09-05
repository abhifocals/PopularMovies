package com.focals.popularmovies;

import android.net.Uri;

public class Movie {

    String title;
    Uri posterUri;
    String plotSynopsis;
    String releaseDate;
    String rating;


    public Movie(String title, Uri posterUri, String plotSynopsis, String releaseDate, String rating) {
        this.title = title;
        this.posterUri = posterUri;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }
}
