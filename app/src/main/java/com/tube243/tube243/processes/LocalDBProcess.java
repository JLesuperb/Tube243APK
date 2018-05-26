package com.tube243.tube243.processes;

import com.tube243.tube243.entities.News;
import com.tube243.tube243.entities.Tube;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by JonathanLesuperb on 5/15/2017.
 */

public class LocalDBProcess
{
    private Connection connection;

    public LocalDBProcess(String localDBPath) throws ClassNotFoundException, SQLException
    {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + localDBPath);
    }

    public void insertNews(News news) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO News (newsId, artistId, artistName, tubeId) VALUES (?,?,?,?);");
        statement.clearParameters();
        statement.setLong(1,news.getNewsId());
        statement.setLong(2,news.getArtistId());
        statement.setString(3,news.getArtistName());
        statement.setLong(4,news.getTube().getId());
        statement.execute();
    }

    public void insertTube(Tube tube) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Tubes (tubeId, tubeName, tubeFolder, tubeSize) VALUES (?,?,?,?);");
        statement.clearParameters();
        statement.setLong(1,tube.getId());
        statement.setString(2,tube.getName());
        statement.setString(3,tube.getFolder());
        statement.setString(4,tube.getSize());
        statement.execute();
    }
}
