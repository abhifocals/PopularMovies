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
        String json = gson.toJson(list, type);
        return json;
    }

    @TypeConverter
    public List<String> stringToList(String string) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> json = gson.fromJson(string,type);
        return json;
    }
}
