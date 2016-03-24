package app.com.example.popular_movies.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.example.popular_movies.R;
import app.com.example.popular_movies.TrailerFragment;


/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class TrailersAdapter extends CursorAdapter {

    public TrailersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private Trailer convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor

        Trailer m = new Trailer(cursor.getString(TrailerFragment.COL_KEY), cursor.getString(TrailerFragment.COL_NAME), cursor.getString(TrailerFragment.COL_SITE), cursor.getInt(TrailerFragment.COL_SIZE));


        return m;
    }

    /*
Remember that these views are reused as needed.
*/
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        final Trailer m = convertCursorRowToUXFormat(cursor);


        TextView content = (TextView) view.findViewById(R.id.trailer_name);
        content.setText(m.getName());

        TextView author = (TextView) view.findViewById(R.id.tailer_size);
        author.setText(m.getSize().toString() + " MB");

        ImageView img = (ImageView) view.findViewById(R.id.playImg);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "http://www.example.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.youtube.com/watch?v=" + m.getKey()));
                mContext.startActivity(i);

                //v.getId() will give you the image id

            }
        });


    }
}
