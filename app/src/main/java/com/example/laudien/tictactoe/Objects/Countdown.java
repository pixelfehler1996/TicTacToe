package com.example.laudien.tictactoe.Objects;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import static com.example.laudien.tictactoe.Contract.DURATION_COUNTDOWN_FLASH;
import static com.example.laudien.tictactoe.Contract.DURATION_COUNTDOWN_FLIP_IN;
import static com.example.laudien.tictactoe.Contract.DURATION_COUNTDOWN_FLIP_OUT;
import static com.example.laudien.tictactoe.Contract.DURATION_COUNTDOWN_SHAKE;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class Countdown {
    private TextView counterTextView;
    private CountDownTimer timer;
    private OnFinishedListener listener;
    private long time, timeNow;
    private boolean hasFinished, enabled;

    public Countdown(final TextView counterTextView, long time){
        this.counterTextView = counterTextView;
        this.time = time;
        createNewTimer(time);
        Log.i("Countdown", "New Countdown Class created with time = " + time);
    }

    private void createNewTimer(long time){
        hasFinished = false;
        counterTextView.setTextColor(Color.BLACK);
        if(timer != null)
            timer.cancel();
        timer = new CountDownTimer(100 + time, 1000) {
            @Override
            public void onTick(long l) {
                timeNow = l;
                counterTextView.setText(Long.toString(l/1000));
                if(l > 2000 && l < 4000) {
                    counterTextView.setTextColor(Color.RED); // red text
                    YoYo.with(Techniques.Flash) // flash animation
                            .duration(animationDuration * DURATION_COUNTDOWN_FLASH)
                            .playOn(counterTextView);
                }
            }

            @Override
            public void onFinish() {
                counterTextView.setTextColor(Color.BLACK); // black text
                YoYo.with(Techniques.Shake) // shake animation
                        .duration(animationDuration * DURATION_COUNTDOWN_SHAKE)
                        .playOn(counterTextView);
                if(listener != null) // notify the listener
                    listener.onFinish();
                hasFinished = true;
            }
        };
    }

    public void start(){
        if(!enabled) return; // return if timer is not enabled!
        if(hasFinished)
            createNewTimer(time);
        timer.start();
        Log.i("Countdown", "The timer was started!");
    }

    public void pause(){
        timer.cancel();
        createNewTimer(timeNow);
        Log.i("Countdown", "The timer was paused!");
    }

    public void reset(){
        if(!enabled) return; // return if timer is not enabled!
        if(!hasFinished) timer.cancel();
        createNewTimer(time);
        Log.i("Countdown", "Timer resetted!");
    }

    public void enable(){
        enabled = true;
        createNewTimer(time);
        if(counterTextView.getAlpha() != 1f) // show animation only if text view is not visible!
            YoYo.with(Techniques.FlipInX)
                    .duration(animationDuration * DURATION_COUNTDOWN_FLIP_IN)
                    .playOn(counterTextView);
        Log.i("Countdown", "Timer enabled!");
    }

    public void disable(){
        if(!enabled) return; // return if timer is not enabled!
        enabled = false;
        timer.cancel();
        YoYo.with(Techniques.FlipOutX)
                .duration(animationDuration * DURATION_COUNTDOWN_FLIP_OUT)
                .playOn(counterTextView);
        Log.i("Countdown", "Timer disabled!");
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setTime(long time){
        this.time = time;
        createNewTimer(time);
        Log.i("Countdown", "Time was set to " + time);
    }

    public void setOnFinishedListener(OnFinishedListener listener){
        this.listener = listener;
    }

    public interface OnFinishedListener{
        void onFinish();
    }
}
