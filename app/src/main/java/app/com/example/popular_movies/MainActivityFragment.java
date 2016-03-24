package app.com.example.popular_movies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import app.com.example.popular_movies.data.MovieContract;
import app.com.example.popular_movies.model.Movie;
import app.com.example.popular_movies.model.MovieAdapter;
import app.com.example.popular_movies.sync.MovieSyncAdapter;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
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
            //    MovieContract.MovieEntry.COLUMN_FAVOURITE,
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

    public MainActivityFragment() {
    }

    private String SELECTED_LIST_POS_KEY = "SelectedListPos";
    private static final int MOVIE_LOADER = 0;
    private MovieAdapter mMovieListAdapter;
    private List<Movie> movies;
    private int mPosition;
    private GridView lv;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //  String params =  Utility.getPreferredSortCriteria(getContext());
        Uri movieURI = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(), movieURI, MOVIE_COLUMNS, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieListAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            lv.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        onSortCriteriaChanged();
        super.onStart();
    }

    public void onSortCriteriaChanged() {
        getMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

    }

    public void getMovies() {
        MovieSyncAdapter.syncImmediately(getContext());

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "oncreate");
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            // getMovies();
            onSortCriteriaChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String PAR_KEY = "MovieId";
    private String MOVIE_KEY = "MovieList";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mMovieListAdapter = new MovieAdapter(getActivity(), null, 0);
        lv = (GridView) rootView.findViewById(R.id.movieListing);
        lv.setAdapter(mMovieListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((DetailActivityFragment.Callback) getActivity()).onItemSelected(cursor.getLong(COL_MOVIE_ID));
                    mPosition = position;


                }

            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_LIST_POS_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_LIST_POS_KEY);
        }
        return rootView;


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_LIST_POS_KEY, mPosition);
        }
        //  outState.putParcelableArrayList(MOVIE_KEY, (ArrayList<? extends Parcelable>) movies);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }


}
