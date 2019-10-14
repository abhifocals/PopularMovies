package com.focals.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focals.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

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

        String reviewUrlStrilg = NetworkUtils.getReview(currentMovie.getId()).toString();
        openWebPage(reviewUrlStrilg);


//        Toast toast = Toast.makeText(this, "Review Shown", Toast.LENGTH_SHORT);
//        toast.show();
    }

    private void openWebPage(String url) {
        Uri uri = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
}

