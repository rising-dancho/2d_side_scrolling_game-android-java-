package com.finals.game.flappy.perez;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN = 3500;
    //Variables
    Animation topAnim, bottomAnim;
    ImageView logo_anim;
    //fonts
    TextView hci_name,hci_dev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        //Fonts Changing
        logo_anim = (ImageView) findViewById(R.id.imageView);
        hci_name = (TextView) findViewById(R.id.textView);
        hci_dev = findViewById(R.id.textView2);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/AliceRegular.ttf");
        //hci_name.setTypeface(typeface);
        //hci_dev.setTypeface(typeface);

        //Animation Splash Screen
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        logo_anim.setAnimation(topAnim);
        hci_name.setAnimation(bottomAnim);
        //hci_dev.setAnimation(bottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}
