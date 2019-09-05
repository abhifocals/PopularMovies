package com.focals.popularmovies;

import android.net.Uri;

public class Movie {

    String title;
    Uri posterUri;
    String plotSynopsis;
    String releaseDate;

    public String getRating() {
        return rating;
    }

    String rating;

    public String getPopularity() {
        return popularity;
    }

    String popularity;


    public Movie(String title, Uri posterUri, String plotSynopsis, String releaseDate, String rating, String popularity) {
        this.title = title;
        this.posterUri = posterUri;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
    }
}
