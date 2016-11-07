package com.example.laudien.tictactoe;

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
        mediaPlayer = mediaPlayer.create(context, soundFile);
        mediaPlayer.start();
    }

    public void stop(){
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }
}
