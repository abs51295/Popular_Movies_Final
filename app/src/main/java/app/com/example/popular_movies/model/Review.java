package app.com.example.popular_movies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class Review implements Parcelable {

    private String content;
    private String author;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Review(String content, String author) {

        this.content = content;
        this.author = author;
    }

    public Review() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Review> CREATOR = new Creator<Review>() {
        public Review createFromParcel(Parcel source) {
            Review mReview = new Review();
            mReview.content = source.readString();
            mReview.author = source.readString();

            return mReview;
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(content);
        parcel.writeString(author);


    }
}
