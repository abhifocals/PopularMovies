package com.focals.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focals.popularmovies.room.MovieDao;
import com.focals.popularmovies.room.MovieDatabase;
import com.focals.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.OnClickHandler, View.OnClickListener {

    private Movie currentMovie;
    private RecyclerView trailersRecyclerView;
    private TrailersAdapter trailersAdapter;
    private List<String> trailerUrls;
    private int id;
    private TextView favoriteButton;

    MovieDatabase db;
    MovieDao movieDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_movie);

        // Getting views
        final TextView title = findViewById(R.id.title);
        final ImageView thumbnail = findViewById(R.id.thumbnail);
        final TextView rating = findViewById(R.id.rating);
        final TextView releaseDate = findViewById(R.id.releaseDate);
        final TextView plot = findViewById(R.id.plot);
        favoriteButton = (TextView) findViewById(R.id.favoriteButton);

        // Getting intent
        Intent intent = getIntent();

        // Room Get from DB
        db = MovieDatabase.getInstance(this);
        movieDao = db.movieDao();
        id = intent.getIntExtra("ID", 0);

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                currentMovie = movieDao.getMovieById(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Setting content in views
                        if (currentMovie != null) {
                            title.setText(currentMovie.title);
                            Picasso.get().load(currentMovie.posterPath).into(thumbnail);
                            releaseDate.setText(currentMovie.releaseDate);
                            plot.setText(currentMovie.plotSynopsis);
                            rating.setText(String.valueOf(currentMovie.rating));
                        }

                        // Get Trailer Urls
                        FetchMovieTrailersTask fetchMovieTrailersTask = new FetchMovieTrailersTask();
                        fetchMovieTrailersTask.execute();

                    }
                });

            }
        });

        // Set the listener
        favoriteButton.setOnClickListener(this);
    }

    private void setUpTrailersAdapter(List<String> trailerUrls) {
        trailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTrailers);
        trailersAdapter = new TrailersAdapter(trailerUrls, this);

        trailersRecyclerView.setAdapter(trailersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(linearLayoutManager);

        this.trailerUrls = trailerUrls;
    }

    public void addToFavorites(View view) {
        Toast toast = Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT);
        toast.show();

        // Room
        currentMovie.setFavorite(true);

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.updateMovie(currentMovie);
            }
        });

        // Change Text of Button
        favoriteButton.setText("Remove from Favorite");
    }

    public void removeFromFavorites(View view) {
        Toast toast = Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT);
        toast.show();

        // Room
        currentMovie.setFavorite(false);

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.updateMovie(currentMovie);
            }
        });

        // Change Text of Button
        favoriteButton.setText("Mark as Favorite");
    }

    public void showReview(View view) {
        FetchMovieReviewTask reviewTask = new FetchMovieReviewTask();
        reviewTask.execute();
    }

    public void onClick(int position) {
        String trailerUrl = trailerUrls.get(position);

        Uri uri = Uri.parse(trailerUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {

        Button favoriteButton = (Button) v;

        if (v != null) {

            if (((Button) v).getText().equals("Mark as Favorite")) {
                addToFavorites(v);
            } else {
                removeFromFavorites(v);
            }
        }

    }

    class FetchMovieReviewTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            return NetworkUtils.getResponseFromUrl(NetworkUtils.getReviewUrl(currentMovie.getMovieId()));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject review = new JSONObject(s);

                String content = new JSONArray(review.getString("results")).getJSONObject(0).getString("content");

                // Room
                currentMovie.setReview(content);

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
            return NetworkUtils.getResponseFromUrl(NetworkUtils.getTrailersUrl(currentMovie.getMovieId()));
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

                for (int i = 0; i < results.length(); i++) {

                    String key = results.getJSONObject(i).getString("key");

                    String videoUrl = "https://www.youtube.com/watch?v=" + key;

                    keys.add(key);

                    videoUrls.add(videoUrl);
                }

                System.out.println();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            setUpTrailersAdapter(videoUrls);

            // Room
            currentMovie.setTrailers(videoUrls);
        }
    }
}

