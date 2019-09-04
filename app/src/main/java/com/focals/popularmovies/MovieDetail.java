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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_movie);

        title = (TextView) findViewById(R.id.title);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);

        Intent intent = getIntent();

        if (intent != null) {
            String response = intent.getStringExtra("title");
            Uri uri = intent.getData();

            title.setText(response);
            Picasso.get().load(uri).into(thumbnail);
        }
    }
}

