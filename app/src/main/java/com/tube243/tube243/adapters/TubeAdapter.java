package com.tube243.tube243.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.tube243.tube243.R;
import com.tube243.tube243.entities.Tube;
import com.tube243.tube243.utils.Utils;

import java.util.List;

/**
 * Created by JonathanLesuperb on 4/23/2017.
 */

public class TubeAdapter extends RecyclerView.Adapter<TubeAdapter.ViewHolder> {

    private List<Tube> tubeList;
    private Context context;
    private OnTubeClickListener onTubeClickListener;

    public TubeAdapter(List<Tube> tubeList, Context context){
        this.tubeList=tubeList;
        this.context=context;
    }

    public void setOnTubeClickListener(OnTubeClickListener onTubeClickListener) {
        this.onTubeClickListener = onTubeClickListener;
    }

    public void setFilterPattern(String filterString)
    {

    }

    public interface OnTubeClickListener
    {
        void onClickTube(ViewHolder holder, Tube tube);
    }

    @Override
    public TubeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_tube,null);
        return new TubeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TubeAdapter.ViewHolder holder, int position)
    {
        runEnterAnimation(holder.itemView);
        final Tube tube = tubeList.get(position);
        holder.tubeTitle.setText(tubeList.get(position).getName().replace(".mp3",""));
        holder.viewCounter.setText(tubeList.get(position).getCounter()+"");
        holder.tubeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTubeClickListener.onClickTube(holder,tube);
            }
        });
        ViewCompat.setTransitionName(holder.tubeImageView, "tubeImage"+position);
    }

    private void runEnterAnimation(View view) {
        view.setTranslationY(Utils.getScreenHeight(context));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public int getItemCount() {
        return tubeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView tubeCardView;
        public AppCompatImageView tubeImageView;
        AppCompatTextView tubeTitle;
        AppCompatTextView viewCounter;
        ViewHolder(View itemView) {
            super(itemView);
            tubeCardView = itemView.findViewById(R.id.tubeCardView);
            tubeImageView = itemView.findViewById(R.id.tubeImageView);
            tubeTitle = itemView.findViewById(R.id.tubeTitle);
            viewCounter = itemView.findViewById(R.id.viewCounter);
        }
    }
}
