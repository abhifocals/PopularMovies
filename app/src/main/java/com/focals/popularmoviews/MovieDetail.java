package com.focals.popularmoviews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MovieDetail extends AppCompatActivity {

    TextView title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_movie);

        title = (TextView) findViewById(R.id.title);

        Intent intent = getIntent();

        if (intent != null) {
            String response = intent.getStringExtra("response");

           title.setText(response);


        }


    }
}
