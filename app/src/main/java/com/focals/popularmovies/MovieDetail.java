package com.focals.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MovieDetail extends AppCompatActivity {

    TextView title;


//    original title
//    movie poster image thumbnail
//    A plot synopsis (called overview in the api)
//    user rating (called vote_average in the api)
//    release date

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_movie);

        title = (TextView) findViewById(R.id.title);

        Intent intent = getIntent();

        if (intent != null) {
            String response = intent.getStringExtra("title");

           title.setText(response);

        }
    }
}

