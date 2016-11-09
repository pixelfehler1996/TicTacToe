package com.example.laudien.tictactoe.Objects;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.laudien.tictactoe.R;

import static com.example.laudien.tictactoe.MainActivity.animationDuration;
import static com.example.laudien.tictactoe.MainActivity.displayWidth;

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
        Toast.makeText(context, "Arrr!", Toast.LENGTH_SHORT).show(); // show the toast
        shipImage.setTranslationX(displayWidth); // set the ship to the right of the screen
        shipImage.animate().translationX(-1 * displayWidth).setDuration(animationDuration * 25); // ship animation

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shipImage.setVisibility(View.INVISIBLE); // make the ship invisible again
            }
        },animationDuration * 25);
    }
}
