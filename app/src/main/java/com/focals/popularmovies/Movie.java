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
    String rating;
    private String popularity;
    private int movieId;
    private boolean favorite;
    private String reviewUrl;
//    private List<String> trailers;

    @PrimaryKey(autoGenerate = true)
    private int id;


    @Ignore
    public Movie(String title, String posterPath, String plotSynopsis, String releaseDate, String rating, String popularity, boolean favorite, int movieId) {
        this.title = title;
        this.posterPath = posterPath;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
        this.movieId = movieId;
        this.favorite = favorite;
    }

    public Movie(String title, String posterPath, String plotSynopsis, String releaseDate, String rating, String popularity, boolean favorite,  int movieId, int id) {
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

    public String getRating() {
        return rating;
    }

    public String getPopularity() {
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

    public String getReviewUrl() {
        return reviewUrl;
    }

//    public List<String> getTrailers() {
//        return trailers;
//    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

//    public void setTrailers(List<String> trailers) {
//        this.trailers = trailers;
//    }
}
