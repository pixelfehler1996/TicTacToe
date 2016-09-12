package com.example.laudien.tictactoe;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    SeekBar timeSeekBar;
    SeekBar difficultySeekBar;
    TextView timeTextView;
    TextView difficultyTextView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        timeSeekBar = (SeekBar)findViewById(R.id.timeSeekBar);
        difficultySeekBar = (SeekBar)findViewById(R.id.difficultySeekBar);
        timeTextView = (TextView)findViewById(R.id.timeTextView);
        difficultyTextView = (TextView)findViewById(R.id.difficultyTextView);
        sharedPreferences = getSharedPreferences("com.example.laudien.tictactoe",0);

        // Initialize timeSeekBar
        timeSeekBar.setProgress(sharedPreferences.getInt("savedTime",20));
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

        // Initialize difficultySeekBar
        difficultySeekBar.setProgress(sharedPreferences.getInt("difficulty", 1));
        difficultyTextView.setText(difficultyToString(difficultySeekBar.getProgress()));
        difficultySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                difficultyTextView.setText(difficultyToString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    String difficultyToString(int difficulty){
        String diffString = "";
        switch (difficulty){
            case 0:
                diffString = getResources().getString(R.string.easy);
                break;
            case 1:
                diffString = getResources().getString(R.string.medium);
                break;
            case 2:
                diffString = getResources().getString(R.string.hard);
        }
        return diffString;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.edit()
                .putInt("savedTime", timeSeekBar.getProgress())
                .putInt("difficulty", difficultySeekBar.getProgress())
                .apply();
    }
}
