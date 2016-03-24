package app.com.example.popular_movies.model;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.com.example.popular_movies.R;
import app.com.example.popular_movies.ReviewFragment;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class ReviewsAdapter extends CursorAdapter {
    public ReviewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private Review convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor

        Review m = new Review(cursor.getString(ReviewFragment.COL_CONTENT), cursor.getString(ReviewFragment.COL_AUTHOR)
        );

        return m;
    }

    /*
Remember that these views are reused as needed.
*/
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        Review m = convertCursorRowToUXFormat(cursor);


        TextView content = (TextView) view.findViewById(R.id.review_content);
        content.setText(m.getContent());

        TextView author = (TextView) view.findViewById(R.id.author);
        author.setText(m.getAuthor());

    }
}
