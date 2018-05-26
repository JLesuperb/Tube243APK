package com.tube243.tube243.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.entities.Search;

import java.util.List;

/**
 * Created by JonathanLesuperb on 4/22/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>
{
    private List<Search> searches;

    public SearchAdapter(List<Search> searches){
        this.searches = searches;
    }
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_view,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        holder.searchTitle.setText(searches.get(position).getTitle());
        holder.searchSubTitle.setText(searches.get(position).getSubTitle());
    }

    @Override
    public int getItemCount() {
        return searches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        AppCompatTextView searchTitle;
        AppCompatTextView searchSubTitle;
        AppCompatImageView searchImage;

        public ViewHolder(View itemView) {
            super(itemView);
            searchTitle = (AppCompatTextView)itemView.findViewById(R.id.searchTitle);
            searchSubTitle = (AppCompatTextView)itemView.findViewById(R.id.searchSubTitle);
            searchImage = (AppCompatImageView)itemView.findViewById(R.id.searchImage);
        }
    }
}
