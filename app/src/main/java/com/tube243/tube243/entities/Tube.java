package com.tube243.tube243.entities;

/**
 * Created by JonathanLesuperb on 4/23/2017.
 */

public class Tube
{
    public static final String TABLE_NAME = "tubes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TUBE_ID = "tube_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FOLDER = "folder";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_COUNTER= "counter";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TUBE_ID + " INTEGER ,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_FOLDER + " TEXT,"
                    + COLUMN_SIZE + " TEXT,"
                    + COLUMN_COUNTER + " INTEGER"+ ")";

    private Long id;
    private Integer tubeId;
    private String name;
    private String folder;
    private String size;
    private Integer counter;

    public Tube()
    {

    }

    public Tube(Long id, String name,String folder, String size, Integer counter)
    {
        this.id = id;
        this.name = name;
        this.folder = folder;
        this.size = size;
        this.counter = counter;
    }

    public Tube(Integer tubeId, String name,String folder, String size, Integer counter)
    {
        this.id = id;
        this.tubeId = tubeId;
        this.name = name;
        this.folder = folder;
        this.size = size;
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

    public String getSize() {
        return size;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Integer getTubeId() {
        return tubeId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTubeId(int tubeId) {
        this.tubeId = tubeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
