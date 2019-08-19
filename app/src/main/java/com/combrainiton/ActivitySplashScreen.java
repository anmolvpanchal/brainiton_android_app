package com.combrainiton;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.combrainiton.main.ActivityNavEnterPin;
import com.combrainiton.managers.NormalQuizManagement;
import com.combrainiton.utils.AppProgressDialog;
import com.combrainiton.utils.AppSharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import pl.droidsonroids.gif.GifImageView;


public class ActivitySplashScreen extends AppCompatActivity {

    GifImageView logoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logoView = findViewById(R.id.splash_screen_logo);
        //logoView.setImageResource(R.drawable.studio); TODO set your logo image in this view
        initFirebaseServices();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getIntent().hasExtra("ACTION")) {
                    String quizID = getIntent().getStringExtra("QUIZ_ID");
                    startActivity(new Intent(ActivitySplashScreen.this, ActivityNavEnterPin.class).putExtra("QUIZ_ID",quizID));
                } else {
                    explore();
                }
            }
        }, 4560);

    }

    //for app notifications
    private void initFirebaseServices() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    private void explore() {
        boolean isLogin = new AppSharedPreference(ActivitySplashScreen.this).getBoolean("isLogin");
        if (isLogin) {
            AppProgressDialog mDialog = new AppProgressDialog(ActivitySplashScreen.this);
            mDialog.show();
            new NormalQuizManagement(ActivitySplashScreen.this, ActivitySplashScreen.this, mDialog).getAllQuiz();

        } else {
            startActivity(new Intent(ActivitySplashScreen.this, ActivityIntro.class)
                    .putExtra("from", "splesh"));
            finish();
        }
    }
}

 /*   public void permissions() {
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Get Your Location");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Get Your COARSE Location");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                StringBuilder message = new StringBuilder("You need to grant access to " + permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message.append(", ").append(permissionsNeeded.get(i));
                ActivityCompat.requestPermissions(ActivitySplashScreen.this, permissionsList.toArray(new String[0]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[0]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        splash_permission();

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(ActivitySplashScreen.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return ActivityCompat.shouldShowRequestPermissionRationale(ActivitySplashScreen.this, permission);
        }
        return true;
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[0]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);

            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                splash_permission();

            } else {

                Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                        .show();
                finish();

            }
        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    private void splash_permission() {

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 30;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);

                            if (progressStatus >= 100) {
                                permissions();
                                explore();
                            }
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }*/
