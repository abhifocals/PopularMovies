package com.focals.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focals.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetail extends AppCompatActivity {

    private Movie currentMovie;
    private RecyclerView trailersRecyclerView;
    private TrailersAdapter trailersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_movie);

        // Getting views
        TextView title = findViewById(R.id.title);
        ImageView thumbnail = findViewById(R.id.thumbnail);
        TextView rating = findViewById(R.id.rating);
        TextView releaseDate = findViewById(R.id.releaseDate);
        TextView plot = findViewById(R.id.plot);

        // Getting intent
        Intent intent = getIntent();
        currentMovie = intent.getParcelableExtra("movie");

        // Setting content in views
        if (currentMovie != null) {
            title.setText(currentMovie.title);
            Picasso.get().load(currentMovie.posterPath).into(thumbnail);
            releaseDate.setText(currentMovie.releaseDate);
            plot.setText(currentMovie.plotSynopsis);
            rating.setText(currentMovie.rating);
        }

        // Setting up Adapter for RecyclerView Trailers
        trailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTrailers);
        trailersAdapter = new TrailersAdapter();
        trailersRecyclerView.setAdapter(trailersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void addToFavorites(View view) {
        Toast toast = Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showReview(View view) {




        FetchMovieTrailersTask fetchMovieTrailersTask = new FetchMovieTrailersTask();
        fetchMovieTrailersTask.execute();

//        FetchMovieReviewTask reviewTask = new FetchMovieReviewTask();
//        reviewTask.execute();
    }

    class FetchMovieReviewTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            return NetworkUtils.getResponseFromUrl(NetworkUtils.getReviewUrl(currentMovie.getId()));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject review = new JSONObject(s);

                String content = new JSONArray(review.getString("results")).getJSONObject(0).getString("content");


                // Start Review Activity here
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                intent.putExtra("REVIEW", content);

                startActivity(intent);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class FetchMovieTrailersTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            return NetworkUtils.getResponseFromUrl(NetworkUtils.getTrailersUrl(currentMovie.getId()));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            List<String> keys = new ArrayList<>();
            List<String> videoUrls = new ArrayList<>();

            // Get keys from Response.
            try {
                JSONObject videos = new JSONObject(s);
                JSONArray results = videos.getJSONArray("results");

                for (int i=0; i<results.length(); i++) {

                    String key = results.getJSONObject(i).getString("key");

                    String videoUrl = "https://www.youtube.com/watch?v=" + key;

                    keys.add(key);

                    videoUrls.add(videoUrl);


                }

                System.out.println();


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}

