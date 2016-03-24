package app.com.example.popular_movies;
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */

public class Utility {
    public static Uri getMovieURI(Context context, String sortCriteria) {
        //  String sortCriteria = getPreferredSortCriteria(context);
        final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String SORT_KEY = "sort_by";
        final String API_KEY = "api_key";
        //if(params[0]=="favourite.desc") {
        //  return getMoviesFromLocalDb(params[0]);
        //}
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(SORT_KEY, sortCriteria)
                .appendQueryParameter(API_KEY, BuildConfig.OPEN_MOVIE_API_KEY).build();


        return builtUri;


    }

    public static String getReviewsURI(long mMovieId) {
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        //
        final String API_KEY = "api_key";
        Uri builtUri = Uri.parse(BASE_URL + mMovieId + "/reviews").buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.OPEN_MOVIE_API_KEY).build();
        return builtUri.toString();
    }

    public static String getTrailersURI(long mMovieId) {
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        //
        final String API_KEY = "api_key";

        Uri builtUri = Uri.parse(BASE_URL + mMovieId + "/videos").buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.OPEN_MOVIE_API_KEY).build();
        return builtUri.toString();
    }

    public static String getPreferredSortCriteria(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sort_pref_key),
                context.getString(R.string.pref_sortby_defaultvalue));
    }
//
//    public static boolean isMetric(Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getString(context.getString(R.string.pref_units_key),
//                context.getString(R.string.pref_units_metric))
//                .equals(context.getString(R.string.pref_units_metric));
//    }
//
//    static String formatTemperature(double temperature, boolean isMetric) {
//        double temp;
//        if ( !isMetric ) {
//            temp = 9*temperature/5+32;
//        } else {
//            temp = temperature;
//        }
//        return String.format("%.0f", temp);
//    }

    public static String getFormattedDate(String date) {
        DateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
        Date d = null;
        try {
            d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return df1.format(d);
    }

    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }
}