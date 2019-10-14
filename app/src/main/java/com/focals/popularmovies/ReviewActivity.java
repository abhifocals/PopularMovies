package com.focals.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ReviewActivity extends AppCompatActivity {

    TextView textViewReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        textViewReview = (TextView) findViewById(R.id.textViewReview);

        Intent intent = getIntent();

        String review = intent.getStringExtra("REVIEW");

        textViewReview.setText(review);

    }
}
