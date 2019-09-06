package com.focals.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.OnClickHandler {

    RecyclerView rv_main;
    PopularMoviesAdapter adapter;
    List<Movie> movieList;
    private GridLayoutManager gridLayoutManager;
    ProgressBar progressBar;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Views
        rv_main = (RecyclerView) findViewById(R.id.rv_movies);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get Popular Movies
        FetchMovieData fetchTask = new FetchMovieData();
        fetchTask.execute(NetworkUtils.getPopularMoviesURL());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sort_popular) {

            // Get List sorted by popular here
            Collections.sort(movieList, new PopularityComparator());
            setUpAdapterAndLayoutManager();
        }

        if (item.getItemId() == R.id.sort_rated) {

            // Get List sorted by popular here
            Collections.sort(movieList, new RatingComparator());
            setUpAdapterAndLayoutManager();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(int index) {
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra("movie", movieList.get(index));
        startActivity(intent);
    }

    class FetchMovieData extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            showProgressBar();

            URL url = urls[0];
            String response = NetworkUtils.getResponseFromUrl(url);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgressBar();

            if (s == null) {
                showError();
            } else {
                // Build Movie Objects from Response
                movieList = MovieJsonParser.buildMovieArray(s);

                // Attach Adapter and Layout Manager
                setUpAdapterAndLayoutManager();
            }
        }
    }

    private void setUpAdapterAndLayoutManager() {
        adapter = new PopularMoviesAdapter(movieList.size(), this);
        gridLayoutManager = new GridLayoutManager(this, 2);

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
        error = (TextView) findViewById(R.id.tv_error);
        error.setVisibility(View.VISIBLE);
    }
}
