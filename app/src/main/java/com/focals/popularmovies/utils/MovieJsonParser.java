package com.focals.popularmovies.utils;

import com.focals.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieJsonParser {

    Movie movie;


    public static void buildMovieArray(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray results = jsonObject.getJSONArray("results");

            System.out.println();



            // Iterate through response and build Array of MovieObjects












            jsonObject.getJSONArray("results").getJSONObject(0).getString("title");


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
