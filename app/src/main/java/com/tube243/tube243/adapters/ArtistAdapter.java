package com.tube243.tube243.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tube243.tube243.R;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Artist;

import java.util.List;

/**
 * Created by JonathanLesuperb on 4/23/2017.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder>
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

    public interface OnArtistClickListener
    {
        void onClickArtist(ViewHolder holder, Artist artist);
    }

    private final List<Artist> artistList;
    private OnArtistClickListener onArtistClickListener;

    public ArtistAdapter(List<Artist> artistList){
        this.artistList = artistList;
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
        final AppCompatImageView imageView = holder.artistImageView;
        String onlinePath = Params.SERVER+"/views/users/tbm/"+artist.getFolder()+"/img/"+artist.getImage();

        onlinePath = "http://192.168.8.101/training/assets/images/avatars/avatar.jpg";
        onlinePath = "http://www.tube243.com/views/users/"+artist.getFolder()+"/tbm8cd/img/"+artist.getImage();
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                //exception.printStackTrace();
                Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
                Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
                Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        builder.build().load(onlinePath).into(holder.artistImageView);
        /*Picasso.with(context)
                .load(onlinePath)
                .error(R.drawable.ic_artist_cover)
                .placeholder(R.drawable.ic_artist_cover)
                .into(holder.artistImageView);*/
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                artist.setImageBitmap(bitmap);
                onArtistClickListener.onClickArtist(holder,artist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
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
