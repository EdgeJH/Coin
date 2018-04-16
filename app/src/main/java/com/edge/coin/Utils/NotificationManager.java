package com.edge.coin.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;

import com.edge.coin.MainPackage.MainActivity;
import com.edge.coin.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NotificationManager {

    private static final String GROUP_MY_COIN= "tedPark";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannel(Context context) {

        NotificationChannelGroup group1 = new NotificationChannelGroup(GROUP_MY_COIN, GROUP_MY_COIN);
        getManager(context).createNotificationChannelGroup(group1);



        NotificationChannel channelNotice = new NotificationChannel(Channel.NOTICE,
                context.getString(R.string.notification_channel_notice_title), android.app.NotificationManager.IMPORTANCE_HIGH);
        channelNotice.setDescription(context.getString(R.string.notification_channel_notice_description));
        channelNotice.setGroup(GROUP_MY_COIN);
        channelNotice.setLightColor(Color.RED);
        channelNotice.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager(context).createNotificationChannel(channelNotice);


    }

    private static android.app.NotificationManager getManager(Context context) {
        return (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void deleteChannel(Context context, @Channel String channel) {
        getManager(context).deleteNotificationChannel(channel);

    }


    public static void sendNotification(Context context, int id, @Channel String channel, String title, String body) {

        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, channel)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(getSmallIcon())
                    .setAutoCancel(true);
        } else {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSound(alarmSound)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(getSmallIcon())
                    .setAutoCancel(true);
        }


        getManager(context).notify(id, builder.build());
    }
    public static void startForgroundNoti(Service service, int id, @Channel String channel, String title, String body) {
        Intent intent = new Intent(service, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(service, channel)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColor(ContextCompat.getColor(service,R.color.line20))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(getSmallIcon())
                    .setShowWhen(true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        } else {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new Notification.Builder(service)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSound(alarmSound)
                        .setColor(ContextCompat.getColor(service,R.color.line20))
                        .setWhen(System.currentTimeMillis())
                        .setShowWhen(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSmallIcon(getSmallIcon())
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
            } else {
                builder = new Notification.Builder(service)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSound(alarmSound)
                        .setShowWhen(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(getSmallIcon())
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
            }
        }
        service.startForeground(id,builder.build());

        //getManager(context).notify(id, builder.build());
    }


    private static int getSmallIcon() {
        return R.drawable.ic_noti;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({

            Channel.NOTICE
    })
    public @interface Channel {
        String NOTICE = "notice";
    }

}