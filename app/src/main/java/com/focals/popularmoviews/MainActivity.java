package com.focals.popularmoviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.focals.popularmoviews.utils.MovieJsonParser;
import com.focals.popularmoviews.utils.NetworkUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.OnClickHandler {

    RecyclerView rv_main;
    RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> adapter;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_main = (RecyclerView) findViewById(R.id.rv_movies);

        adapter = new PopularMoviesAdapter(20, this);

        // Attach Adapter
        rv_main.setAdapter(adapter);
        rv_main.setHasFixedSize(true);

        // Provide a Layout Manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rv_main.setLayoutManager(gridLayoutManager);

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
    public void onItemClick(int item) {
        System.out.println();

        Intent intent = new Intent(this, MovieDetail.class);

        intent.putExtra("response", response);

        MovieJsonParser.buildMovieArray(response);




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
        }
    }
}
