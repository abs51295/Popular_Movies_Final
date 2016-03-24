package app.com.example.popular_movies.model;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.com.example.popular_movies.MainActivityFragment;
import app.com.example.popular_movies.R;


/**
 * {@link MovieAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private Movie convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor

        Movie m = new Movie(cursor.getString(MainActivityFragment.COL_MOVIE_ID),
                cursor.getString(MainActivityFragment.COL_MOVIE_TITLE),
                cursor.getString(MainActivityFragment.COLUMN_RELEASE_DATE),
                cursor.getString(MainActivityFragment.COLUMN_MOVIE_IMAGE),
                cursor.getString(MainActivityFragment.COLUMN_OVERVIEW),
                cursor.getString(MainActivityFragment.COLUMN_VOTE_COUNT),
                cursor.getFloat(MainActivityFragment.COLUMN_RATING));

        return m;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        Movie m = convertCursorRowToUXFormat(cursor);
        ImageView tt2 = (ImageView) view.findViewById(R.id.list_image_id);
        if (tt2 != null) {

            Uri uri = Uri.parse("http://image.tmdb.org/t/p/w185/" + m.getPoster_path() + "");
            Picasso.with(context).load(uri).into(tt2);

        }

    }
}
