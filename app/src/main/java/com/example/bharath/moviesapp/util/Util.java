package com.example.bharath.moviesapp.util;

/**
 * Created by Bharath on 4/22/2016.
 */
public class Util {


    public static String getCustomizedDate(String releasedate){

        String[] trimdate  = releasedate.split("-");

        return trimdate[0];

    }

    public static String getCustomizedRating(String rating){

        double ratingDouble  = Double.parseDouble(rating);

        int roundOfRating =(int)ratingDouble;

        String userrating = String.format("%d/10",roundOfRating);

        return userrating;

    }
}
