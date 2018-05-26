package com.tube243.tube243.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by JLesuperb on 2017-11-21.
 */

public class Utility
{
    public static int calculateNoOfColumns(Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 150);
    }

    @Nullable
    private static File getParentFolder()
    {
        File f = new File(android.os.Environment.getExternalStorageDirectory(),
                File.separator+"Tube243"+File.separator);
        if(!f.exists())
        {
            if(f.mkdirs())
            {
                return f;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return f;
        }
    }

    @Nullable
    public static File getFolder(String folder)
    {
        File parent = getParentFolder();
        if(parent!=null)
        {
            File f = new File(getParentFolder(),File.separator+folder+File.separator);
            if(!f.exists())
            {
                if(f.mkdirs())
                {
                    return f;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return f;
            }
        }
        else
        {
            return null;
        }
    }

    public static boolean cleanFolder(File folder)
    {
        boolean isDeleted = true;
        File[] files = folder.listFiles();
        for(File file : files)
        {
            if(file.exists() && file.isFile())
            {
                if(!file.delete())
                {
                    isDeleted = false;
                }
            }
        }
        return isDeleted;
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {}
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
}
