package com.focals.popularmovies.room;

import android.content.Context;

import com.focals.popularmovies.Movie;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters(ListConverter.class)
public abstract class MovieDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies";
    private static MovieDatabase sInstance;

    public static MovieDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), MovieDatabase.class, DATABASE_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
