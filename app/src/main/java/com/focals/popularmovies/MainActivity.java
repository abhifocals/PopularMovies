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
    private ArrayList<Movie> topRatedList = new ArrayList<>();

    private ProgressBar progressBar;
    private FetchMovieData fetchTask;
    private Menu menu;
    MovieDatabase db;
    MovieDao movieDao;

    public static boolean GET_POPULAR;
    private static boolean GET_TOP_RATED;

    private static boolean LOADED_POPULAR;
    private static boolean LOADED_TOP_RATED;
    private static boolean LOADED_FAVORITE;

    private static String loadedPopular = "loadedPopular";
    private static String loadedTopRated = "loadedTopRated";
    private static String loadedFavorite = "loadedFavorite";

    private static final String TAG = "Test";
    MainViewModel mainViewModel;
    TextView error;
    public static final String MOVIE_ID = "MOVIE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Views
        rv_main = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.progressBar);
        error = (TextView) findViewById(R.id.tv_error);

        // Get DB
        db = MovieDatabase.getInstance(this);
        movieDao = db.movieDao();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        showProgressBar();

        // Upon Rotation
        if (savedInstanceState != null && savedInstanceState.getBoolean(loadedPopular)) {
            setUpAdapterAndLayoutManager(mainViewModel.getPopularMoviesData().getValue());
        } else if (savedInstanceState != null && savedInstanceState.getBoolean(loadedTopRated)) {
            setUpAdapterAndLayoutManager(mainViewModel.getTopRatedMoviesData().getValue());
        } else if (savedInstanceState != null && savedInstanceState.getBoolean(loadedFavorite)) {
            setUpAdapterAndLayoutManager(mainViewModel.getFavoriteMovieData().getValue());
        } else {
            GET_POPULAR = true;
            GET_TOP_RATED = true;
            LOADED_POPULAR = true;

            fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getPopularMoviesURL());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(loadedPopular, LOADED_POPULAR);
        outState.putBoolean(loadedTopRated, LOADED_TOP_RATED);
        outState.putBoolean(loadedFavorite, LOADED_FAVORITE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.sort_menu, menu);

        if (LOADED_POPULAR) {
            setMenuOptions(false, true, true);

        } else if (LOADED_TOP_RATED) {
            setMenuOptions(true, false, true);

        } else if (LOADED_FAVORITE) {
            setMenuOptions(true, true, false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sort_popular:

                showProgressBar();

                // Setup ViewModel
                setupViewModel(mainViewModel.getPopularMoviesData());

                // Disable this option, enable other
                setMenuOptions(false, true, true);
//                setLoadedList(true, false, false);

                break;

            case R.id.sort_rated:

                showProgressBar();

                if (GET_TOP_RATED) {
                    fetchTask = new FetchMovieData();
                    fetchTask.execute(NetworkUtils.getTopRatedMoviesURL());
                } else {
                    // Setup ViewModel
                    setupViewModel(mainViewModel.getTopRatedMoviesData());
                }

                // Disable this option, enable other
                setMenuOptions(true, false, true);
//                setLoadedList(false, true, false);

                break;

            case R.id.sort_favorites:

                showProgressBar();
                setMenuOptions(true, true, false);
//                setLoadedList(false, false, true);

                mainViewModel.getFavoriteMovieData().observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        if (movies.size() == 0) {
                            showEmptyFavoriteListMessage();
                        } else {
                            setUpAdapterAndLayoutManager(movies);
                        }
                    }
                });
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

            if (s == null) {
                showError();
                return;
            }

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

                setupViewModel(mainViewModel.getPopularMoviesData());

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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Setup ViewModel
                                setupViewModel(mainViewModel.getTopRatedMoviesData());
                            }
                        });
                    }
                });
            }
        }
    }


    ///// ***** Helpers ***** /////

    private void setupViewModel(final LiveData<List<Movie>> movieData) {
        movieData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                setUpAdapterAndLayoutManager(movies);
            }
        });
    }

    private void setUpAdapterAndLayoutManager(List<Movie> listOfMovies) {
        MainAdapter adapter = new MainAdapter(listOfMovies, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        hideProgressBar();
        hideNoFavoriteMessage();

        rv_main.setAdapter(adapter);
        rv_main.setHasFixedSize(true);
        rv_main.setLayoutManager(gridLayoutManager);
    }

    private void hideProgressBar() {
        rv_main.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideNoFavoriteMessage() {
        rv_main.setVisibility(View.VISIBLE);
        error.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        rv_main.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showError() {
        progressBar.setVisibility(View.INVISIBLE);
        error.setText(getString(R.string.error));
        error.setVisibility(View.VISIBLE);

        // Hide Overflow Menu
        menu.setGroupVisible(R.id.overflowMenu, false);
    }

    private void showEmptyFavoriteListMessage() {
        progressBar.setVisibility(View.INVISIBLE);
        error.setText(getString(R.string.emptyFavListMessage));
        error.setVisibility(View.VISIBLE);
        rv_main.setVisibility(View.INVISIBLE);
    }

    private void setMenuOptions(boolean popular, boolean rated, boolean favorite) {
        // This disables the menu option for the currently loaded list
        menu.findItem(R.id.sort_popular).setEnabled(popular);
        menu.findItem(R.id.sort_rated).setEnabled(rated);
        menu.findItem(R.id.sort_favorites).setEnabled(favorite);

        // These flags are used for determining which list was loaded for item click method
        LOADED_POPULAR = !popular;
        LOADED_TOP_RATED = !rated;
        LOADED_FAVORITE = !favorite;
    }
}