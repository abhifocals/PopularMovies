package com.focals.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Views
        rv_main = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.progressBar);

        // Get Popular Movies
        if (movieList == null) {
            fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getPopularMoviesURL());
            showProgressBar();
        } else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
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
//                openWebPage("http://www.google.com");
        }
        setUpAdapterAndLayoutManager();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int index) {
        Intent intent = new Intent(this, MovieDetailActivity.class);

        // TODO Room

        intent.putExtra("movie", movieList.get(index));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", movieList);

        // TODO ViewModel
    }


    private void testDatabase() {
        MovieDatabase db = MovieDatabase.getInstance(this);
        MovieDao movieDao = db.movieDao();

        // Insert
        movieDao.insertMovie(movieList.get(0));

        Movie movie = movieDao.getMovieByMovieId(movieList.get(0).getMovieId());

        boolean favFlag = movie.isFavorite();

        movie.setFavorite(true);

        movieDao.updateFavoriteFlag(movie);

        favFlag = movieDao.getMovieByMovieId(movieList.get(0).getMovieId()).isFavorite();

        // Query
        System.out.println();
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

                testDatabase();

                // Insert into Database
                db = MovieDatabase.getInstance(getApplicationContext());
                movieDao = db.movieDao();

                for (Movie movie : movieList) {
                    movieDao.insertMovie(movie);
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