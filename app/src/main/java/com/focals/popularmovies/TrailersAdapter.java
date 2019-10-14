package com.focals.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

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
        holder.trailerText.setText("Trailer");
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        ImageView trailerPlayButton;
        TextView trailerText;


        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);

            trailerPlayButton = (ImageView) itemView.findViewById(R.id.trailerPlayButton);
            trailerText = (TextView) itemView.findViewById(R.id.trailerText);
        }
    }
}
