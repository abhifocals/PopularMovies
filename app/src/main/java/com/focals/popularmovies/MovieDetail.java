package com.focals.popularmovies;

import android.content.Intent;
import android.net.Uri;
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

        if (intent != null) {
            title.setText(intent.getStringExtra("title"));
            Picasso.get().load(intent.getStringExtra("posterPath")).into(thumbnail);
            releaseDate.setText(intent.getStringExtra("releaseDate"));
            plot.setText(intent.getStringExtra("plot"));
            rating.setText(intent.getStringExtra("rating"));
        }
    }
}

