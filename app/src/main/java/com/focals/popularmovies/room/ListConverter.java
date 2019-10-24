package com.focals.popularmovies.room;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class ListConverter {

    @TypeConverter
    public String listToString(List<String> list) {

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.toJson(list, type);
    }

    @TypeConverter
    public List<String> stringToList(String string) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(string,type);
    }
}
