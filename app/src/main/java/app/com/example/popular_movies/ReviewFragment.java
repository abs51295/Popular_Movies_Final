package app.com.example.popular_movies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import app.com.example.popular_movies.data.MovieContract;
import app.com.example.popular_movies.model.ReviewsAdapter;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class ReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = ReviewFragment.class.getSimpleName();
    private String PAR_KEY = "MovieId";

    public ReviewFragment() {

        //     setHasOptionsMenu(true);
    }

    private ReviewsAdapter mReviewsAdapter;
    private static final int REVIEWS_LOADER = 1;
    public static final String[] REVIEW_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT

    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_MOVIE_ID_PK = 0;
    public static final int COL_AUTHOR = 1;
    public static final int COL_CONTENT = 2;

    long mMovieId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mMovieId != 0) {

            //  long mMovieId = (long) intent.getData();
            Uri mUri = MovieContract.ReviewEntry.buildReviewUri(mMovieId);
            return new CursorLoader(getActivity(), mUri, REVIEW_COLUMNS, null, null, null);
        }

        //   Uri movieURI = MovieContract.MovieEntry.CONTENT_URI;

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovieId = bundle.getLong(PAR_KEY);
        }
        mReviewsAdapter = new ReviewsAdapter(getActivity(), null, 0);
        GridView lv = (GridView) rootView.findViewById(R.id.reviews_listing);
        lv.setAdapter(mReviewsAdapter);
        //RecyclerView rv= (RecyclerView) rootView.findViewById(R.id.reviewListing);
        //rv.setAdapter(mReviewsAdapter);
        //reviews_listing
        return rootView;

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            mReviewsAdapter.swapCursor(cursor);
        }

//

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReviewsAdapter.swapCursor(null);
    }
}
