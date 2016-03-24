package app.com.example.popular_movies.data;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import app.com.example.popular_movies.Utility;

public class MovieProvider extends ContentProvider {
    private final String LOG_TAG = MovieProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final Uri MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private MovieDbHelper mOpenHelper;


    static final int MOVIES = 100;
    static final int MOVIE_BY_ID = 101;
    static final int REVIEWS_BY_MOVIEID = 102;
    static final int REVIEWS = 103;

    static final int TRAILERS_BY_MOVIEID = 104;
    static final int TRAILERS = 105;


    //   static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    //  static final int LOCATION = 300;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    private static final SQLiteQueryBuilder sReviewsQueryBuilder;
    private static final SQLiteQueryBuilder sTrailersQueryBuilder;

    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sReviewsQueryBuilder = new SQLiteQueryBuilder();
        sTrailersQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME/* + " INNER JOIN " +
                            MovieContract.LocationEntry.TABLE_NAME +
                            " ON " + MovieContract.MovieEntry.TABLE_NAME +
                            "." + MovieContract.MovieEntry.COLUMN_LOC_KEY +
                            " = " + MovieContract.LocationEntry.TABLE_NAME +
                            "." + MovieContract.LocationEntry._ID*/);
        sReviewsQueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
        sTrailersQueryBuilder.setTables(MovieContract.TrailerEntry.TABLE_NAME);
    }

    //location.location_setting = ?
    private static final String sMovieOrderByPopularity =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_POPULARITY + " desc";
    private static final String sMovieOrderByRating =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_RATING + " desc";
    //   private static final String sMovieOrderByFavourite =
    //         MovieContract.MovieEntry.TABLE_NAME+"."+
    //               MovieContract.MovieEntry.COLUMN_FAVOURITE+" desc";
    private static final String sGetFavouritedMovies =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " in ";

    private static final String sGetMovieById =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";

    private static final String sGetReviewsByMovieId =
            MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " =?";


    private static final String sGetTrailersByMovieId =
            MovieContract.TrailerEntry.TABLE_NAME + "." +
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " =?";

    private Cursor getMovieById(Uri uri, String[] projection) {

        String orderByClause = "";

        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs;
        String selection;


        selectionArgs = new String[]{Long.toString(movieId)};
        selection = sGetMovieById;


        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                ""
        );
    }

    private Cursor getReviewsByMovieId(Uri uri, String[] projection) {
        //  String sortingSetting = MovieContract.MovieEntry.getSortingSettingFromUri(uri);
        String orderByClause = "";

        long movieId = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
//
        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{Long.toString(movieId)};
        selection = sGetReviewsByMovieId;


        return sReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                ""
        );
    }

    private Cursor getTrailersByMovieId(Uri uri, String[] projection) {

        String orderByClause = "";

        long movieId = MovieContract.TrailerEntry.getMovieIdFromUri(uri);

        String[] selectionArgs;
        String selection;


        selectionArgs = new String[]{Long.toString(movieId)};
        selection = sGetTrailersByMovieId;


        return sTrailersQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                ""
        );
    }

    private Cursor getMovies(Uri uri, String[] projection) {

        String sortingSetting = Utility.getPreferredSortCriteria(getContext());
        String orderByClause = "";

        if (sortingSetting.equals("popularity.desc")) {
            orderByClause = sMovieOrderByPopularity;
        } else if (sortingSetting.equals("vote_average.desc")) {
            orderByClause = sMovieOrderByRating;
        }

        Log.v(LOG_TAG, "checkpoint 1:order by " + orderByClause);
//
//
        String[] selectionArgs = null;
        String selection = null;
        if (sortingSetting.equals("favourite.desc")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String concatFavMovieIds = prefs.getString("FAVMOVIES", "");
            selectionArgs = concatFavMovieIds.split(",");
            selection = sGetFavouritedMovies + " (";
            for (int i = 0; i < selectionArgs.length; i++) {
                if (i > 0) {
                    selection += ",";
                }
                selection += "?";
            }
            selection += ")";


        }


        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderByClause
        );
    }


    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_BY_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEWS_BY_MOVIEID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILERS_BY_MOVIEID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILERS);

        return matcher;
        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieContract to help define the types to the UriMatcher.


        // 3) Return the new matcher!
        //return null;
    }

    /*
        Students: We've coded this for you.  We just create a new MovieDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case MOVIE_BY_ID: {
                retCursor = getMovieById(uri, projection);
                break;
            }
            // "weather"
            case MOVIES: {
                retCursor = getMovies(uri, projection);
                break;
            }
            case REVIEWS_BY_MOVIEID: {
                retCursor = getReviewsByMovieId(uri, projection);
                break;
            }
            case TRAILERS_BY_MOVIEID: {
                retCursor = getTrailersByMovieId(uri, projection);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                normalizeDate(values);
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count = 0;
        long movieId;
        switch (match) {
            case MOVIES:
                count = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS_BY_MOVIEID:
                movieId = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                count = db.delete(MovieContract.ReviewEntry.TABLE_NAME, sGetReviewsByMovieId, new String[]{String.valueOf(movieId)});
                break;
            case TRAILERS_BY_MOVIEID:
                movieId = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                count = db.delete(MovieContract.TrailerEntry.TABLE_NAME, sGetTrailersByMovieId, new String[]{String.valueOf(movieId)});
                break;
        }
        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        // Student: return the actual rows deleted
        return count;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)) {
            long dateValue = values.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        return db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);

    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //  normalizeDate(value);

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //  normalizeDate(value);
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILERS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //  normalizeDate(value);
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}