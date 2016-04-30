package com.example.bharath.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharath.moviesapp.util.Util;
import com.squareup.picasso.Picasso;

/**
 * Created by Bharath on 4/24/2016.
 */
public class MovieDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        MovieFlavor movieFlavor =(MovieFlavor)arguments.getParcelable("moviedata");
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.movietitle);
        ImageView thumbnailView = (ImageView) rootView.findViewById(R.id.moviethumbnail);
        TextView date = (TextView) rootView.findViewById(R.id.moviedate);
        TextView rating = (TextView) rootView.findViewById(R.id.userrating);
        TextView overview = (TextView) rootView.findViewById(R.id.movieOverview);

        title.setText(movieFlavor.title);
        Picasso.with(getActivity()).load(movieFlavor.backdropImage).into(thumbnailView);
        date.setText(Util.getCustomizedDate(movieFlavor.releaseDate));
        rating.setText(Util.getCustomizedRating(movieFlavor.userRating));
        overview.setText(movieFlavor.overview);


        return rootView;
    }
}
