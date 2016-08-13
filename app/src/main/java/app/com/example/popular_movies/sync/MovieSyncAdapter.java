package app.com.example.popular_movies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import app.com.example.popular_movies.R;
import app.com.example.popular_movies.Utility;
import app.com.example.popular_movies.data.MovieContract;


public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in milliseconds.
// 60 seconds (1 minute) * 480 = 8 hours
    public static final int SYNC_INTERVAL = 60 * 480;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        try {

            String sortCriteria = Utility.getPreferredSortCriteria(getContext());
            String movieUri = Utility.getMovieURI(getContext()
                    , sortCriteria).toString();
            if (sortCriteria.equals("favourite.desc")) {
                return;
            }
            URL url = new URL(movieUri);
            Log.v(LOG_TAG, "Built Uri:" + movieUri.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.

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

            }
            movieJsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

            //  List<Movie> l =getMoviesFromLocalDb(params[0]);

            //return l;
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

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
            if (movieJsonStr != null) {
                insertMovieDataInLocalDB(movieJsonStr);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void insertMovieDataInLocalDB(String movieJsonStr)
            throws JSONException {
        try {
            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray jsonArray = forecastJson.getJSONArray("results");
            ContentValues[] cVVector = new ContentValues[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                ContentValues movieValues = new ContentValues();
                // movieValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 0);

                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, item.getString("original_title"));
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, item.getString("popularity"));
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, item.getString("release_date"));

                movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, item.getBoolean("video"));
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, item.getString("id"));
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE, item.getString("poster_path"));
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, item.getString("overview"));
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, item.getString("vote_count"));
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, item.getString("vote_average"));


                cVVector[i] = movieValues;
            }
            int insertCount = 0;
            int deleteCount = 0;
            // add to database
            if (cVVector.length > 0) {

                //   if(getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI+"/"+item.getString("id"),))
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                //String concatFavMovieIds = prefs.getString("FAVMOVIES", "");
                deleteCount = getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID,null); //+ " not in (?)", new String[]{concatFavMovieIds});
                insertCount = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cVVector);
            }


            Log.d(LOG_TAG, "FetchMovieTask Complete. " + deleteCount + " Deleted ," + insertCount + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // return null;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


}