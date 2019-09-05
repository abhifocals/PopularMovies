package com.focals.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.focals.popularmovies.utils.MovieJsonParser;
import com.focals.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.OnClickHandler {

    RecyclerView rv_main;
    PopularMoviesAdapter adapter;
    String response;
    List<Movie> movieList;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get Popular Movies
        FetchMovieData fetchTask = new FetchMovieData();
        fetchTask.execute(NetworkUtils.getPopularMoviesURL());

        adapter = new PopularMoviesAdapter(20, this);
        gridLayoutManager = new GridLayoutManager(this, 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sort_popular) {
            FetchMovieData fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getPopularMoviesURL());
        }

        if (item.getItemId() == R.id.sort_rated) {
            FetchMovieData fetchTask = new FetchMovieData();
            fetchTask.execute(NetworkUtils.getTopRatedMoviesUrl());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int index) {
        Intent intent = new Intent(this, MovieDetail.class);

        String title = movieList.get(index).title;
        intent.putExtra("title", title);
        intent.setData(movieList.get(index).posterUri);
        intent.putExtra("plot", movieList.get(index).plotSynopsis);
        intent.putExtra("releaseDate", movieList.get(index).releaseDate);
        intent.putExtra("rating", movieList.get(index).rating);
        startActivity(intent);
    }

    class FetchMovieData extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String response = NetworkUtils.getResponseFromUrl(url);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            response = s;
            movieList = null;

            movieList = MovieJsonParser.buildMovieArray(response);
            adapter.setMovies(movieList);

            // Attach Adapter
            rv_main = (RecyclerView) findViewById(R.id.rv_movies);
            rv_main.setAdapter(adapter);
            rv_main.setHasFixedSize(true);

            // Provide a Layout Manager
            rv_main.setLayoutManager(gridLayoutManager);

        }
    }
}
