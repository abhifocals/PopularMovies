package com.focals.popularmovies.room;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory;

public class MovieDetailViewModelFactory extends NewInstanceFactory {

    private final MovieDatabase movieDatabase;
    private final int movieId;

    public MovieDetailViewModelFactory(MovieDatabase movieDatabase, int movieId) {
        this.movieDatabase = movieDatabase;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieDetailViewModel(movieId, movieDatabase);
    }
}
