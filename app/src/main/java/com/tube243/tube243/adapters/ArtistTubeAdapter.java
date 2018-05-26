package com.tube243.tube243.adapters;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.entities.Tube;

import java.util.List;

/**
 * Created by JonathanLesuperb on 5/3/2017.
 */

public class ArtistTubeAdapter extends RecyclerView.Adapter<ArtistTubeAdapter.ViewHolder>
{
    private final List<Tube> tubeList;
    private OnTubeListener onTubeListener;

    public ArtistTubeAdapter(List<Tube> tubeList){
        this.tubeList = tubeList;
    }

    public void setOnTubeListener(OnTubeListener onTubeListener) {
        this.onTubeListener = onTubeListener;
    }

    public interface OnTubeListener
    {
        void onTubeClick(Tube tube);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false);
        if(view.getLayoutParams().width==RecyclerView.LayoutParams.MATCH_PARENT)
            view.getLayoutParams().width = parent.getWidth();
        return new ArtistTubeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tube tube = tubeList.get(position);
        holder.songTitle.setText(tube.getName());
        holder.songTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTubeListener.onTubeClick(tube);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tubeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView songTitle;
        ViewHolder(View itemView) {
            super(itemView);
            songTitle = (AppCompatTextView)itemView.findViewById(R.id.songTitle);
        }
    }
}
