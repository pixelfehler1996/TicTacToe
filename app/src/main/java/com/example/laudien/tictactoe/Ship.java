package com.example.laudien.tictactoe;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Random;

class Ship {
    private Context context;
    private Board board;
    private ImageView shipImage;
    private MediaPlayer mediaPlayer;

    public Ship(Context context, Board board, ImageView shipImage, MediaPlayer mediaPlayer){
        this.context = context;
        this.board = board;
        this.shipImage = shipImage;
        this.mediaPlayer = mediaPlayer;
    }

    public void show(){ // returns the chip that is placed by the ship
        mediaPlayer = MediaPlayer.create(context,R.raw.foghorn);

        shipImage.setVisibility(View.VISIBLE); // make the ship visible

        // get an empty field
        board.placeRandom();

        mediaPlayer.start();
        //imageView.setTranslationX(0f);
        YoYo.with(Techniques.FadeIn)
                .duration(500)
                .playOn(shipImage);
        shipImage.animate().translationX(-1500f).setDuration(5000);
        Toast.makeText(context, "Arrr!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shipImage.setVisibility(View.INVISIBLE); // make the ship invisible again
            }
        },5000);
    }
}
