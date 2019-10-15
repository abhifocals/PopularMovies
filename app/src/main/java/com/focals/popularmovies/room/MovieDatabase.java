package com.focals.popularmovies.room;

import android.content.Context;

import com.focals.popularmovies.Movie;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies";
    private static MovieDatabase sInstance;

    public static MovieDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                // TODO: remove allowMainThread
                sInstance = Room.databaseBuilder(context.getApplicationContext(), MovieDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
