package com.focals.popularmovies;

import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie")
public class Movie {

    String title;
    String posterPath;
    String plotSynopsis;
    String releaseDate;
    double rating;
    private double popularity;

    @PrimaryKey()
    private int movieId;
    private boolean favorite;
    private String review;
    private List<String> trailers;
    private int id;


    @Ignore
    public Movie(String title, String posterPath, String plotSynopsis, String releaseDate, double rating, double popularity, boolean favorite, int movieId) {
        this.title = title;
        this.posterPath = posterPath;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
        this.movieId = movieId;
        this.favorite = favorite;
    }

    public Movie(String title, String posterPath, String plotSynopsis, String releaseDate, double rating, double popularity, boolean favorite, int movieId, int id) {
        this.title = title;
        this.posterPath = posterPath;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
        this.favorite = favorite;
        this.movieId = movieId;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getId() {
        return id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getReview() {
        return review;
    }

    public List<String> getTrailers() {
        return trailers;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setTrailers(List<String> trailers) {
        this.trailers = trailers;
    }
}
