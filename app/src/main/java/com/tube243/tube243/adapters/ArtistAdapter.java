package com.tube243.tube243.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.tube243.tube243.R;
import com.tube243.tube243.entities.Artist;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JonathanLesuperb on 4/23/2017.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> implements Filterable
{
    private Context context;

    public void setOnArtistClickListener(OnArtistClickListener onArtistClickListener)
    {
        this.onArtistClickListener = onArtistClickListener;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public void setFilterPattern(String filterPattern)
    {
        if(!filterPattern.isEmpty())
        {
            if(artistListFiltred.size()!=0)
            {
                artistListTotal = artistList;
            }
            artistListFiltred.clear();
            for (Artist artist:artistList)
            {
                if(artist.getName().contains(filterPattern))
                {
                    artistListFiltred.add(artist);
                }
            }
            artistList = artistListFiltred;
            notifyDataSetChanged();
        }
        else
        {
            if(artistListTotal.size()>0)
            {
                artistList = artistListTotal;
                artistListTotal.clear();
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public Filter getFilter()
    {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                String charString = constraint.toString();
                if (charString.isEmpty())
                {
                    artistListFiltred = artistList;
                }
                else
                {
                    List<Artist> filteredList = new LinkedList<>();
                    for (Artist row : artistList)
                    {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()))
                        {
                            filteredList.add(row);
                        }
                    }
                    artistListFiltred = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = artistListFiltred;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                artistListFiltred = (LinkedList<Artist>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnArtistClickListener
    {
        void onClickArtist(ViewHolder holder, Artist artist);
    }

    private List<Artist> artistList;
    private List<Artist> artistListFiltred;
    private List<Artist> artistListTotal;
    private OnArtistClickListener onArtistClickListener;

    public ArtistAdapter(List<Artist> artistList)
    {
        this.artistList = artistList;
        artistListFiltred = artistList;
        artistListTotal = new ArrayList<>();
    }
    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_artist,null);
        return new ArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArtistAdapter.ViewHolder holder, int position) {
        final Artist artist = artistList.get(position);
        holder.artistTitle.setText(artist.getName());
        holder.tubeCounter.setText(artist.getCounter()+"");
        ViewCompat.setTransitionName(holder.artistImageView, "artistImage"+position);
        //String onlinePath = Params.SERVER+"/views/users/tbm/"+artist.getFolder()+"/img/"+artist.getImage();
        String onlinePath = "http://www.tube243.com/views/users/tbm/"+artist.getFolder()+"/img/"+artist.getImage();
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });
        builder.build().load(onlinePath)
                .error(R.drawable.ic_artist_cover)
                .tag(context)
                .resize(300,300)
                .placeholder(R.drawable.ic_artist_cover)
                .into(holder.artistImageView);
        /*Glide.with(context)
                .load(onlinePath)
                .error(R.drawable.ic_artist_cover)
                .placeholder(R.drawable.ic_artist_cover)
                .into(holder.artistImageView);*/
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onArtistClickListener.onClickArtist(holder,artist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistListFiltred.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout container;
        CardView artistCardView;
        public AppCompatImageView artistImageView;
        AppCompatTextView artistTitle;
        AppCompatTextView tubeCounter;
        ViewHolder(View itemView)
        {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            artistCardView = itemView.findViewById(R.id.artistCardView);
            artistImageView = itemView.findViewById(R.id.artistImageView);
            artistTitle = itemView.findViewById(R.id.artistTitle);
            tubeCounter = itemView.findViewById(R.id.tubeCounter);
        }
    }
}
