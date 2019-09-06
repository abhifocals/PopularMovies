package com.focals.popularmovies.comparators;

import com.focals.popularmovies.Movie;

import java.util.Comparator;

public class PopularityComparator implements Comparator<Movie> {

    @Override
    public int compare(Movie o1, Movie o2) {
        if (Double.valueOf(o2.getPopularity()) < Double.valueOf(o1.getPopularity())) {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
