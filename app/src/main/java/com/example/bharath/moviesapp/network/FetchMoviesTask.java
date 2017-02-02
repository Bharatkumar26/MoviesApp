package com.example.bharath.moviesapp.network;

import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.util.Log;

import com.example.bharath.moviesapp.BuildConfig;
import com.example.bharath.moviesapp.Command;
import com.example.bharath.moviesapp.model.Movie;
import com.example.bharath.moviesapp.model.Movies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FetchMoviesTask extends AsyncTask<Void, Void, List<com.example.bharath.moviesapp.model.Movie>> {


    public static String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "favorites";


    @StringDef({MOST_POPULAR, TOP_RATED, FAVORITES})
    public @interface SORT_BY {
    }


    private final NotifyAboutTaskCompletionCommand mCommand;
    private
    @SORT_BY
    String mSortBy = MOST_POPULAR;


    public interface Listener {
        void onFetchFinished(Command command);
    }


    public static class NotifyAboutTaskCompletionCommand implements Command {
        private Listener mListener;

        private List<com.example.bharath.moviesapp.model.Movie> mMovies;

        public NotifyAboutTaskCompletionCommand(Listener listener) {
            mListener = listener;
        }

        @Override
        public void execute() {
            mListener.onFetchFinished(this);
        }

        public List<com.example.bharath.moviesapp.model.Movie> getMovies() {
            return mMovies;
        }
    }

    public FetchMoviesTask(@SORT_BY String sortBy, NotifyAboutTaskCompletionCommand command) {
        mCommand = command;
        mSortBy = sortBy;
    }

    @Override
    protected void onPostExecute(List<com.example.bharath.moviesapp.model.Movie> movies) {
        if (movies != null) {
            mCommand.mMovies = movies;
        } else {
            mCommand.mMovies = new ArrayList<>();
        }
        mCommand.execute();
    }

    @Override
    protected List<com.example.bharath.moviesapp.model.Movie> doInBackground(Void... params) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

       MovieDatabaseService service = retrofit.create(MovieDatabaseService.class);
        Call<com.example.bharath.moviesapp.model.Movies> call = service.discoverMovies(mSortBy,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<com.example.bharath.moviesapp.model.Movies> response = call.execute();
            com.example.bharath.moviesapp.model.Movies movies = response.body();
            return movies.getMovies();

        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }
        return null;
    }
}
