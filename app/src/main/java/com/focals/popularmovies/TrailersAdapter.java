package com.focals.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    OnClickHandler clickHandler;
    List<String> trailerUrls;

    public TrailersAdapter(List<String> trailerUrls, OnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        this.trailerUrls = trailerUrls;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.trailerPlayButton.setImageResource(android.R.drawable.ic_media_play);

        String trailerText = holder.itemView.getContext().getResources().getString(R.string.trailer) + " " + (position + 1);
        holder.trailerText.setText(trailerText);
    }

    @Override
    public int getItemCount() {
        return trailerUrls.size();
    }

    public interface OnClickHandler {
        void onClickTrailer(int position);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView trailerPlayButton;
        TextView trailerText;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerPlayButton = (ImageView) itemView.findViewById(R.id.trailerPlayButton);
            trailerText = (TextView) itemView.findViewById(R.id.trailerText);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            clickHandler.onClickTrailer(getAdapterPosition());
        }
    }
}
