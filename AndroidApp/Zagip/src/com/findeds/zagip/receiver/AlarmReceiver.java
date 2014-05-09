package com.findeds.zagip.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.findeds.zagip.R;
import com.findeds.zagip.Session;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    private static int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "Scheduled Travel Plan",
                System.currentTimeMillis());
        int randInt = new Random().nextInt(500) + 1;
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                randInt,
                new Intent(context, AlarmReceiver.class), 0);
        Bundle extras = intent.getExtras();
        String title = extras.getString("title");

        //here we get the title and description of our Notification
        String note = extras.getString("note");
        notification.setLatestEventInfo(context, note, title, contentIntent);
        notification.flags = Notification.FLAG_INSISTENT;
        notification.defaults |= Notification.DEFAULT_SOUND;

        //here we set the default sound for our notification
        // The PendingIntent to launch our activity if the user selects this notification
        manager.notify((int) Session.id, notification);
    }

};
