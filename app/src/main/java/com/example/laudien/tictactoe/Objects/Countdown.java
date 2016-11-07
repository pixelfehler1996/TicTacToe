package com.example.laudien.tictactoe.Objects;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class Countdown {
    private TextView counterTextView;
    private CountDownTimer timer;
    private OnFinishedListener listener;
    private long time, timeNow;
    private boolean hasFinished;
    private boolean enabled;

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
                            .duration(animationDuration * 5/2)
                            .playOn(counterTextView);
                }
            }

            @Override
            public void onFinish() {
                counterTextView.setTextColor(Color.BLACK); // black text
                YoYo.with(Techniques.Shake) // shake animation
                        .duration(animationDuration * 7/2)
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
                    .duration(animationDuration * 5)
                    .playOn(counterTextView);
        Log.i("Countdown", "Timer enabled!");
    }

    public void disable(){
        if(!enabled) return; // return if timer is not enabled!
        enabled = false;
        timer.cancel();
        YoYo.with(Techniques.FlipOutX)
                .duration(animationDuration * 5)
                .playOn(counterTextView);
        Log.i("Countdown", "Timer disabled!");
    }

    public void setTime(long time){
        if(!enabled) return; // return if timer is not enabled!
        this.time = time;
        timer.cancel();
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
