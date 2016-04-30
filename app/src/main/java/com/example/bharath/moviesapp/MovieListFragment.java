package com.example.bharath.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.example.bharath.moviesapp.util.MarginDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MovieListFragment extends Fragment {

//    private MoviesListAdapter flavorAdapter;
    private List<MovieFlavor> flavorList;
    private MovieListAdapterRecycleView mAdapter;

    private static String LOG_TAG = "MovieListFragment";
    private String prevListChoice;

    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate is called");
        Log.i(LOG_TAG, "savedInstanceState"+" "+savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movieflavors")) {
            flavorList = new ArrayList<MovieFlavor>();

        }
        else {
            flavorList = savedInstanceState.getParcelableArrayList("movieflavors");
            prevListChoice = savedInstanceState.getString("prevlistchoice");
        }
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        flavorAdapter = new MoviesListAdapter(getActivity(),new ArrayList() );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));


//        // Get a reference to the ListView, and attach this adapter to it.
//        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_flavor);
//        gridView.setAdapter(flavorAdapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                MovieFlavor movieflavor = (MovieFlavor)flavorAdapter.getItem(position);
//                Intent moviedetailintent = new Intent(getActivity(),MovieDetailActivity.class);
//                moviedetailintent.putExtra("moviedata",movieflavor);
//                getActivity().startActivity(moviedetailintent);
//
//            }
//        });
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
//        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);

//        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MovieListAdapterRecycleView(flavorList,getActivity().getApplicationContext());
        mAdapter.setOnItemClickListener(new MovieListAdapterRecycleView.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                MovieFlavor movieflavor = (MovieFlavor)mAdapter.getItem(position);
                Intent moviedetailintent = new Intent(getActivity(),MovieDetailActivity.class);
                moviedetailintent.putExtra("moviedata",movieflavor);
                getActivity().startActivity(moviedetailintent);
            }
        });
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState is called");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String movielistchoice = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        outState.putString("prevlistchoice",movielistchoice);
        if(!flavorList.isEmpty()) {
            outState.putParcelableArrayList("movieflavors", (ArrayList<MovieFlavor>) mAdapter.getMovieList());

        }
        super.onSaveInstanceState(outState);
    }

    private void updateMovieList() {
        FetchMoviesTask weatherTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String movielistchoice = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        if(prevListChoice == null){
            weatherTask.execute(movielistchoice);
        }
        else if(!prevListChoice.equals(movielistchoice)||flavorList.isEmpty()){
            weatherTask.execute(movielistchoice);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            flavorList.clear();
//            mAdapter.clearData();

            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

            updateMovieList();

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieFlavor[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private MovieFlavor[] getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_LIST = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_TITLE = "title";
            final String TMDB_BACKDROP_PATH = "backdrop_path";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";


            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = forecastJson.getJSONArray(TMDB_LIST);

            MovieFlavor[] moviesposter = new MovieFlavor[moviesArray.length()];

            for(int i = 0; i < moviesArray.length(); i++) {


                JSONObject movieForecast = moviesArray.getJSONObject(i);

                String posterpathImgStr = (String)movieForecast.get(TMDB_POSTER_PATH);

                String overview = (String)movieForecast.get(TMDB_OVERVIEW);

                String title = (String)movieForecast.get(TMDB_TITLE);

                String moviethumnailImgStr = (String)movieForecast.get(TMDB_BACKDROP_PATH);

                String movieRating  = String.valueOf(movieForecast.get(TMDB_VOTE_AVERAGE));

                String releaseDate  =(String)movieForecast.get(TMDB_RELEASE_DATE);


                posterpathImgStr = String.format("http://image.tmdb.org/t/p/w185%s",posterpathImgStr);

                moviethumnailImgStr = String.format("http://image.tmdb.org/t/p/w185%s",moviethumnailImgStr);

                moviesposter[i] = new MovieFlavor(posterpathImgStr, title, overview, releaseDate, movieRating, moviethumnailImgStr);


                System.out.println();
            }
            return moviesposter;

        }
        @Override
        protected MovieFlavor[] doInBackground(String... params) {


            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";
            String units = "metric";


            try {

                final String MOVIEPOSTER_BASE_URL =String.format("http://api.themoviedb.org/3/movie/%s?",params[0]);

                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIEPOSTER_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIESDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.i(LOG_TAG,"Fetch moviesJsonStr"+" "+moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(MovieFlavor[] moviesFlavors) {
            if (moviesFlavors != null) {
                mAdapter.clearData();

                for(int index =0; index<moviesFlavors.length;index++) {
                    mAdapter.addItem(moviesFlavors[index], index);

                }

            }
        }


    }
}

