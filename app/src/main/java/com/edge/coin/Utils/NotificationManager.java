package com.edge.coin.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;

import com.edge.coin.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NotificationManager {

    private static final String GROUP_MY_COIN= "tedPark";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannel(Context context) {

        NotificationChannelGroup group1 = new NotificationChannelGroup(GROUP_MY_COIN, GROUP_MY_COIN);
        getManager(context).createNotificationChannelGroup(group1);


        NotificationChannel channelMessage = new NotificationChannel(Channel.MESSAGE,
                context.getString(R.string.notification_channel_message_title), android.app.NotificationManager.IMPORTANCE_DEFAULT);
        channelMessage.setDescription(context.getString(R.string.notification_channel_message_description));
        channelMessage.setGroup(GROUP_MY_COIN);
        channelMessage.setLightColor(Color.GREEN);
        channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager(context).createNotificationChannel(channelMessage);

        NotificationChannel channelComment = new NotificationChannel(Channel.COMMENT,
                context.getString(R.string.notification_channel_comment_title), android.app.NotificationManager.IMPORTANCE_DEFAULT);
        channelComment.setDescription(context.getString(R.string.notification_channel_comment_description));
        channelComment.setGroup(GROUP_MY_COIN);
        channelComment.setLightColor(Color.BLUE);
        channelComment.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager(context).createNotificationChannel(channelComment);

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

        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(service, channel)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(getSmallIcon())
                    .setAutoCancel(true);
        } else {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder = new Notification.Builder(service)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSound(alarmSound)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(getSmallIcon())
                    .setAutoCancel(true);
        }
        service.startForeground(id,builder.build());

        //getManager(context).notify(id, builder.build());
    }


    private static int getSmallIcon() {
        return android.R.drawable.stat_notify_chat;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Channel.MESSAGE,
            Channel.COMMENT,
            Channel.NOTICE
    })
    public @interface Channel {
        String MESSAGE = "message";
        String COMMENT = "comment";
        String NOTICE = "notice";
    }

}