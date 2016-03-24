package app.com.example.popular_movies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.example.popular_movies.data.MovieContract;
import app.com.example.popular_movies.model.Movie;
/**
 * Created by ABS - VIRUS on 2/24/2016.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
        //setHasOptionsMenu(true);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(long movieId);
    }


    public static String PAR_KEY = "MovieId";
    private static final int MOVIE_DETAIL_LOADER = 1;
    public static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            //  MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE,

            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VIDEO,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT

    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_MOVIE_ID_PK = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_ID = 2;
    //  public    static final int COLUMN_FAVOURITE = 3;
    public static final int COLUMN_MOVIE_IMAGE = 3;
    public static final int COLUMN_OVERVIEW = 4;
    public static final int COLUMN_POPULARITY = 5;
    public static final int COLUMN_RATING = 6;
    public static final int COLUMN_RELEASE_DATE = 7;
    public static final int COLUMN_VIDEO = 8;
    public static final int COLUMN_VOTE_COUNT = 9;
    Uri mUri;
    long mMovieId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (mMovieId != 0) {

            //  long mMovieId = (long) intent.getData();
            Uri mUri = MovieContract.MovieEntry.buildMovieUri(mMovieId);
            return new CursorLoader(getActivity(), mUri, MOVIE_COLUMNS, null, null, null);
        }


        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovieId = bundle.getLong(PAR_KEY);

            FetchReviewsTask getReviewsTask = new FetchReviewsTask(getContext());
            getReviewsTask.execute(String.valueOf(mMovieId));


            FetchTrailersTask getTrailersTask = new FetchTrailersTask(getContext());
            getTrailersTask.execute(String.valueOf(mMovieId));
        }


        return rootView;

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }
        final Movie mMovie = new Movie(cursor.getString(DetailActivityFragment.COL_MOVIE_ID),
                cursor.getString(DetailActivityFragment.COL_MOVIE_TITLE),
                cursor.getString(DetailActivityFragment.COLUMN_RELEASE_DATE),
                cursor.getString(DetailActivityFragment.COLUMN_MOVIE_IMAGE),
                cursor.getString(DetailActivityFragment.COLUMN_OVERVIEW),
                cursor.getString(DetailActivityFragment.COLUMN_VOTE_COUNT),
                cursor.getFloat(DetailActivityFragment.COLUMN_RATING));


        getActivity().setTitle(mMovie.getTitle()); // provide compatibility to all the versions
        final DetailPageViewHolder viewHolder = new DetailPageViewHolder(getView());
        viewHolder.ratingView.setText(mMovie.getRating() + "/10");
        viewHolder.voteCountView.setText(mMovie.getVote_count() + (mMovie.getVote_count().equals("1") ? " person rated" : " people rated"));
        viewHolder.overviewView.setText(mMovie.getOverview());
        viewHolder.releaseDateView.setText("Released on " + Utility.getFormattedDate(mMovie.getRelease_date()));


        Uri uri = Uri.parse("http://image.tmdb.org/t/p/w185/" + mMovie.getPoster_path() + "");
        Picasso.with(getContext()).load(uri).error(R.mipmap.movie_pic).into(viewHolder.iconView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String concatFavMovieIds = prefs.getString("FAVMOVIES", "");
        List<String> favMovieIdArray = Arrays.asList(TextUtils.split(concatFavMovieIds, ","));
        if (favMovieIdArray.contains(mMovie.getId())) {
            viewHolder.favView.setRating(1);
        } else {
            viewHolder.favView.setRating(0);
        }
        viewHolder.favView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                //  SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
                String concatFavMovieIds = prefs.getString("FAVMOVIES", "");
                List<String> favMovieIdArray = Arrays.asList(TextUtils.split(concatFavMovieIds, ","));
                List<String> newArray = new ArrayList<String>();
                newArray.addAll(favMovieIdArray);
                if (favMovieIdArray.contains(mMovie.getId())) {
                    if (rating == 0) {
                        newArray.remove(mMovie.getId());
                    }
                } else {
                    if (rating == 1) {
                        newArray.add(mMovie.getId());
                    }
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("FAVMOVIES", TextUtils.join(",", newArray));
                editor.commit();


            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
