package com.focals.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.focals.popularmovies.room.MovieDao;
import com.focals.popularmovies.room.MovieDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.PopularMoviesViewHolder> {

    private final int itemCount;
    private final OnClickHandler clickHandler;
    private List<Movie> movies;
    MovieDatabase db;
    MovieDao movieDao;

    MainAdapter(int numOfItems, OnClickHandler clickHandler) {
        this.itemCount = numOfItems;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public PopularMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_card, parent, false);

        db = MovieDatabase.getInstance(parent.getContext());
        movieDao = db.movieDao();

        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PopularMoviesViewHolder holder, final int position) {

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
             final List<Movie>  movies = movieDao.getMovies();

                AppExecutors.getsInstance().getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get().load(movies.get(position).posterPath).error(R.drawable.placeholder).into(holder.movieCard);
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemCount;
    }


    public interface OnClickHandler {
        void onItemClick(int index);
    }

    class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView movieCard;

        PopularMoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            movieCard = itemView.findViewById(R.id.iv_movieCard);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickHandler.onItemClick(getAdapterPosition());
        }
    }
}