package com.tube243.tube243.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.entities.Tube;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JonathanLesuperb on 4/19/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tubes_db";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // create notes table
        db.execSQL(Artist.CREATE_TABLE);
        db.execSQL(Tube.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Artist.TABLE_NAME+";");
        db.execSQL("DROP TABLE IF EXISTS " + Tube.TABLE_NAME+";");

        // Create tables again
        onCreate(db);
    }

    public long insert(Artist artist)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(Artist.COLUMN_ARTIST_ID, artist.getArtistId());
        values.put(Artist.COLUMN_NAME, artist.getName());
        values.put(Artist.COLUMN_FOLDER, artist.getFolder());
        values.put(Artist.COLUMN_IMAGE, artist.getImage());
        values.put(Artist.COLUMN_COUNTER, artist.getCounter());

        // insert row
        long id = db.insert(Artist.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public long insert(Tube tube)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(Tube.COLUMN_TUBE_ID, tube.getTubeId());
        values.put(Tube.COLUMN_NAME, tube.getName());
        values.put(Tube.COLUMN_FOLDER, tube.getFolder());
        values.put(Tube.COLUMN_SIZE, tube.getSize());
        values.put(Tube.COLUMN_COUNTER, tube.getCounter());

        // insert row
        long id = db.insert(Tube.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public Artist getArtist(long id)
    {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Artist.TABLE_NAME,
                new String[]{Artist.COLUMN_ID, Artist.COLUMN_ARTIST_ID, Artist.COLUMN_NAME
                        , Artist.COLUMN_FOLDER, Artist.COLUMN_IMAGE, Artist.COLUMN_COUNTER},
                Artist.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Artist artist = new Artist(
                cursor.getLong(cursor.getColumnIndex(Artist.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(Artist.COLUMN_ARTIST_ID)),
                cursor.getString(cursor.getColumnIndex(Artist.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Artist.COLUMN_FOLDER)),
                cursor.getString(cursor.getColumnIndex(Artist.COLUMN_IMAGE)),
                cursor.getInt(cursor.getColumnIndex(Artist.COLUMN_COUNTER)));

        // close the db connection
        cursor.close();

        return artist;
    }

    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Artist.TABLE_NAME + " ORDER BY RANDOM();";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Artist artist = new Artist();
                artist.setId(cursor.getLong(cursor.getColumnIndex(Artist.COLUMN_ID)));
                artist.setArtistId(cursor.getInt(cursor.getColumnIndex(Artist.COLUMN_ARTIST_ID)));
                artist.setName(cursor.getString(cursor.getColumnIndex(Artist.COLUMN_NAME)));
                artist.setFolder(cursor.getString(cursor.getColumnIndex(Artist.COLUMN_FOLDER)));
                artist.setImage(cursor.getString(cursor.getColumnIndex(Artist.COLUMN_IMAGE)));
                artist.setCounter(cursor.getInt(cursor.getColumnIndex(Artist.COLUMN_COUNTER)));

                artists.add(artist);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return artists;
    }

    public List<Tube> getAllTubes() {
        List<Tube> tubes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tube.TABLE_NAME + " ORDER BY RANDOM();";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tube tube = new Tube();
                tube.setId(cursor.getLong(cursor.getColumnIndex(Tube.COLUMN_ID)));
                tube.setTubeId(cursor.getInt(cursor.getColumnIndex(Tube.COLUMN_TUBE_ID)));
                tube.setName(cursor.getString(cursor.getColumnIndex(Tube.COLUMN_NAME)));
                tube.setFolder(cursor.getString(cursor.getColumnIndex(Tube.COLUMN_FOLDER)));
                tube.setSize(cursor.getString(cursor.getColumnIndex(Tube.COLUMN_SIZE)));
                tube.setCounter(cursor.getInt(cursor.getColumnIndex(Tube.COLUMN_COUNTER)));

                tubes.add(tube);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return tubes;
    }

    public int getArtistsCount()
    {
        String countQuery = "SELECT  * FROM " + Artist.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int getTubesCount()
    {
        String countQuery = "SELECT  * FROM " + Tube.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateNote(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Artist.COLUMN_NAME, artist.getName());

        // updating row
        return db.update(Artist.TABLE_NAME, values, Artist.COLUMN_ID + " = ?",
                new String[]{String.valueOf(artist.getId())});
    }

    public void deleteNote(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Artist.TABLE_NAME, Artist.COLUMN_ID + " = ?",
                new String[]{String.valueOf(artist.getId())});
        db.close();
    }

    public void clearArtists()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ Artist.TABLE_NAME);
        db.close();
    }

    public void clearTubes()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ Tube.TABLE_NAME);
        db.close();
    }
}
