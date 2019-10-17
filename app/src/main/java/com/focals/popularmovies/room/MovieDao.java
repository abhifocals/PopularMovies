package com.focals.popularmovies.room;


import com.focals.popularmovies.Movie;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MovieDao {

    @Insert
    void insertMovie (Movie movie);

    @Query("SELECT * FROM Movie where movieId = :movieId")
    Movie getMovieByMovieId(int movieId);

    @Query("SELECT * FROM movie where id = :id")
    Movie getMovieById(int id);

    @Query("SELECT * FROM movie")
    List<Movie> getMovies();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Query("SELECT * FROM movie where favorite = 1")
    LiveData<List<Movie>> getFavorites ();
}
