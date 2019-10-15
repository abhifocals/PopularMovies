package com.focals.popularmovies;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MovieDao {

    @Insert
    void insertMovie (MovieInDb movie);

    @Query("SELECT * FROM movie where movieId = :movieId")
    MovieInDb getMovieById (int movieId);

//    @Query("SELECT * FROM movie")
//    void getMovies();
}
