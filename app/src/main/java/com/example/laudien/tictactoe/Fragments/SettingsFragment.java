package com.example.laudien.tictactoe.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.laudien.tictactoe.R;
import java.util.ArrayList;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;
import static com.example.laudien.tictactoe.Objects.ArtificialIntelligence.EASY;
import static com.example.laudien.tictactoe.Objects.ArtificialIntelligence.HARD;
import static com.example.laudien.tictactoe.Objects.ArtificialIntelligence.MEDIUM;

public class SettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{
    public static final String PREFERENCES = "Settings";
    public static final String PREFERENCE_TIME = "savedTime";
    public static final String PREFERENCE_DIFFICULTY = "difficulty";
    public static final String PREFERENCE_ANIMATION_DURATION = "animationDuration";
    public static final int TIME_DEF = 10;
    private static final int TIME_MIN = 2;
    public static final int ANIMATION_DURATION_DEF = 100;
    private static final int ANIMATION_DURATION_MIN = 50;
    private SeekBar timeSeekBar, difficultySeekBar, seekBar_animation;
    private TextView timeTextView, difficultyTextView, animationTextView;
    private int time, difficulty;
    private ArrayList<OnSettingsChangedListener> listeners;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listeners = new ArrayList<>(); // initialize the list of the OnSettingsChanged listeners
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        // timeSeekBar
        time = sharedPreferences.getInt(PREFERENCE_TIME, TIME_DEF);
        timeSeekBar = (SeekBar)view.findViewById(R.id.timeSeekBar);
        timeTextView = (TextView)view.findViewById(R.id.timeTextView);
        timeSeekBar.setOnSeekBarChangeListener(this);
        timeSeekBar.setProgress(time);
        timeTextView.setText(Integer.toString(timeSeekBar.getProgress()));

        // difficultySeekBar
        difficulty = sharedPreferences.getInt(PREFERENCE_DIFFICULTY, MEDIUM);
        difficultySeekBar = (SeekBar)view.findViewById(R.id.difficultySeekBar);
        difficultyTextView = (TextView)view.findViewById(R.id.difficultyTextView);
        difficultySeekBar.setProgress(difficulty);
        difficultySeekBar.setOnSeekBarChangeListener(this);
        difficultyTextView.setText(difficultyToString(getContext(), difficultySeekBar.getProgress()));

        // seekbar_animation
        seekBar_animation = (SeekBar)view.findViewById(R.id.seekBar_animation);
        animationTextView = (TextView) view.findViewById(R.id.animationTextView);
        seekBar_animation.setProgress((int)animationDuration/2);
        seekBar_animation.setOnSeekBarChangeListener(this);
        animationTextView.setText((double)animationDuration/(ANIMATION_DURATION_DEF*2) + "x");

        return view;
    }

    public static String difficultyToString(Context context, int difficulty){ // converts a difficulty into a string
        switch (difficulty){
            case EASY: return context.getResources().getString(R.string.easy);
            case MEDIUM: return context.getResources().getString(R.string.medium);
            case HARD: return context.getResources().getString(R.string.hard);
        }
        return null; // if the difficulty given is not existent, return null
    }

    @Override
    public void onPause() {
        super.onPause();

        if(time != timeSeekBar.getProgress()){
            time = timeSeekBar.getProgress();
            sharedPreferences.edit().putInt(PREFERENCE_TIME,time).apply();
            Log.i("SettingsFragment", "Time was changed to " + time);
            for(OnSettingsChangedListener listener : listeners)
                listener.onSettingsChanged(PREFERENCE_TIME);
        }
        if(difficulty != difficultySeekBar.getProgress()){
            difficulty = difficultySeekBar.getProgress();
            sharedPreferences.edit().putInt(PREFERENCE_DIFFICULTY, difficulty).apply();
            Log.i("SettingsFragment", "Difficulty changed to " + difficulty);
            for(OnSettingsChangedListener listener : listeners)
                listener.onSettingsChanged(PREFERENCE_DIFFICULTY);
        }
        if(animationDuration/2 != seekBar_animation.getProgress()){
            animationDuration = seekBar_animation.getProgress() * 2;
            sharedPreferences.edit().putLong(PREFERENCE_ANIMATION_DURATION, animationDuration).apply();
            Log.i("SettingsFragment", "Animation duration changed to " + animationDuration);
            for(OnSettingsChangedListener listener : listeners)
                listener.onSettingsChanged(PREFERENCE_ANIMATION_DURATION);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()){
            case R.id.timeSeekBar:
                if(seekBar.getProgress() < TIME_MIN){seekBar.setProgress(TIME_MIN);} // set minimum
                timeTextView.setText(Integer.toString(seekBar.getProgress()));
                break;
            case R.id.difficultySeekBar:
                difficultyTextView.setText(difficultyToString(getContext(), i));
                break;
            case R.id.seekBar_animation:
                if(i < ANIMATION_DURATION_MIN) seekBar.setProgress(ANIMATION_DURATION_MIN); // set minimum
                double newTime = (double) seekBar.getProgress() / ANIMATION_DURATION_DEF;
                animationTextView.setText(newTime + "x");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void addOnSettingsChangedListener(OnSettingsChangedListener listener){
        listeners.add(listener);
    }

    public interface OnSettingsChangedListener{
        void onSettingsChanged(String preference);
    }
}
