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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.OnClickHandler, View.OnClickListener {

    private LiveData<Movie> currentMovieData;
    private Movie currentMovie;
    private RecyclerView trailersRecyclerView;
    private TrailersAdapter trailersAdapter;
    private List<String> trailerUrls;
    private String loadedData;
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

        final int movieId = intent.getIntExtra("MOVIE_ID", 0);


        currentMovieData = movieDao.getMovieByMovieId(movieId);

        currentMovieData.observe(MovieDetailActivity.this, new Observer<Movie>() {
            @Override
            public void onChanged(Movie movie) {
                currentMovie = movie;

                title.setText(movie.title);
                Picasso.get().load(movie.posterPath).into(thumbnail);
                releaseDate.setText(movie.releaseDate);
                plot.setText(movie.plotSynopsis);
                rating.setText(String.valueOf(movie.rating));

                if (currentMovie.isFavorite()) {
                    favoriteButton.setText("Remove from Favorite");
                } else {
                    favoriteButton.setText("Mark as Favorite");
                }
            }
        });

        // Get Trailer Urls
//        FetchMovieTrailersTask fetchMovieTrailersTask = new FetchMovieTrailersTask();
//        fetchMovieTrailersTask.execute();

        // Set the listener
        favoriteButton.setOnClickListener(this);


//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Setting content in views
//                        if (currentMovieData != null) {
//                            title.setText(currentMovieData.title);
//                            Picasso.get().load(currentMovieData.posterPath).into(thumbnail);
//                            releaseDate.setText(currentMovieData.releaseDate);
//                            plot.setText(currentMovieData.plotSynopsis);
//                            rating.setText(String.valueOf(currentMovieData.rating));
//                        }
//
//                        if (currentMovieData.isFavorite()) {
//                            favoriteButton.setText("Remove from Favorite");
//                        }
//
//
//
//                    }
//                });


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

        currentMovie.setFavorite(true);
        favoriteButton.setText("Remove from Favorite");

        movieDao.updateMovie(currentMovie);
    }

    public void removeFromFavorites(View view) {
        Toast toast = Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT);
        toast.show();

        currentMovie.setFavorite(false);
        favoriteButton.setText("Mark as Favorite");

        movieDao.updateMovie(currentMovie);
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

