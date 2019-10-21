package com.focals.popularmovies.room;

import android.app.Application;

import com.focals.popularmovies.Movie;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    LiveData<List<Movie>> popularMoviesData;
    LiveData<List<Movie>> topRatedMoviesData;
    LiveData<List<Movie>> favoriteMovieData;

    public MainViewModel(@NonNull Application application, LiveData<List<Movie>> getMoviesData) {
        super(application);

        MovieDatabase db = MovieDatabase.getInstance(application.getApplicationContext());

        this.popularMoviesData = db.movieDao().getPopularMovies();
        this.topRatedMoviesData = db.movieDao().getTopRatedMovies();
        this.favoriteMovieData = db.movieDao().getFavorites();
    }

    public LiveData<List<Movie>> getPopularMoviesData() {
        return popularMoviesData;
    }

    public LiveData<List<Movie>> getTopRatedMoviesData() {
        return topRatedMoviesData;
    }

    public LiveData<List<Movie>> getFavoriteMovieData() {
        return favoriteMovieData;
    }
}
