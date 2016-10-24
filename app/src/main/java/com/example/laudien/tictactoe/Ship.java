package com.example.laudien.tictactoe;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

class Ship {
    private Context context;
    private int[] positionState;
    private ConstraintLayout board;
    private ImageView imageView;
    private MediaPlayer mediaPlayer;
    private CountDownTimer timer;

    Ship(Context context, int[] positionState, ConstraintLayout board, ImageView imageView,
         MediaPlayer mediaPlayer, CountDownTimer timer){
        this.context = context;
        this.positionState = positionState;
        this.board = board;
        this.imageView = imageView;
        this.mediaPlayer = mediaPlayer;
        this.timer = timer;
    }

    ImageView show(){ // returns the chip that is placed by the ship
        ImageView chip;
        timer.cancel();
        int rand = new Random().nextInt(9);
        mediaPlayer = MediaPlayer.create(context,R.raw.foghorn);

        imageView.setVisibility(View.VISIBLE); // make the ship visable

        // get an empty field
        while (positionState[rand] != 2)
            rand = new Random().nextInt(9);
        chip = (ImageView) board.getChildAt(rand);

        mediaPlayer.start();
        imageView.setTranslationX(0f);
        imageView.animate().translationX(-1500f).setDuration(5000);
        Toast.makeText(context, "Arrr!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.INVISIBLE); // make the ship invisable again
            }
        },5000);

        return chip;
    }
}
