package com.example.laudien.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class SettingsFragment extends Fragment {
    public static final String PREFERENCES = "Settings";
    public static final String PREFERENCE_TIME = "savedTime";
    public static final String PREFERENCE_DIFFICULTY = "difficulty";
    public static final String PREFERENCE_ANIMATION_DURATION = "animationDuration";

    private SeekBar timeSeekBar, difficultySeekBar, seekBar_animation;
    private TextView timeTextView, difficultyTextView;
    private int time, difficulty;
    private OnSettingsChangedListener listener;
    private SharedPreferences sharedPreferences;

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
        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        // Initialize timeSeekBar

        timeSeekBar.setProgress(sharedPreferences.getInt(PREFERENCE_TIME, 20));
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
        difficultySeekBar.setProgress(sharedPreferences.getInt(PREFERENCE_DIFFICULTY, 1));
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
        difficultySeekBar.setProgress(sharedPreferences.getInt(PREFERENCE_DIFFICULTY, 1));
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

        if(time != timeSeekBar.getProgress()){
            sharedPreferences.edit().putInt(PREFERENCE_TIME,timeSeekBar.getProgress()).apply();
            listener.onSettingsChanged(PREFERENCE_TIME);
        }
        if(difficulty != difficultySeekBar.getProgress()){
            sharedPreferences.edit().putInt(PREFERENCE_DIFFICULTY, difficultySeekBar.getProgress()).apply();
            listener.onSettingsChanged(PREFERENCE_DIFFICULTY);
        }
        if(animationDuration != seekBar_animation.getProgress()){
            animationDuration = seekBar_animation.getProgress();
            sharedPreferences.edit().putLong(PREFERENCE_ANIMATION_DURATION, animationDuration).apply();
            listener.onSettingsChanged(PREFERENCE_ANIMATION_DURATION);
        }
    }

    public void setOnSettingsChangedListener(OnSettingsChangedListener listener){
        this.listener = listener;
    }

    public interface OnSettingsChangedListener{
        void onSettingsChanged(String preference);
    }
}
