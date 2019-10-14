package com.focals.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    final String title;
    final String posterPath;
    final String plotSynopsis;
    final String releaseDate;
    final String rating;
    private final String popularity;
    final int id;

    public Movie(String title, String posterUri, String plotSynopsis, String releaseDate, String rating, String popularity, int id) {
        this.title = title;
        this.posterPath = posterUri;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
        this.id = id;
    }

    private Movie(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        plotSynopsis = in.readString();
        releaseDate = in.readString();
        rating = in.readString();
        popularity = in.readString();
        id = in.readInt();
    }

    public String getRating() {
        return rating;
    }

    public String getPopularity() {
        return popularity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(plotSynopsis);
        dest.writeString(releaseDate);
        dest.writeString(rating);
        dest.writeString(popularity);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
