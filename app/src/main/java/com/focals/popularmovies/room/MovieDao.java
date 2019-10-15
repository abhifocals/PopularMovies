package com.focals.popularmovies.room;


import com.focals.popularmovies.Movie;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MovieDao {

    @Insert
    void insertMovie (Movie movie);

    @Query("SELECT * FROM Movie where movieId = :movieId")
    Movie getMovieById (int movieId);

    @Query("SELECT * FROM movie")
    List<Movie> getMovies();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavoriteFlag(Movie movie);
}
