package com.example.laudien.tictactoe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.example.laudien.tictactoe.MainActivity.ANIMATION_DURATION;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;
import static com.example.laudien.tictactoe.MainActivity.sharedPreferences;

public class SettingsFragment extends Fragment {

    SeekBar timeSeekBar, difficultySeekBar, seekBar_animation;
    TextView timeTextView, difficultyTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        timeSeekBar = (SeekBar)view.findViewById(R.id.timeSeekBar);
        difficultySeekBar = (SeekBar)view.findViewById(R.id.difficultySeekBar);
        seekBar_animation = (SeekBar)view.findViewById(R.id.seekBar_animation);
        seekBar_animation.setProgress((int)animationDuration);
        timeTextView = (TextView)view.findViewById(R.id.timeTextView);
        difficultyTextView = (TextView)view.findViewById(R.id.difficultyTextView);

        // Initialize timeSeekBar

        timeSeekBar.setProgress(sharedPreferences.getInt("savedTime", 20));
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
        return view;
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
    public void onPause() {
        super.onPause();
        animationDuration = seekBar_animation.getProgress();
        sharedPreferences.edit()
                .putInt("savedTime", timeSeekBar.getProgress())
                .putInt("difficulty", difficultySeekBar.getProgress())
                .putLong(ANIMATION_DURATION, animationDuration)
                .apply();
    }
}
