package com.focals.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {

    private final int itemCount;
    private final OnClickHandler clickHandler;
    private List<Movie> movies;

    PopularMoviesAdapter(int numOfItems, OnClickHandler clickHandler) {
        this.itemCount = numOfItems;
        this.clickHandler = clickHandler;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public PopularMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_card, parent, false);

        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesViewHolder holder, int position) {
        Picasso.get().load(movies.get(position).posterPath).error(R.drawable.placeholder).into(holder.movieCard);
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
