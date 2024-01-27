package com.example.storix;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // to hide the status bar
        setContentView(R.layout.activity_main);

        Animation topAnim, bottomAnim;
        ImageView logoImg;
        TextView appName, slogan;

        logoImg = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);
        slogan = findViewById(R.id.slogan);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        logoImg.setAnimation(topAnim);
        appName.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                List<Pair<View, String>> pairs = new ArrayList<>();
                pairs.add(new Pair<View, String>(logoImg, "logo_img"));
                pairs.add(new Pair<View, String>(appName, "Sto_rif"));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs.toArray(new Pair[pairs.size()]));
                Intent intent = new Intent(MainActivity.this, SignIn.class); // to move to the next activity
                startActivity(intent, options.toBundle());
            }
        }, SPLASH_SCREEN);
    }
}