package com.example.bharath.moviesapp.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bharath.moviesapp.BuildConfig;
import com.example.bharath.moviesapp.model.Trailer;
import com.example.bharath.moviesapp.model.Trailers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Encapsulates fetching the movie's trailers from the movie db api.
 */
public class FetchTrailersTask extends AsyncTask<Long, Void, List<com.example.bharath.moviesapp.model.Trailer>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private final Listener mListener;

    /**
     * Interface definition for a callback to be invoked when trailers are loaded.
     */
    public interface Listener {
        void onFetchFinished(List<com.example.bharath.moviesapp.model.Trailer> trailers);
    }

    public FetchTrailersTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<com.example.bharath.moviesapp.model.Trailer> doInBackground(Long... params) {
        // If there's no movie id, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDatabaseService service = retrofit.create(MovieDatabaseService.class);
        Call<com.example.bharath.moviesapp.model.Trailers> call = service.findTrailersById(movieId,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<com.example.bharath.moviesapp.model.Trailers> response = call.execute();
            com.example.bharath.moviesapp.model.Trailers trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<com.example.bharath.moviesapp.model.Trailer> trailers) {
        if (trailers != null) {
            mListener.onFetchFinished(trailers);
        } else {
            mListener.onFetchFinished(new ArrayList<com.example.bharath.moviesapp.model.Trailer>());
        }
    }
}
