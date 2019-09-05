package com.focals.popularmovies.comparators;

import android.widget.LinearLayout;

import com.focals.popularmovies.Movie;

import java.util.Comparator;

public class RatingComparator implements Comparator<Movie> {


    @Override
    public int compare(Movie o1, Movie o2) {
        if (Double.valueOf(o2.getRating()) < Double.valueOf(o1.getRating())) {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
