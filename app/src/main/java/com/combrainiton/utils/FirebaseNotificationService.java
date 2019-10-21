package com.combrainiton.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.combrainiton.R;
import com.combrainiton.main.ActivityNavExplore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import android.util.Log;
import android.widget.Toast;

import retrofit2.http.Url;

public class FirebaseNotificationService extends FirebaseMessagingService {

    String title,text;
    Uri img_url;
    Uri url;

    @Override
    public void onMessageReceived(RemoteMessage message) {

        super.onMessageReceived(message);

        title = message.getNotification().getTitle();
        text = message.getNotification().getBody();
        img_url = message.getNotification().getImageUrl();
        url = message.getNotification().getImageUrl();
        Log.i("Check","innnn");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("firebase", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = Objects.requireNonNull(task.getResult()).getToken();

                        // Log and toast
                        Log.i("firebase", "firebase token " + token);
                    }
                });


        //Convert image uri to bitmap
/*
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), img_url);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        Bitmap bitmap = null;
        try {
            InputStream in = new URL(url.toString()).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, ActivityNavExplore.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"28+")
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null))
                .setSmallIcon(R.drawable.ic_app_logo_notification) //meta-data has been made in manifest.xml for icon
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                .setAutoCancel(true);

        //if image is available convert to bitmap and set it else leave
        if(bitmap != null){
            builder.setLargeIcon(bitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null));
        }



        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.i("FirebaseToken", token);

    }
}