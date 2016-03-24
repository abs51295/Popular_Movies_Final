package app.com.example.popular_movies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import app.com.example.popular_movies.data.MovieContract;
import app.com.example.popular_movies.model.TrailersAdapter;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class TrailerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = TrailerFragment.class.getSimpleName();
    private String PAR_KEY = "MovieId";
    ShareActionProvider shareActionProvider;

    public TrailerFragment() {

        setHasOptionsMenu(true);
    }

    private TrailersAdapter mTrailersAdapter;
    private static final int Trailers_LOADER = 1;
    public static final String[] TRAILERS_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_SITE, MovieContract.TrailerEntry.COLUMN_SIZE,
            MovieContract.TrailerEntry.COLUMN_KEY

    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_MOVIE_ID_PK = 0;
    public static final int COL_NAME = 1;
    public static final int COL_SITE = 2;
    public static final int COL_SIZE = 3;
    public static final int COL_KEY = 4;

    long mMovieId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(Trailers_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mMovieId != 0) {

            //  long mMovieId = (long) intent.getData();
            Uri mUri = MovieContract.TrailerEntry.buildTrailerUri(mMovieId);
            return new CursorLoader(getActivity(), mUri, TRAILERS_COLUMNS, null, null, null);
        }

        //   Uri movieURI = MovieContract.MovieEntry.CONTENT_URI;

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailers, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovieId = bundle.getLong(PAR_KEY);
        }
        mTrailersAdapter = new TrailersAdapter(getActivity(), null, 0);


        ListView lv = (ListView) rootView.findViewById(R.id.trailers_listing);
        lv.setAdapter(mTrailersAdapter);

        return rootView;

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            mTrailersAdapter.swapCursor(cursor);

            if (cursor.moveToFirst()) {
                if (shareActionProvider != null) {
                    String key = cursor.getString(TrailerFragment.COL_KEY);
                    shareActionProvider.setShareIntent(createShareMovieIntent(key));
                } else {
                    // Log.e(LOG_TAG, "ShareActionProvider is null");
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailersAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuitem = menu.findItem(R.id.action_share);

        shareActionProvider = new ShareActionProvider(getActivity());
        MenuItemCompat.setActionProvider(menuitem, shareActionProvider);


        // ShareActionProvider shareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menuitem);


    }

    private Intent createShareMovieIntent(String site) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + site + " #MovieApp");
        return shareIntent;
    }
}

