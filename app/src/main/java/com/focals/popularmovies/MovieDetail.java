package com.focals.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MovieDetail extends AppCompatActivity {



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
        Movie currentMovie = intent.getParcelableExtra("movie");

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
}

