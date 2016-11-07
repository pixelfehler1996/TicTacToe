package com.example.laudien.tictactoe.Objects;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {
    private MediaPlayer mediaPlayer;
    private Context context;

    public SoundPlayer(Context context){
        this.context = context;
    }

    public void play(int soundFile){
        stop();
        mediaPlayer = MediaPlayer.create(context, soundFile);
        mediaPlayer.start();
    }

    public void stop(){
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }
}
