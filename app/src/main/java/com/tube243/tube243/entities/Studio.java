package com.tube243.tube243.entities;

/**
 * Created by JonathanLesuperb on 4/23/2017.
 */

public class Studio
{
    private Long id;
    private String name;
    private String image;
    private String phoneNumber;

    public Studio(Long id, String name,String image, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}
