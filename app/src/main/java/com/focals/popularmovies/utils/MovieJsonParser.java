package com.focals.popularmovies.utils;

import android.net.Uri;

import com.focals.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieJsonParser {

    public static ArrayList<Movie> buildMovieArray(String response) {
        ArrayList<Movie> listOfMovies = new ArrayList<>();

        try { // add a null check
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray("results");

            // Iterate through response and build Array of MovieObjects
            for (int i = 0; i < results.length(); i++) {
                JSONObject movieJson = results.getJSONObject(i);
                String title = movieJson.getString("title");
                String posterPath = movieJson.getString("poster_path");
                String plotSynopsis = movieJson.getString("overview");
                String releaseDate = movieJson.getString("release_date");
                double rating = Double.parseDouble(movieJson.getString("vote_average"));
                double popularity = Double.parseDouble(movieJson.getString("popularity"));
                int movieId = Integer.valueOf(movieJson.getString("id"));
                boolean favorite = false;

                Uri posterUri = NetworkUtils.buildPosterUri(posterPath);

                Movie movie = new Movie(title, posterUri.toString(), plotSynopsis, releaseDate, rating, popularity, favorite, movieId);

                if (!listOfMovies.contains(movie)) {
                    listOfMovies.add(movie);
                }
            }
            return listOfMovies;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}