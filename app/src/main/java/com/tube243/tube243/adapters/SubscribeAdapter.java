package com.tube243.tube243.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.processes.LocalImageTask;

import java.util.List;

/**
 * Created by JonathanLesuperb on 4/23/2017.
 */

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.ViewHolder>
{
    public void setOnSubscribeClickListener(OnSubscribeClickListener onSubscribeClickListener) {
        this.onSubscribeClickListener = onSubscribeClickListener;
    }

    public interface OnSubscribeClickListener
    {
        void onClickSubscribe(Artist artist);
    }

    private OnSubscribeClickListener onSubscribeClickListener;
    private final List<Artist> artistList;

    public SubscribeAdapter(List<Artist> artistList){
        this.artistList = artistList;
    }
    @Override
    public SubscribeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_studio,null);
        return new SubscribeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubscribeAdapter.ViewHolder holder, int position) {
        final Artist artist = artistList.get(position);
        holder.subscribeTitle.setText(artist.getName());
        LocalImageTask imageTask = new LocalImageTask(holder.subscribeImageView);
        imageTask.setUrlString(Params.SERVER+"/views/users/tbm/"+artist.getFolder()+"/img/"+artist.getImage());
        imageTask.execute("");
        holder.studioCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubscribeClickListener.onClickSubscribe(artist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView studioCardView;
        AppCompatImageView subscribeImageView;
        AppCompatTextView subscribeTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            studioCardView = (CardView)itemView.findViewById(R.id.studioCardView);
            subscribeImageView = (AppCompatImageView)itemView.findViewById(R.id.subscribeImageView);
            subscribeTitle = (AppCompatTextView)itemView.findViewById(R.id.subscribeTitle);
        }
    }
}
