package com.getrent.timer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.getrent.timer.recever.NotificationActionReceiver;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class NotificationUtil {

    private String CHANNEL_ID_TIMER = "menu_timer";
    private String CHANNEL_NAME_TIMER = "Timer App Timer";
    private int TIMER_ID = 0;
    //MainActivity activity = new MainActivity();

    public void showTimerExpired(Context context){
        Intent startIntent =new Intent(context, NotificationActionReceiver.class);
        startIntent.setAction(AppConstants.ACTION_START);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer Expired!")
                .setContentText("Set time and Start again?");

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nManager.createNotificationChannel(createChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER));
        }
        nManager.notify(TIMER_ID, nBuilder.build());
//        activity.callShare();
//        activity.setProgressBarValues();
    }
    private NotificationCompat.Builder getBasicNotificationBuilder(Context context, String channelId,Boolean playSound) {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .setDefaults(0);
        if (playSound) nBuilder.setSound(notificationSound);
        return nBuilder;
    }

    private PendingIntent getPendingIntentWithStack(Context context, Class<MainActivity> javaClass){
        Intent resultIntent = new Intent(context, javaClass);
        resultIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(javaClass);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void showTimerRunning(Context context){
        Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
        stopIntent.setAction( AppConstants.ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(context, NotificationActionReceiver.class);
        pauseIntent.setAction(AppConstants.ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer is Running.")
                    .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("EDMT channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            nManager.createNotificationChannel(notificationChannel);
        }
        nManager.notify(TIMER_ID, nBuilder.build());


//        activity.callShare();
//        activity.setProgressBarValues();
    }


    public void showTimerPaused(Context context){
//        activity.callShare();
//        activity.setProgressBarValues();
        Intent resumeIntent =new Intent(context, NotificationActionReceiver.class);
        resumeIntent.setAction(AppConstants.ACTION_RESUME);
        PendingIntent resumePendingIntent = PendingIntent.getBroadcast(context, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer is paused.")
                .setContentText("Resume?")

                    .setOngoing(true)
                .addAction(R.drawable.ic_play, "Resume", resumePendingIntent);



        NotificationManager nManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nManager.createNotificationChannel(createChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER));
        }

        nManager.notify(TIMER_ID, nBuilder.build());
    }

    public void hideTimerNotification(Context context){
        NotificationManager nManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(TIMER_ID);
    }


    private NotificationChannel createChannel(String channelID, String channelName){
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("EDMT channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        }
        return notificationChannel;
    }



}
