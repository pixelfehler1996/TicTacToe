package com.example.laudien.tictactoe;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    SeekBar timeSeekBar;
    TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        timeSeekBar = (SeekBar)findViewById(R.id.timeSeekBar);
        timeTextView = (TextView)findViewById(R.id.timeTextView);

        SharedPreferences savedTime = getSharedPreferences("savedTime",0);
        timeSeekBar.setProgress(savedTime.getInt("savedTime",20));

        timeTextView.setText(Integer.toString(timeSeekBar.getProgress()));

        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        savedTime.edit().putInt("savedTime", timeSeekBar.getProgress()).apply();
    }
}
