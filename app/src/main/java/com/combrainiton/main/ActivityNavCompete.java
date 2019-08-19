package com.combrainiton.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.combrainiton.R;
import com.combrainiton.managers.NormalQuizManagement;
import com.combrainiton.utils.NetworkHandler;

public class ActivityNavCompete extends AppCompatActivity implements View.OnClickListener {

    ImageView cardImage1;
    ImageView cardImage2;
    ImageView cardImage3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_compete);
        initMainView();
        Glide.with(getApplicationContext())
                .load("http://link.brainiton.in/imgcard1")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(cardImage1);
        Glide.with(getApplicationContext())
                .load("http://link.brainiton.in/imgcard2")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(cardImage2);
        Glide.with(getApplicationContext())
                .load("http://link.brainiton.in/imgcard3")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(cardImage3);
    }


    public void initMainView(){
        findViewById(R.id.btm_nav_explore).setOnClickListener(this);
        findViewById(R.id.btm_nav_compete).setOnClickListener(this);
        findViewById(R.id.btm_nav_enter_pin).setOnClickListener(this);
        findViewById(R.id.btm_nav_my_quizzes).setOnClickListener(this);
        findViewById(R.id.btm_nav_profile).setOnClickListener(this);

        cardImage1 = findViewById(R.id.compete_card_image_1);
        cardImage1.setOnClickListener(this);
        cardImage2 = findViewById(R.id.compete_card_image_2);
        cardImage2.setOnClickListener(this);
        cardImage3 = findViewById(R.id.compete_card_image_3);
        cardImage3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btm_nav_compete:
                //do Nothing
                break;
            case R.id.btm_nav_enter_pin:
                startActivity(new Intent(ActivityNavCompete.this, ActivityNavEnterPin.class));
                break;
            case R.id.btm_nav_explore:
                explore();
                break;
            case R.id.btm_nav_my_quizzes:
                startActivity(new Intent(ActivityNavCompete.this, ActivityNavMyQuizzes.class));
                break;
            case R.id.btm_nav_profile:
                startActivity(new Intent(ActivityNavCompete.this, ActivityNavMyProfile.class));
                break;
            case R.id.compete_card_image_1:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://link.brainiton.in/txtcard1")));
                break;
            case R.id.compete_card_image_2:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://link.brainiton.in/txtcard2")));
                break;
            case R.id.compete_card_image_3:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://link.brainiton.in/txtcard3")));
                break;
        }

    }

    //this will open the home activity
    private void explore() {
        if (new NetworkHandler(getApplicationContext()).isNetworkAvailable()) { //if internet is connected
            com.combrainiton.utils.AppProgressDialog mDialog = new com.combrainiton.utils.AppProgressDialog(ActivityNavCompete.this);
            //mDialog.show(); //show the progress dialog
            // get all quiz data and start he home activity
            new NormalQuizManagement(getApplicationContext(), this, mDialog).getAllQuiz();
        } else {
            //show error if network is not connected
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_network_issue), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        explore();
    }

    public void display(View v) {
        Toast.makeText(getBaseContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
    }

}
