package com.focals.popularmovies;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MovieDao {

    @Insert
    void insertMovie (Movie movie);

    @Query("SELECT * FROM Movie where movieId = :movieId")
    Movie getMovieById (int movieId);

//    @Query("SELECT * FROM movie")
//    void getMovies();
}
