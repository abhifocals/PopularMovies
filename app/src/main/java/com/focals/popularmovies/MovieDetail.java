package com.focals.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focals.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MovieDetail extends AppCompatActivity {

    private Movie currentMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_movie);

        // Getting views
        TextView title = findViewById(R.id.title);
        ImageView thumbnail = findViewById(R.id.thumbnail);
        TextView rating = findViewById(R.id.rating);
        TextView releaseDate = findViewById(R.id.releaseDate);
        TextView plot = findViewById(R.id.plot);

        // Getting intent
        Intent intent = getIntent();
        currentMovie = intent.getParcelableExtra("movie");

        // Setting content in views
        if (currentMovie != null) {
            title.setText(currentMovie.title);
            Picasso.get().load(currentMovie.posterPath).into(thumbnail);
            releaseDate.setText(currentMovie.releaseDate);
            plot.setText(currentMovie.plotSynopsis);
            rating.setText(currentMovie.rating);
        }
    }

    public void addToFavorites(View view) {
        Toast toast = Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showReview(View view) {

        String reviewUrlStrilg = NetworkUtils.getReviewUrl(currentMovie.getId()).toString();
        openWebPage(reviewUrlStrilg);


//        Toast toast = Toast.makeText(this, "Review Shown", Toast.LENGTH_SHORT);
//        toast.show();
    }

    private void openWebPage(String url) {

            FetchMovieReviewTask reviewTask = new FetchMovieReviewTask();
            reviewTask.execute();





        //        Uri uri = Uri.parse(url);
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        if (intent.resolveActivity(getPackageManager()) != null)
//            startActivity(intent);
    }

    class FetchMovieReviewTask extends AsyncTask<URL, Void, String> {


        @Override
        protected String doInBackground(URL... urls) {
            return NetworkUtils.getResponseFromUrl(NetworkUtils.getReviewUrl(currentMovie.getId()));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject review = new JSONObject(s);

                String content = new JSONArray(review.getString("results")).getJSONObject(0).getString("content");


                // Start Review Activity here





            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}

