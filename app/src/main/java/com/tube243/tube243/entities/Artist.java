package com.tube243.tube243.entities;

import java.io.Serializable;

/**
 * Created by JonathanLesuperb on 4/23/2017.
 */

public class Artist implements Serializable
{
    public static final String TABLE_NAME = "artists";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ARTIST_ID = "artist_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FOLDER = "folder";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_COUNTER= "counter";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ARTIST_ID + " INTEGER ,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_FOLDER + " TEXT,"
                    + COLUMN_IMAGE + " TEXT,"
                    + COLUMN_COUNTER + " INTEGER"+ ")";

    private Long id;
    private Integer artistId;
    private String name;
    private String folder;
    private String image;
    private Integer counter;

    public Artist()
    {
        
    }

    public Artist(Integer artistId, String name, String folder, String image, Integer counter)
    {
        this.artistId = artistId;
        this.name = name;
        this.folder = folder;
        this.image = image;
        this.counter = counter;
    }

    public Artist(Long id, Integer artistId  , String name, String folder, String image, Integer counter)
    {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
        this.folder = folder;
        this.image = image;
        this.counter = counter;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCounter() {
        return counter;
    }

    public String getFolder() {
        return folder;
    }

    public String getImage() {
        return image;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Integer getArtistId() {
        return artistId;
    }
}
