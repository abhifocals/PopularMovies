package com.focals.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.focals.popularmovies.room.MovieDao;
import com.focals.popularmovies.room.MovieDatabase;
import com.focals.popularmovies.utils.MovieJsonParser;
import com.focals.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnClickHandler {

    private RecyclerView rv_main;
    private ArrayList<Movie> movieList;
    private ProgressBar progressBar;
    private FetchMovieData fetchTask;
    private Menu menu;
    MovieDatabase db;
    MovieDao movieDao;

    public static String LOADED_DATA_TAG;
    public static String LOADED_DATA_VALUE;
    public static final String MOVIE_ID = "MOVIE_ID";
    public static boolean POPULAR;
    private static boolean TOP_RATED;
    private static boolean FAVORITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Views
        rv_main = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.progressBar);

        // Get DB
        db = MovieDatabase.getInstance(this);
        movieDao = db.movieDao();

        // Get Popular Movies
        if (movieList == null) {
            fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getPopularMoviesURL());
            POPULAR = true;

            showProgressBar();
        } else {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // TODO ViewModel
            case R.id.sort_popular:
                fetchTask = new FetchMovieData();
                fetchTask.execute(NetworkUtils.getPopularMoviesURL());
                showProgressBar();

                // Disable this option, enable other
                item.setEnabled(false);
                menu.findItem(R.id.sort_rated).setEnabled(true);
                break;

            case R.id.sort_rated:
                fetchTask = new FetchMovieData();
                fetchTask.execute(NetworkUtils.getTopRatedMoviesURL());
                showProgressBar();

                // Disable this option, enable other
                item.setEnabled(false);
                menu.findItem(R.id.sort_popular).setEnabled(true);
                break;

            case R.id.sort_favorites:
                final LiveData<List<Movie>> movieLiveData = movieDao.getFavorites();

                movieLiveData.observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        movieList = new ArrayList<>(movies);
                    }
                });

                setUpAdapterAndLayoutManager();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(final int index) {
        final Intent intent = new Intent(this, MovieDetailActivity.class);

        // intent.putExtra(LOADED_DATA_TAG,LOADED_DATA_VALUE);

        final List<Movie> list;


        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final int movieId;

                if (POPULAR) {
                    movieId = movieDao.getPopularMovies().get(index).getMovieId();
                } else if (TOP_RATED) {
                    movieId = movieDao.getTopRatedMovies().get(index).getMovieId();
                } else if (FAVORITE) {
                    movieId = movieDao.getFavoriteMovies().get(index).getMovieId();
                } else {
                    movieId = 0;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        intent.putExtra(MOVIE_ID, movieId);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // TODO ViewModel
    }

    class FetchMovieData extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            return NetworkUtils.getResponseFromUrl(urls[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgressBar();

            if (s == null) {
                showError();
            } else {
                // Build Movie Objects from Response
                movieList = null;
                movieList = MovieJsonParser.buildMovieArray(s);

                // Room Insert into Database
                for (final Movie movie : movieList) {

                    AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            movieDao.insertMovie(movie);
                        }
                    });
                }

                // Attach Adapter and Layout Manager
                setUpAdapterAndLayoutManager();
            }
        }
    }

    private void setUpAdapterAndLayoutManager() {
        MainAdapter adapter = new MainAdapter(movieList.size(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        rv_main.setAdapter(adapter);
        rv_main.setHasFixedSize(true);
        rv_main.setLayoutManager(gridLayoutManager);

    }

    private void hideProgressBar() {
        rv_main.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        rv_main.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showError() {
        if (movieList == null) {
            progressBar.setVisibility(View.INVISIBLE);
            TextView error = findViewById(R.id.tv_error);
            error.setVisibility(View.VISIBLE);

            // Hide Sort Menu
//            findViewById(R.id.sortMenu).setVisibility(View.INVISIBLE); TODO: Hide Overflow Menu. 30m.
        }
    }
}