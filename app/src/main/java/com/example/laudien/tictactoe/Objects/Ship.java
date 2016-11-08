package com.example.laudien.tictactoe.Objects;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.laudien.tictactoe.R;

import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class Ship {
    private Context context;
    private Board board;
    private ImageView shipImage;
    private SoundPlayer soundPlayer;

    public Ship(Context context, Board board, ImageView shipImage, SoundPlayer soundPlayer){
        this.context = context;
        this.board = board;
        this.shipImage = shipImage;
        this.soundPlayer = soundPlayer;
    }

    public void show(){
        Log.i("Ship", "Placing a random chip...");
        shipImage.setVisibility(View.VISIBLE); // make the ship visible
        board.placeRandom(); // place a random chip
        soundPlayer.play(R.raw.foghorn); // play the ship sound

        // do the animation
        YoYo.with(Techniques.FadeIn)
                .duration(animationDuration * 5/2)
                .playOn(shipImage);
        shipImage.animate().translationX(-1500f).setDuration(animationDuration * 25);

        Toast.makeText(context, "Arrr!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shipImage.setVisibility(View.INVISIBLE); // make the ship invisible again
            }
        },animationDuration * 25);
    }
}
