package com.focals.popularmoviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {

    private Context context;
    private int itemCount;
    private OnClickHandler clickHandler;

    PopularMoviesAdapter(int numOfItems, OnClickHandler clickHandler) {
        this.itemCount = numOfItems;
        this.clickHandler = clickHandler;
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
//        holder.tempTV.setText("test");
//        holder.movieCard.setImageDrawable(context.getResources().getDrawable(R.drawable.poodle, null));
        holder.movieCard.setImageResource(R.drawable.doodle);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }


    public interface OnClickHandler {
        public void onItemClick(int item);
    }


    class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tempTV;
        private ImageView movieCard;


        public PopularMoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            movieCard = (ImageView) itemView.findViewById(R.id.iv_movieCard);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickHandler.onItemClick(getAdapterPosition());
        }
    }
}
