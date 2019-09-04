package com.focals.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

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
    public void onItemClick(View view) {
        Intent intent = new Intent(this, MovieDetail.class);

        String posterPath = null;
        intent.putExtra("response", response);

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