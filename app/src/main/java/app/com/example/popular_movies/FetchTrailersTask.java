package app.com.example.popular_movies;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.com.example.popular_movies.data.MovieContract;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class FetchTrailersTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private final Context mContext;

    public FetchTrailersTask(Context context) {
        mContext = context;


    }


    private boolean DEBUG = true;

    private void insertTrailerDataInLocalDB(String TrailerJsonStr, String movieId)
            throws JSONException {
        try {
            JSONObject forecastJson = new JSONObject(TrailerJsonStr);
            JSONArray jsonArray = forecastJson.getJSONArray("results");
            ContentValues[] cVVector = new ContentValues[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.TrailerEntry.COLUMN_KEY, item.getString("key"));
                contentValues.put(MovieContract.TrailerEntry.COLUMN_NAME, item.getString("name"));
                contentValues.put(MovieContract.TrailerEntry.COLUMN_SITE, item.getString("site"));
                contentValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, item.getString("size"));
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);


                cVVector[i] = contentValues;
            }
            int insertCount = 0;
            int deleteCount = 0;
            // add to database
            if (cVVector.length > 0) {

                deleteCount = mContext.getContentResolver().delete(MovieContract.TrailerEntry.buildTrailerUri(Long.parseLong(movieId)), null, null);
                insertCount = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cVVector);
            }
            Log.d(LOG_TAG, "FetchTrailerTask Complete. " + deleteCount + " Deleted ," + insertCount + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // return null;
    }

    /*
        Students: This code will allow the FetchMovieTask to continue to return the strings that
        the UX expects so that we can continue to test the application even once we begin using
        the database.
     */
//    List<Trailer> convertContentValuesToUXFormat(ContentValues[] cvv) {
//        Movie movieItem = null;
//        List<Movie> list = new ArrayList<Movie>();
//        // return strings to keep UI functional for now
//        String[] resultStrs = new String[cvv.length];
//        for ( int i = 0; i < cvv.length; i++ ) {
//            ContentValues weatherValues = cvv[i];
//            //  JSONObject item = jsonArray.getJ  SONObject(i);
//
//
//
//            movieItem = new Trailer(weatherValues.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID),
//
//                    weatherValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE),
//                    weatherValues.getAsString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE),
//                    weatherValues.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE),
//                    weatherValues.getAsString(MovieContract.MovieEntry.COLUMN_OVERVIEW),
//                    weatherValues.getAsString(MovieContract.MovieEntry.COLUMN_VOTE_COUNT),
//                    weatherValues.getAsFloat(MovieContract.MovieEntry.COLUMN_RATING)
//            );
//            list.add(movieItem);
//
//
//        }
//        return list;
//    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */


    @Override
    protected Void doInBackground(String... params) {

        // If there's no movie id, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
//        String sortQuery = params[0];
        String movieId = params[0];
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;


        try {


            String mUrl = Utility.getTrailersURI(Long.valueOf(movieId));
            URL url = new URL(mUrl);
            Log.v(LOG_TAG, "Built Uri:" + url);
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

            // List<Trailer> l =getTrailersFromLocalDb(params[0]);

            //return l;
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            insertTrailerDataInLocalDB(movieJsonStr, movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        //
        // mContext.getContentResolver().

        // This will only happen if there was an error getting or parsing the forecast.
        // return null;
        return null;
    }


}

