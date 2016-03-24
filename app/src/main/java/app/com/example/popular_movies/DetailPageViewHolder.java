package app.com.example.popular_movies;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class DetailPageViewHolder {
    /**
     * Cache of the children views for a forecast list item.
     */

    public final ImageView iconView;

    public final TextView releaseDateView;
    public final TextView overviewView;
    public final TextView voteCountView;
    public final TextView ratingView;

    public final RatingBar favView;

    public DetailPageViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.img_thumbnail);
        favView = (RatingBar) view.findViewById(R.id.favBtn);


        releaseDateView = (TextView) view.findViewById(R.id.release_date);
        overviewView = (TextView) view.findViewById(R.id.overview);
        voteCountView = (TextView) view.findViewById(R.id.vote_count);
        ratingView = (TextView) view.findViewById(R.id.ratingBarId);

    }
}
