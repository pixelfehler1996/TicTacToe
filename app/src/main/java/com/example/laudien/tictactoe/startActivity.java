package com.example.laudien.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipAudioCall;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    TextView timeTextView;
    SeekBar seekBar;

    public void startGame(View view){
        SharedPreferences kiIsUsed = getSharedPreferences("kiIsUsed",0);
        SharedPreferences.Editor editor = kiIsUsed.edit();
        editor.putBoolean("kiIsUsed",false);
        editor.commit();
        this.onStop();
        startActivity(new Intent(StartActivity.this,GameActivity.class));
    }
    public void playerVsKI(View view){
        SharedPreferences kiIsUsed = getSharedPreferences("kiIsUsed",0);
        SharedPreferences.Editor editor = kiIsUsed.edit();
        editor.putBoolean("kiIsUsed",true);
        editor.commit();
        this.onStop();
        startActivity(new Intent(StartActivity.this,GameActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // reset kiIsUsed to false ==>
        SharedPreferences kiIsUsed = getSharedPreferences("kiIsUsed",0);
        SharedPreferences.Editor editor = kiIsUsed.edit();
        editor.putBoolean("kiIsUsed",false);
        editor.commit(); // <==

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        timeTextView = (TextView)findViewById(R.id.timeTextView);

        SharedPreferences savedTime = getSharedPreferences("savedTime",0);
        seekBar.setProgress(savedTime.getInt("savedTime",20));

        timeTextView.setText(Integer.toString(seekBar.getProgress()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar.getProgress() < 5){seekBar.setProgress(5);}
                timeTextView.setText(Integer.toString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences savedTime = getSharedPreferences("savedTime",0);
        SharedPreferences.Editor editor = savedTime.edit();

        editor.putInt("savedTime",seekBar.getProgress());

        editor.commit();
    }
}
