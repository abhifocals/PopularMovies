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

import com.focals.popularmovies.database.MovieDao;
import com.focals.popularmovies.database.MovieDatabase;
import com.focals.popularmovies.database.MovieDetailViewModel;
import com.focals.popularmovies.database.MovieDetailViewModelFactory;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.OnClickHandler, View.OnClickListener {

    private Movie currentMovie;
    private List<String> trailerUrls;
    private TextView favoriteButton;
    public static final String REVIEW_TAG = "REVIEW";

    private MovieDao movieDao;

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
        favoriteButton = findViewById(R.id.favoriteButton);

        // Getting intent
        Intent intent = getIntent();
        final int movieId = intent.getIntExtra(MainActivity.MOVIE_ID, 0);

        // Initialize for database access
        MovieDatabase db = MovieDatabase.getInstance(this);
        movieDao = db.movieDao();
        MovieDetailViewModelFactory factory = new MovieDetailViewModelFactory(db, movieId);
        MovieDetailViewModel movieDetailViewModel = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);

        // Observe data
        movieDetailViewModel.getMovie().observe(MovieDetailActivity.this, new Observer<Movie>() {
            @Override
            public void onChanged(Movie movie) {
                currentMovie = movie;

                title.setText(movie.title);
                Picasso.get().load(movie.posterPath).error(R.drawable.placeholder).into(thumbnail);
                releaseDate.setText(movie.releaseDate);
                plot.setText(movie.plotSynopsis);
                rating.setText(String.valueOf(movie.rating));

                if (currentMovie.isFavorite()) {
                    favoriteButton.setText(getString(R.string.removeFavorite));
                } else {
                    favoriteButton.setText(getString(R.string.addFavorite));
                }

                // Get Trailer Urls
                FetchMovieTrailersTask fetchMovieTrailersTask = new FetchMovieTrailersTask();
                fetchMovieTrailersTask.execute();
            }
        });

        // Set the listener
        favoriteButton.setOnClickListener(this);
    }

    private void setUpTrailersAdapter(List<String> trailerUrls) {
        RecyclerView trailersRecyclerView = findViewById(R.id.recyclerViewTrailers);
        TrailersAdapter trailersAdapter = new TrailersAdapter(trailerUrls, this);

        trailersRecyclerView.setAdapter(trailersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(linearLayoutManager);

        this.trailerUrls = trailerUrls;
    }

    private void addToFavorites(View view) {
        Toast toast = Toast.makeText(this, getResources().getString(R.string.addToFavToast), Toast.LENGTH_SHORT);
        toast.show();

        currentMovie.setFavorite(true);
        favoriteButton.setText(getString(R.string.removeFavorite));

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.updateMovie(currentMovie);
            }
        });
    }

    private void removeFromFavorites() {
        Toast toast = Toast.makeText(this, getResources().getString(R.string.removeFromFavToast), Toast.LENGTH_SHORT);
        toast.show();

        currentMovie.setFavorite(false);
        favoriteButton.setText(getString(R.string.addFavorite));

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.updateMovie(currentMovie);
            }
        });
    }

    public void showReview(View view) {
        FetchMovieReviewTask reviewTask = new FetchMovieReviewTask();
        reviewTask.execute();
    }

    public void onClickTrailer(int position) {
        String trailerUrl = trailerUrls.get(position);

        Uri uri = Uri.parse(trailerUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {

        if (v != null) {

            if (((Button) v).getText().equals(getString(R.string.addFavorite))) {
                addToFavorites(v);
            } else {
                removeFromFavorites();
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

                // Disable the Review button if no review

                if (review.getString("results").equals("[]")) {
                    Toast.makeText(MovieDetailActivity.this, getString(R.string.noReviewMessage), Toast.LENGTH_SHORT).show();
                } else {

                    String content = new JSONArray(review.getString("results")).getJSONObject(0).getString("content");

                    // Room
                    currentMovie.setReview(content);

                    // Start Review Activity here
                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                    intent.putExtra(REVIEW_TAG, content);

                    startActivity(intent);
                }

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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setUpTrailersAdapter(videoUrls);

            // Room
            currentMovie.setTrailers(videoUrls);
        }
    }
}

