package com.projects.bakota.sharedapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    public static final String MyPREFERENCES = "MyPrefs" ;

    public static final String First = "cleFirst";
    public static final String Stay = "cleStay";
    CheckBox c;
    EditText e;
    Button btn;
    boolean isConnected;
    boolean first;

    private int progressStatus = 0;
    private Handler handler = new Handler();
    Button start, pause;

    private int current = 0;
    private boolean   running = true;
    private int duration = 0;
    private MediaPlayer mPlayer;
    private SeekBar mSeekBarPlayer;
    private TextView mMediaTime;


    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayer = MediaPlayer.create(this,R.raw.toofan_affairage);
        duration = mPlayer.getDuration();
        //SharedPreferences sharedPref = getPreferences((Context.MODE_PRIVATE));

        SharedPreferences sharedPref = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);

        first = sharedPref.getBoolean(First, true);
        isConnected = sharedPref.getBoolean(Stay,false);


        e= (EditText) findViewById(R.id.mail);
        c= (CheckBox) findViewById(R.id.stay);
        start = (Button) findViewById(R.id.btn_start);
        pause = (Button) findViewById(R.id.btn_pause);
        mSeekBarPlayer = (SeekBar) findViewById(R.id.seek);
        mMediaTime = (TextView) findViewById(R.id.mediaTime);

        mSeekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    //mSeekBarPlayer.setProgress(i/1000);
                    mPlayer.seekTo(i);
                    updateTime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    mPlayer.prepare();
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

                mPlayer.start();
                mSeekBarPlayer.postDelayed(onEverySecond,1000);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.pause();
            }
        });

        Toast.makeText(this,""+isConnected+" - "+first,Toast.LENGTH_LONG).show();

    }
    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run(){
            if(true == running){
                if(mSeekBarPlayer != null) {
                  mSeekBarPlayer.setProgress(mPlayer.getCurrentPosition() );
                }

                if(mPlayer.isPlaying()) {
                    mSeekBarPlayer.postDelayed(onEverySecond, 1000);
                    updateTime();
                }
            }
        }
    };

    private void updateTime(){
        do {
            current = mPlayer.getCurrentPosition();
            System.out.println("duration - " + duration + " current- "
                    + current);
            int dSeconds = (int) (duration / 1000) % 60 ;
            int dMinutes = (int) ((duration / (1000*60)) % 60);
            int dHours   = (int) ((duration / (1000*60*60)) % 24);

            int cSeconds = (int) (current / 1000) % 60 ;
            int cMinutes = (int) ((current / (1000*60)) % 60);
            int cHours   = (int) ((current / (1000*60*60)) % 24);

            if(dHours == 0){
                mMediaTime.setText(String.format("%02d:%02d / %02d:%02d", cMinutes, cSeconds, dMinutes, dSeconds));
            }else{
                mMediaTime.setText(String.format("%02d:%02d:%02d / %02d:%02d:%02d", cHours, cMinutes, cSeconds, dHours, dMinutes, dSeconds));
            }

            try{
                Log.d("Value: ", String.valueOf((int) (current * 100 / duration)));
                if(mSeekBarPlayer.getProgress() >= 100){
                    break;
                }
            }catch (Exception e) {}
        }while (mSeekBarPlayer.getProgress() <= 100);
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        // TODO Auto-generated method stub

        mSeekBarPlayer.setMax(duration/1000);
        mSeekBarPlayer.postDelayed(onEverySecond, 1000);
    }

    public void save(View view) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
       if (first){

           SharedPreferences.Editor editor = sharedpreferences.edit();

           editor.putBoolean(First, false);
           editor.putBoolean(Stay, c.isChecked());

           Toast.makeText(this,""+c.isChecked(),Toast.LENGTH_LONG).show();

           editor.commit();
       }else {
           sharedpreferences.edit().putBoolean(Stay,c.isChecked()).apply();
           sharedpreferences.edit().putBoolean(First,false).apply();
       }
    }
}
