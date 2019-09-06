package com.focals.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MovieDetail extends AppCompatActivity {
    TextView title;
    ImageView thumbnail;
    TextView rating;
    TextView releaseDate;
    TextView plot;
    Movie currentMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_movie);

        title = (TextView) findViewById(R.id.title);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        rating = (TextView) findViewById(R.id.rating);
        releaseDate = (TextView) findViewById(R.id.releaseDate);
        plot = (TextView) findViewById(R.id.plot);

        Intent intent = getIntent();
        currentMovie = intent.getParcelableExtra("movie");

        if (intent != null) {
            title.setText(currentMovie.title);
            Picasso.get().load(currentMovie.posterPath).into(thumbnail);
            releaseDate.setText(currentMovie.releaseDate);
            plot.setText(currentMovie.plotSynopsis);
            rating.setText(currentMovie.rating);
        }
    }
}

