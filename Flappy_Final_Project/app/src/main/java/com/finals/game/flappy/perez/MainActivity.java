package com.finals.game.flappy.perez;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    HomeWatcher mHomeWatcher;
    //Bind/Unbind music service
    private boolean mIsBound = false;
    private MusicService mServ;
    private boolean isMute;
    TextView gamePlay, gameTutorial, gameScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //BIND Music Service
        doBindService();
        final Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        //Start HomeWatcher
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();


        //Fonts Changing
        gamePlay = (TextView) findViewById(R.id.play);
        gameTutorial = (TextView) findViewById(R.id.tutorial);
        gameScore = (TextView) findViewById(R.id.highScoreTxt);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/PressStart2PRegular.ttf");
        gamePlay.setTypeface(typeface);
        gameScore.setTypeface(typeface);
        gameTutorial.setTypeface(typeface);


        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });

        findViewById(R.id.tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Tutorial.class));
            }
        });

        //HighScore
        final SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        gameScore.setText("High Score:" + prefs.getInt("highscore", 0));

        //Sound : icon toggle
        isMute = prefs.getBoolean("isMute", false);
        final ImageView volumeCtrl = findViewById(R.id.volumeCtrl);
        final ImageView musicCtrl = findViewById(R.id.bgMusicCtrl);

        if (isMute) {
            volumeCtrl.setImageResource(R.drawable.ic_volume_off);
            musicCtrl.setImageResource(R.drawable.ic_music_mute);
        } else {
            volumeCtrl.setImageResource(R.drawable.ic_volume_up);
            musicCtrl.setImageResource(R.drawable.ic_music_note);
        }


        volumeCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                if (isMute)
                    volumeCtrl.setImageResource(R.drawable.ic_volume_off);
                else
                    volumeCtrl.setImageResource(R.drawable.ic_volume_up);

                //Saving status of volume button in Sharedprefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMute", isMute);
                editor.apply();

            }
        });

        musicCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                if (isMute) {
                    musicCtrl.setImageResource(R.drawable.ic_music_mute);
                    mServ.pauseMusic();
                } else {
                    musicCtrl.setImageResource(R.drawable.ic_music_note);
                    mServ.resumeMusic();
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMute", isMute);
                editor.apply();
            }
        });
    }


    //Background Music Components
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };


    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Detect idle screen
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }
        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //UNBIND music service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);
    }
}
