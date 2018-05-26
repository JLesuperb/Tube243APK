package com.tube243.tube243.entities;

/**
 * Created by JonathanLesuperb on 4/22/2017.
 */

public class Search
{
    public static String TYPE_ARTIST = "Artist";
    public static String TYPE_GROUP = "Group";
    public static String TYPE_TUBE = "Tube";
    private String title;
    private String subTitle;
    private String type;

    public Search(String title, String subTitle, String type)
    {
        this.title = title;
        this.subTitle = subTitle;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getType() {
        return type;
    }
}
