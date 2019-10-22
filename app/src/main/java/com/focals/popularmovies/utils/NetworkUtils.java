package com.focals.popularmovies.utils;

import android.net.Uri;

import com.focals.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private final static String API_KEY = BuildConfig.MOVIE_DB_API_TOKEN;
    private final static String POPULAR_MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private final static String TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private final static String POSTER_BASE_URL = "https://image.tmdb.org/t/p";

    private static URL buildUrl(String baseUrl) {
        URL url = null;

        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US")
                .build();

        try {
            url = new URL(uri.toString());

            System.out.println();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromUrl(URL url) {
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream in = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return null;
    }

    public static URL getPopularMoviesURL() {
        return buildUrl(POPULAR_MOVIES_BASE_URL);
    }

    public static URL getTopRatedMoviesURL() {
        return buildUrl(TOP_RATED_BASE_URL);
    }

    public static URL getReviewUrl(int id) {
        return buildUrl("https://api.themoviedb.org/3/movie/" + id + "/reviews");
    }

    public static URL getTrailersUrl(int id) {
        return buildUrl("https://api.themoviedb.org/3/movie/" + id + "/videos");
    }

    public static Uri buildPosterUri(String posterPath) {
        return Uri.parse(POSTER_BASE_URL).buildUpon().appendEncodedPath("w500").appendEncodedPath(posterPath.replace("/", "")).build();
    }
}
