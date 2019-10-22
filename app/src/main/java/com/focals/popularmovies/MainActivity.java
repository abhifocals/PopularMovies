package com.focals.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.focals.popularmovies.room.MainViewModel;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnClickHandler {

    private RecyclerView rv_main;
    private ArrayList<Movie> popularList;
    private ArrayList<Movie> topRatedList;

    private ProgressBar progressBar;
    private FetchMovieData fetchTask;
    private Menu menu;
    MovieDatabase db;
    MovieDao movieDao;

    public static final String MOVIE_ID = "MOVIE_ID";
    public static boolean GET_POPULAR;
    private static boolean GET_TOP_RATED;

    private static boolean LOADED_POPULAR;
    private static boolean LOADED_TOP_RATED;
    private static boolean LOADED_FAVORITE;

    private static final String TAG = "Test";
    MainViewModel mainViewModel;

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


        // Get top-rated movies
        topRatedList = new ArrayList<>();


        GET_POPULAR = true;
        GET_TOP_RATED = true;
        LOADED_POPULAR = true;

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // Get Popular Movies
        if (popularList == null) {
            fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getPopularMoviesURL());

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
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sort_popular:

                //showProgressBar();

                // Get data from DB
                mainViewModel.getPopularMoviesData().observe(MainActivity.this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        setUpAdapterAndLayoutManager(movies);
                    }
                });


                // Disable this option, enable other
                item.setEnabled(false);
                menu.findItem(R.id.sort_rated).setEnabled(true);

                LOADED_POPULAR = true;

                LOADED_TOP_RATED = false;
                LOADED_FAVORITE = false;

                break;

            case R.id.sort_rated:

                // showProgressBar();

                if (GET_TOP_RATED) {
                    fetchTask = new FetchMovieData();
                    fetchTask.execute(NetworkUtils.getTopRatedMoviesURL());
                } else {

                    // Get data from DB
                    mainViewModel.getTopRatedMoviesData().observe(MainActivity.this, new Observer<List<Movie>>() {
                        @Override
                        public void onChanged(List<Movie> movies) {
                            setUpAdapterAndLayoutManager(movies);
                        }
                    });
                }

                // Disable this option, enable other
                item.setEnabled(false);
                menu.findItem(R.id.sort_popular).setEnabled(true);

                LOADED_TOP_RATED = true;

                LOADED_POPULAR = false;
                LOADED_FAVORITE = false;

                break;

            case R.id.sort_favorites:

                // showProgressBar();

                // Get data from DB
                mainViewModel.getFavoriteMovieData().observe(MainActivity.this, new Observer<List<Movie>>() {

                    @Override
                    public void onChanged(List<Movie> movies) {
                        setUpAdapterAndLayoutManager(new ArrayList<Movie>(movies));
                    }
                });

                LOADED_FAVORITE = true;

                LOADED_TOP_RATED = false;
                LOADED_POPULAR = false;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(final int index) {
        final Intent intent = new Intent(this, MovieDetailActivity.class);
        int movieId = 0;

        if (LOADED_POPULAR) {
            movieId = mainViewModel.getPopularMoviesData().getValue().get(index).getMovieId();

        } else if (LOADED_TOP_RATED) {
            movieId = mainViewModel.getTopRatedMoviesData().getValue().get(index).getMovieId();

        } else if (LOADED_FAVORITE) {
            movieId = mainViewModel.getFavoriteMovieData().getValue().get(index).getMovieId();
        }

        intent.putExtra(MOVIE_ID, movieId);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // TODO ViewModel
    }

    class FetchMovieData extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            if (GET_POPULAR || GET_TOP_RATED) {
                return NetworkUtils.getResponseFromUrl(urls[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (GET_POPULAR) {
                popularList = MovieJsonParser.buildMovieArray(s);
                GET_POPULAR = false;

                // Room Insert into Database
                AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        // loop here
                        for (final Movie movie : popularList) {
                            movieDao.insertMovie(movie);
                        }
                    }
                });


                // Get data from DBs
                mainViewModel.getPopularMoviesData().observe(MainActivity.this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        setUpAdapterAndLayoutManager(movies);
                    }
                });

            } else if (GET_TOP_RATED) {
                topRatedList = MovieJsonParser.buildMovieArray(s);
                GET_TOP_RATED = false;

                // Room Insert into Database
                AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (final Movie movie : topRatedList) {
                            movieDao.insertMovie(movie);
                        }
                    }
                });

                // Get data from DB
                mainViewModel.getTopRatedMoviesData().observe(MainActivity.this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        setUpAdapterAndLayoutManager(movies);
                    }
                });
            }


//            if (s == null) {
//                showError();
//            } else {
//                // Build Movie Objects from Response
//                popularList = null;
//                popularList = MovieJsonParser.buildMovieArray(s);


        }

    }


    private void setUpAdapterAndLayoutManager(List<Movie> listOfMovies) {
        MainAdapter adapter = new MainAdapter(listOfMovies, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        hideProgressBar();
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
        if (popularList == null) {
            progressBar.setVisibility(View.INVISIBLE);
            TextView error = findViewById(R.id.tv_error);
            error.setVisibility(View.VISIBLE);

            // Hide Sort Menu
//            findViewById(R.id.sortMenu).setVisibility(View.INVISIBLE); TODO: Hide Overflow Menu. 30m.
        }
    }
}