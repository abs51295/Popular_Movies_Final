package app.com.example.popular_movies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class Trailer implements Parcelable {

    private String key;
    private String name;
    private String site;
    private Integer size;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Trailer(String key, String name, String site, Integer size) {

        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
    }

    public Trailer() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel source) {
            Trailer mTrailer = new Trailer();

            mTrailer.key = source.readString();
            mTrailer.name = source.readString();
            mTrailer.site = source.readString();
            mTrailer.size = source.readInt();

            return mTrailer;
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };


    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeInt(size);


    }
}
