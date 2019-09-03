package com.focals.popularmovies;

import android.widget.ImageView;

public class Movie {

    String title;
    String poster_path;
    String plotSynopsis;
    String releaseDate;


    public Movie(String title, String poster_path, String plotSynopsis, String releaseDate) {
        this.title = title;
        this.poster_path = poster_path;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
    }
}
