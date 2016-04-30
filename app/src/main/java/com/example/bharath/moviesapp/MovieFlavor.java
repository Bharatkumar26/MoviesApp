package com.example.bharath.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;


public class MovieFlavor implements Parcelable{
    String posterimage;
    String title;
    String overview;
    String releaseDate;
    String userRating;
    String backdropImage;




    public MovieFlavor(String posterimage, String title, String overview, String releaseDate, String userRating, String backdropImage) {
        this.posterimage = posterimage;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.backdropImage = backdropImage;
    }

    public MovieFlavor(Parcel in) {
        this.posterimage = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.userRating = in.readString();
        this.backdropImage = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return posterimage + "--" + title + "--" + overview+ "--" + releaseDate+ "--" + userRating+ "--" + backdropImage;
    }

    @Override
    public boolean equals(Object o) {
        MovieFlavor movieFlavor =(MovieFlavor)o;
        if(movieFlavor.title.equals(this.title)){
            return true;
        }
        return false;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterimage);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(userRating);
        parcel.writeString(backdropImage);
    }

    public static final Parcelable.Creator<MovieFlavor> CREATOR = new Parcelable.Creator<MovieFlavor>() {
        @Override
        public MovieFlavor createFromParcel(Parcel parcel) {
            return new MovieFlavor(parcel);
        }

        @Override
        public MovieFlavor[] newArray(int i) {
            return new MovieFlavor[i];
        }

    };
}


