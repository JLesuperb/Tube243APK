package com.tube243.tube243.entities;

import java.io.Serializable;

/**
 * Created by JonathanLesuperb on 4/20/2017.
 */

public class Country implements Serializable
{
    private final Long id;
    private final String name;
    private final String pattern;
    private String code;

    public Country(Long id, String name, String code ,String pattern)
    {
        this.id=id;
        this.name=name;
        this.code=code;
        this.pattern = pattern;
    }

    public Long getId() {
        return id;
    }

    public String getPattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getCode() {
        return code;
    }
}
