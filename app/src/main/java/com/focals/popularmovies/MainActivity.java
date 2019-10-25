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
    private ProgressBar progressBar;
    private FetchMovieData fetchTask;
    private Menu menu;
    private MovieDao movieDao;
    private MainViewModel mainViewModel;
    private TextView errorView;

    /**
     * Used for restricting network call to one time only.
     **/
    private static boolean GET_POPULAR;
    private static boolean GET_TOP_RATED;

    /**
     * Used for keeping track of which list is loaded.
     * Needed during item click.
     */
    private static boolean LOADED_POPULAR;
    private static boolean LOADED_TOP_RATED;
    private static boolean LOADED_FAVORITE;

    /**
     * Used for keeping track of state during rotation.
     */
    private static final String LOAD_POPULAR = "loadedPopular";
    private static final String LOAD_TOP_RATED = "loadedTopRated";
    private static final String LOAD_FAVORITE = "loadedFavorite";

    /**
     * Passed to MovieDetailActivity
     */
    public static final String MOVIE_ID = "MOVIE_ID";

    /**
     * Storing movie lists locally since there is a slight lag between calling the database and receiving the values.
     */
    private ArrayList<Movie> popularMovies;
    private ArrayList<Movie> topRatedMovies;
    private ArrayList<Movie> favoriteMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Views
        rv_main = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.progressBar);
        errorView = findViewById(R.id.tv_error);

        // Get DB
        MovieDatabase db = MovieDatabase.getInstance(this);
        movieDao = db.movieDao();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        showProgressBar();

        // Upon Rotation
        if (savedInstanceState != null && savedInstanceState.getBoolean(LOAD_POPULAR)) {
            setUpAdapterAndLayoutManager(mainViewModel.getPopularMoviesData().getValue());
        } else if (savedInstanceState != null && savedInstanceState.getBoolean(LOAD_TOP_RATED)) {
            setUpAdapterAndLayoutManager(mainViewModel.getTopRatedMoviesData().getValue());
        } else if (savedInstanceState != null && savedInstanceState.getBoolean(LOAD_FAVORITE)) {
            setUpAdapterAndLayoutManager(mainViewModel.getFavoriteMovieData().getValue());
        } else {
            GET_POPULAR = true;
            GET_TOP_RATED = true;
            LOADED_POPULAR = true;

            fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getPopularMoviesURL());
        }

        setupViewModel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(LOAD_POPULAR, LOADED_POPULAR);
        outState.putBoolean(LOAD_TOP_RATED, LOADED_TOP_RATED);
        outState.putBoolean(LOAD_FAVORITE, LOADED_FAVORITE);
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

        showProgressBar();

        switch (item.getItemId()) {

            case R.id.sort_popular:
                setMenuOptions(false, true, true);
                setUpAdapterAndLayoutManager(popularMovies);
                break;

            case R.id.sort_rated:
                setMenuOptions(true, false, true);

                if (GET_TOP_RATED) {
                    fetchTask = new FetchMovieData();
                    fetchTask.execute(NetworkUtils.getTopRatedMoviesURL());
                } else {
                    setUpAdapterAndLayoutManager(topRatedMovies);
                }
                break;

            case R.id.sort_favorites:
                setMenuOptions(true, true, false);
                loadFavorites();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Clicks the movie and launches MovieDetailActivity.
     * @param index
     */

    @Override
    public void onItemClick(final int index) {
        final Intent intent = new Intent(this, MovieDetailActivity.class);
        int movieId = 0;

        if (LOADED_POPULAR) {
            movieId = popularMovies.get(index).getMovieId();

        } else if (LOADED_TOP_RATED) {
            movieId = topRatedMovies.get(index).getMovieId();

        } else if (LOADED_FAVORITE) {
            movieId = favoriteMovies.get(index).getMovieId();
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
                showError(R.string.error);

                // Hide Overflow Menu
                menu.setGroupVisible(R.id.overflowMenu, false);

                return;
            }

            if (GET_POPULAR) {
                final ArrayList<Movie> popularList = MovieJsonParser.buildMovieArray(s);
                GET_POPULAR = false;

                // Room Insert into Database
                AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        movieDao.insertMovies(popularList);
                    }
                });

                setUpAdapterAndLayoutManager(popularList);

            } else if (GET_TOP_RATED) {
                final ArrayList<Movie> topRatedList = MovieJsonParser.buildMovieArray(s);
                GET_TOP_RATED = false;

                // Room Insert into Database
                AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        movieDao.insertMovies(topRatedList);
                    }
                });

                setUpAdapterAndLayoutManager(topRatedList);
            }
        }
    }

    ///// ***** Helpers ***** /////

    private void setupViewModel() {
        mainViewModel.getPopularMoviesData().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                popularMovies = new ArrayList<>(movies);
            }
        });

        mainViewModel.getTopRatedMoviesData().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                topRatedMovies = new ArrayList<>(movies);
            }
        });

        mainViewModel.getFavoriteMovieData().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                favoriteMovies = new ArrayList<>(movies);

                if (LOADED_FAVORITE) {
                    loadFavorites();
                }
            }
        });
    }

    private void setUpAdapterAndLayoutManager(List<Movie> listOfMovies) {
        MainAdapter adapter = new MainAdapter(listOfMovies, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        rv_main.setAdapter(adapter);
        rv_main.setHasFixedSize(true);
        rv_main.setLayoutManager(gridLayoutManager);

        rv_main.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorView.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        rv_main.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Used for displaying error if response is null or no  favorite items
     **/

    private void showError(int error) {
        progressBar.setVisibility(View.INVISIBLE);
        errorView.setText(getString(error));
        errorView.setVisibility(View.VISIBLE);
    }

    /**
     * This disables the menu option for the currently loaded list
     * Also sets flags that are used for determining which list was loaded for item click method
     **/

    private void setMenuOptions(boolean popular, boolean rated, boolean favorite) {
        menu.findItem(R.id.sort_popular).setEnabled(popular);
        menu.findItem(R.id.sort_rated).setEnabled(rated);
        menu.findItem(R.id.sort_favorites).setEnabled(favorite);

        LOADED_POPULAR = !popular;
        LOADED_TOP_RATED = !rated;
        LOADED_FAVORITE = !favorite;
    }

    private void loadFavorites() {
        if (favoriteMovies.size() == 0) {
            showError(R.string.emptyFavListMessage);
            rv_main.setVisibility(View.INVISIBLE);
        } else {
            setUpAdapterAndLayoutManager(favoriteMovies);
        }
    }
}