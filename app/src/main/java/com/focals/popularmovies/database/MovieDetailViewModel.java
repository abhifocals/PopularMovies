package com.focals.popularmovies.database;

import com.focals.popularmovies.Movie;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MovieDetailViewModel extends ViewModel {

    private final LiveData<Movie> movie;

    public MovieDetailViewModel(int movieId, MovieDatabase db) {
        movie = db.movieDao().getMovieByMovieId(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
