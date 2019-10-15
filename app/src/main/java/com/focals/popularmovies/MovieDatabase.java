package com.focals.popularmovies;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {MovieInDb.class}, version = 1, exportSchema = false)
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
