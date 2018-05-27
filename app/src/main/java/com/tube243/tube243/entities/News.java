package com.tube243.tube243.entities;

import java.io.Serializable;

/**
 * Created by JonathanLesuperb on 5/15/2017.
 */

public class News implements Serializable
{
    private Long newsId;
    private Long artistId;
    private String artistName;
    private Tube tube;
    private Integer status;

    public News(Long newsId, Long artistId, String artistName, Tube tube, Integer status)
    {
        this.newsId = newsId;
        this.artistId = artistId;
        this.artistName = artistName;
        this.tube = tube;
        this.status = status;
    }

    public Long getNewsId() {
        return newsId;
    }

    public Tube getTube() {
        return tube;
    }

    public Long getArtistId() {
        return artistId;
    }

    public Integer getStatus() {
        return status;
    }

    public String getArtistName() {
        return artistName;
    }
}
