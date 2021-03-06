package com.focals.popularmovies.database;


import com.focals.popularmovies.Movie;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovies(List<Movie> movies);

    @Query("SELECT * FROM Movie where movieId = :movieId")
    LiveData<Movie> getMovieByMovieId(int movieId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Query("SELECT * FROM movie where favorite = 1")
    LiveData<List<Movie>> getFavorites();

    @Query("SELECT * FROM movie ORDER By popularity DESC")
    LiveData<List<Movie>> getPopularMovies();

    @Query("SELECT * FROM movie ORDER By rating DESC")
    LiveData<List<Movie>> getTopRatedMovies();
}
