package com.example.bharath.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bharath on 4/27/2016.
 */
public class MovieListAdapterRecycleView extends RecyclerView.Adapter<MovieListAdapterRecycleView.DataObjectHolder> {

    private static String LOG_TAG = "MovieListAdapterRecycleView";
    private List<MovieFlavor> mDataset;
    private static MyClickListener myClickListener;
    private Context mContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        ImageView posterImage;


        public DataObjectHolder(View itemView) {
            super(itemView);
            posterImage = (ImageView) itemView.findViewById(R.id.poster_item);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            myClickListener.onItemClick(getPosition(), v);
        }
    }
    public MovieFlavor getItem(int position) {
        return mDataset.get(position);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MovieListAdapterRecycleView(List<MovieFlavor> myDataset,Context context) {
        mDataset = myDataset;
        mContext =context;
    }

    public List<MovieFlavor> getMovieList(){
        return mDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_flavor, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        if(position>=0) {
            Picasso.with(mContext).load(mDataset.get(position).posterimage).into(holder.posterImage);
        }


    }

    public void addItem(MovieFlavor dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }
    public void clearData() {
        int size = this.mDataset.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mDataset.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}