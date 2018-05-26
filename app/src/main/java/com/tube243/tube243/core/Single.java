package com.tube243.tube243.core;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.FrameLayout;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by JonathanLesuperb on 4/9/2017.
 */

public class Single
{
    private static final Single ourInstance = new Single();
    private FrameLayout frameLayout;
    private File parentFolder;
    private String localDBPath;

    public static Single getInstance() {
        return ourInstance;
    }

    private Single()
    {

    }

    public void setParentFolder(File parentFolder) {
        this.parentFolder = parentFolder;
    }

    public File getParentFolder() {
        return parentFolder;
    }

    public static void parsingException(Context context, Exception ex) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setTitle("Erreurs");
        String msg = "";
        msg+="\n E: "+ex.getMessage();
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Réessayer", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public void setLocalDBPath(String localDBPath) {
        this.localDBPath = localDBPath;
    }

    public Connection getConnection(String localDBPath) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + localDBPath);
    }

}
