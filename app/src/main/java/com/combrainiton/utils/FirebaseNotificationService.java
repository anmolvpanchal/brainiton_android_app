package com.combrainiton.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.combrainiton.R;
import com.combrainiton.main.ActivityNavExplore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {

        Context context = this;
        NotificationManagerCompat notificationManagerCompat;

        //super.onMessageReceived(message);
        Intent intent = new Intent(this, ActivityNavExplore.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        Notification notification = new NotificationCompat.Builder(this,"28+")
                .setContentTitle(Objects.requireNonNull(message.getNotification()).getTitle() + "")
                .setContentText(message.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_app_logo_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_logo_notification))
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(1,notification);

        Log.i("Firebase","yessssss");



        /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"28+")
                .setContentTitle(Objects.requireNonNull(message.getNotification()).getTitle() + "")
                .setContentText(message.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_app_logo_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_logo_notification))
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());*/


    }

    @Override
    public void onNewToken(String s) {
        Log.i("FirebaseToken", FirebaseInstanceId.getInstance().getToken());
    }
}

