package app.com.example.popular_movies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class Movie implements Parcelable {
    private String id;
    private String title;
    private String release_date;
    private String poster_path;
    private String overview;
    private String vote_count;
    private float rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getRelease_date() {
        return release_date;
    }


    public String getOverview() {
        return overview;
    }


    public float getRating() {
        return rating;
    }


    public String getVote_count() {
        return vote_count;
    }


    public String getPoster_path() {
        return poster_path;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Movie(String movie_id, String title, String release_date, String poster_path, String overview, String vote_count, float rating) {
        this.id = movie_id;
        this.title = title;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_count = vote_count;
        this.rating = rating;
    }


    public Movie() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            Movie mBook = new Movie();
            mBook.title = source.readString();
            mBook.poster_path = source.readString();
            mBook.overview = source.readString();
            mBook.vote_count = source.readString();
            mBook.rating = source.readFloat();
            mBook.release_date = source.readString();
            mBook.id = source.readString();
            return mBook;
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(vote_count);
        parcel.writeFloat(rating);
        parcel.writeString(release_date);
        parcel.writeString(id);

    }
}
