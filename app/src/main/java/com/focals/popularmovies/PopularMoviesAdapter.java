package com.focals.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focals.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {

    private Context context;
    private int itemCount;
    private OnClickHandler clickHandler;
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
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_card, parent, false);

        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesViewHolder holder, int position) {
//        holder.title.setText(movies.get(position).title);

        Uri posterUri = NetworkUtils.buildPosterUrl(movies.get(position).poster_path);

        Picasso.get().load(posterUri).into(holder.movieCard);




//        holder.title.setText("test");
//        holder.movieCard.setImageResource(R.drawable.doodle);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }


    public interface OnClickHandler {
        public void onItemClick(View view);
    }


    class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private ImageView movieCard;

        public PopularMoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            movieCard = (ImageView) itemView.findViewById(R.id.iv_movieCard);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.iv_movieCard) {
                clickHandler.onItemClick((v));
            }

        }
    }
}
