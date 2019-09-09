package com.focals.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.focals.popularmovies.comparators.PopularityComparator;
import com.focals.popularmovies.comparators.RatingComparator;
import com.focals.popularmovies.utils.MovieJsonParser;
import com.focals.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.OnClickHandler {

    private RecyclerView rv_main;
    private ArrayList<Movie> movieList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Views
        rv_main = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.progressBar);

        // Get Popular Movies
        if (savedInstanceState == null) {
            FetchMovieData fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getPopularMoviesURL());
        } else {
            movieList = savedInstanceState.getParcelableArrayList("movies");

            if (movieList != null)
                setUpAdapterAndLayoutManager();
            else
                showError();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_popular:
                Collections.sort(movieList, new PopularityComparator());
                break;

            case R.id.sort_rated:
                Collections.sort(movieList, new RatingComparator());
                break;
        }
        if (movieList != null) {
            setUpAdapterAndLayoutManager();
        } else {
            showError();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int index) {
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra("movie", movieList.get(index));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", movieList);
    }

    class FetchMovieData extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            showProgressBar();

            URL url = urls[0];

            
            return NetworkUtils.getResponseFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgressBar();

            if (s == null) {
                showError();
                hideSortMenu();
            } else {
                // Build Movie Objects from Response
                movieList = MovieJsonParser.buildMovieArray(s);

                // Attach Adapter and Layout Manager
                setUpAdapterAndLayoutManager();
            }
        }
    }

    private void hideSortMenu() {
        findViewById(R.id.sortMenu).setVisibility(View.INVISIBLE);
    }

    private void setUpAdapterAndLayoutManager() {
        PopularMoviesAdapter adapter = new PopularMoviesAdapter(movieList.size(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        adapter.setMovies(movieList);
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
        progressBar.setVisibility(View.INVISIBLE);
        TextView error = findViewById(R.id.tv_error);
        error.setVisibility(View.VISIBLE);
    }
}