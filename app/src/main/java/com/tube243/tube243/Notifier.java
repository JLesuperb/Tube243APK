package com.tube243.tube243;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.tube243.tube243.entities.Tube;

/**
 * Created by JonathanLesuperb on 5/15/2017.
 */

public class Notifier
{
    private Tube tube;
    private Context context;

    public Notifier(Context context, Tube tube)
    {
        this.context=context;
        this.tube = tube;
    }

    public void show()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle("Notification Alert, Click Me!");
        builder.setContentText("Hi, This is Android Notification Detail!");

        Intent intent = new Intent(context, MediaPlayerActivity.class);
        /*intent.putExtra("tubeId",tube.getId());
        intent.putExtra("folder",tube.getFolder());
        intent.putExtra("filename",tube.getName());*/

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
