package com.tube243.tube243.processes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by JonathanLesuperb on 11/27/2016.
 */

public class LocalImageTask extends AsyncTask<String,Void,Bitmap>
{
    private final AppCompatImageView imageView;
    private String urlString;

    public LocalImageTask(AppCompatImageView imageView)
    {
        this.imageView = imageView;
    }
    public void setUrlString(String urlString)
    {
        this.urlString = urlString;
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        String url = urlString;
        Bitmap bitmap = null;
        try
        {
            InputStream inputStream = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
        if(result!=null)
            imageView.setImageBitmap(result);
    }
}
