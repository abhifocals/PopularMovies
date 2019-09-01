package com.focals.popularmoviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.OnClickHandler {

    RecyclerView rv_main;
    RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_main = (RecyclerView) findViewById(R.id.rv_movies);

        adapter = new PopularMoviesAdapter(20);

        // Attach Adapter

        rv_main.setAdapter(adapter);

        rv_main.setHasFixedSize(true);


        // Provide a Layout Manager

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        rv_main.setLayoutManager(gridLayoutManager);

    }

    @Override
    public void onItemClick(int item) {
            System.out.println();
    }
}
