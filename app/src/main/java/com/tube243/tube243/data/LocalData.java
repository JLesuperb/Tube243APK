package com.tube243.tube243.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JonathanLesuperb on 4/20/2017.
 */

public class LocalData
{
    private static final String LocalConfig = "local_config";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public LocalData(Context context)
    {
        preferences = context.getSharedPreferences(LocalConfig,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setString(String key,String value)
    {
        if(value==null)
            editor.remove(key);
        else
            editor.putString(key,value);
        editor.commit();
    }

    public void setLong(String key,Long value)
    {
        editor.putLong(key,value);
        editor.commit();
    }

    public String getString(String key){
        return preferences.getString(key,null);
    }

    public Long getLong(String key){
        Long l = preferences.getLong(key,Long.MIN_VALUE);
        return (l==Long.MIN_VALUE) ? null : l;
    }
}
