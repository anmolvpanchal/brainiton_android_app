package com.combrainiton.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.combrainiton.R;
import com.combrainiton.main.ActivityNavExplore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class InActiveNotification extends Service {
    public int counter=0;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        //If app gets removed from background, service has to be restarted
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ToastBroadcastReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  "+ (counter++));
                sharedPreferences = getApplicationContext().getSharedPreferences("InActiveNotification", Context.MODE_PRIVATE);
                ConvertDateToReadableDate(sharedPreferences.getString("lastDate",""));
            }
        };
        timer.schedule(timerTask, 1000, 3000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public String ConvertDateToReadableDate(String DateTime) {
        if (DateTime != null) {
            if (!DateTime.equals("")) {
                // the input should be in this format 2019-03-08 15:14:29
                //if not you have to change the pattern in SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

                Date lastDate;
                Date currentDate = new Date();
                int hour = 0, min = 0, sec = 0,ch = 0,cm = 0,cs = 0;
                String dayName = "", dayNum = "", monthName = "", year = "";
                long numOfMilliSecondPassed = 0;
                float milliSecond = 86400000.0f; // 1 day is 86400000 milliseconds
                float numOfDayPass;

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                try {
                    lastDate = dateFormat.parse(DateTime); // convert String to date

                    numOfMilliSecondPassed = currentDate.getTime() - lastDate.getTime(); //get the difference in date in millisecond

                    hour = Integer.parseInt((String) android.text.format.DateFormat.format("hh", lastDate));
                    min = Integer.parseInt((String) android.text.format.DateFormat.format("mm", lastDate));
                    sec = Integer.parseInt((String) android.text.format.DateFormat.format("ss", lastDate));
                    ch = Integer.parseInt((String) android.text.format.DateFormat.format("hh", currentDate));
                    cm = Integer.parseInt((String) android.text.format.DateFormat.format("mm", currentDate));
                    cs = Integer.parseInt((String) android.text.format.DateFormat.format("ss", currentDate));
                    dayName = (String) android.text.format.DateFormat.format("EEEE", lastDate);
                    dayNum = (String) android.text.format.DateFormat.format("dd", lastDate);
                    monthName = (String) android.text.format.DateFormat.format("MMM", lastDate);
                    year = (String) android.text.format.DateFormat.format("yyyy", lastDate);


                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Convert the milliseconds to days
                numOfDayPass = (numOfMilliSecondPassed / milliSecond);

                //Staging
                if((cm - min) == 1){
                    SharedPreferences sharedPreferences = getSharedPreferences("InActiveNotification", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.remove("lastDate");
                    edit.apply();
                    edit.putString("lastDate",dateFormat.format(new Date()));
                    edit.apply();
                    createNotification();
                }

                //Production
                if(numOfDayPass == 2){
                    SharedPreferences sharedPreferences = getSharedPreferences("InActiveNotification", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.remove("lastDate");
                    edit.apply();
                    edit.putString("lastDate",dateFormat.format(new Date()));
                    edit.apply();
                    createNotification();
                }

            } else {
                return null;
            }
        } else {
            return null;
        }
        return "";
    }

    private void createNotification() {
        Intent intent = new Intent(getApplicationContext(), ActivityNavExplore.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"28+")
                .setContentTitle("Squeeze your brain again!")
                .setContentText("You haven't played since two days. Get back fast!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_app_logo_notification) //meta-data has been made in manifest.xml for icon
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}
