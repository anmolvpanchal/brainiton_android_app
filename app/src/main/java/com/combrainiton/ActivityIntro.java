package com.combrainiton;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.combrainiton.authentication.ActivitySignUp;

import pl.droidsonroids.gif.GifImageView;

public class ActivityIntro extends AppCompatActivity implements View.OnClickListener {

    TextView topTextView;
    GifImageView imageView;
    TextView bottomTextView;

    TextView skipButton;
    TextView nextButton;

    Button letsGoButton;

    private int imageCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        initMainView();
    }

    public void initMainView() {
        imageView = findViewById(R.id.activity_intro_image_switcher);
        topTextView = findViewById(R.id.intro_screen_image_text_top);
        bottomTextView = findViewById(R.id.intro_screen_image_text_bottom);
        nextButton = findViewById(R.id.intro_activity_next_button);
        skipButton = findViewById(R.id.intro_activity_skip_button);
        letsGoButton = findViewById(R.id.intro_activity_let_s_go_button);
        letsGoButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.intro_activity_next_button:
                loadNextImage();
                break;
            case R.id.intro_activity_skip_button:
                startSignUpActivtiy();
                break;
            case R.id.intro_activity_let_s_go_button:
                startSignUpActivtiy();
                break;
        }
    }

    public void loadNextImage() {
        imageCounter++;
        switch (imageCounter) {
            case 1:
                imageView.setImageResource(R.drawable.gif_intro_2);
                topTextView.setText(getString(R.string.intro_screen_text_top_2));
                bottomTextView.setText(getString(R.string.intro_screen_text_bottom_2));
                break;
            case 2:
                imageView.setImageResource(R.drawable.gif_intro_3);
                topTextView.setText(getString(R.string.intro_screen_text_top_3));
                bottomTextView.setText(getString(R.string.intro_screen_text_bottom_3));
                break;
        }

        if (imageCounter == 2) {
            letsGoButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
            skipButton.setVisibility(View.INVISIBLE);
        }
    }

    public void startSignUpActivtiy() {
        startActivity(new Intent(this, ActivitySignUp.class).putExtra("from", "intro"));
    }

}
