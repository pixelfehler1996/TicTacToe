package com.example.laudien.tictactoe.Objects;

import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.widget.ImageView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.laudien.tictactoe.R;
import java.util.ArrayList;
import java.util.Random;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class Board {
    public final static int [][] WINNING_POSITIONS = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    public final static int RED_PLAYER = 0;
    public final static int YELLOW_PLAYER = 1;
    public final static int EMPTY_FIELD = 2;
    public final static int RESULT_DRAW = 2;
    public final static int NO_RESULT_YET = 3;
    private int[] positionState = {2,2,2,2,2,2,2,2,2}; //shows what chips are placed (0=red, 1=yellow, 2=free/no Chip)
    private SoundPlayer soundPlayer;
    private ConstraintLayout boardLayout;
    private boolean gameIsRunning, userCanInteract;
    private Countdown countdown;
    private int activePlayer;
    private ArrayList<OnGameOverListener> onGameOverListeners;
    private ArrayList<OnNextPlayerListener> onNextPlayerListeners;



    public Board(ConstraintLayout boardLayout, Countdown countdown, SoundPlayer soundPlayer){
        this.boardLayout = boardLayout;
        this.countdown = countdown;
        this.soundPlayer = soundPlayer;
        onGameOverListeners = new ArrayList<>();
        onNextPlayerListeners = new ArrayList<>();
    }

    public void placeChip(ImageView chip, boolean isPlayer){
        if(!gameIsRunning) return;
        if(!userCanInteract && isPlayer) return; // don't do anything if user do not have to interact
        if(positionState[Integer.parseInt(chip.getTag().toString())] != EMPTY_FIELD) return; // return if field is not free

        Log.i("Board", "Chip placed at " + chip.getTag().toString());

        chip.setImageResource((activePlayer == RED_PLAYER) ? R.drawable.red : R.drawable.yellow); // sets the color
        YoYo.with(Techniques.BounceInDown) // chip animation
                .duration(animationDuration * 5/2)
                .playOn(chip);

        positionState[Integer.parseInt(chip.getTag().toString())] = activePlayer; // sets player in the positionState

        int winner = getWinner();
        switch (winner){
            case RED_PLAYER: // red has won
            case YELLOW_PLAYER: // yellow has won
            case RESULT_DRAW: // draw
                userCanInteract = false;
                gameIsRunning = false;
                for(OnGameOverListener listener : onGameOverListeners)
                    listener.onGameOver(winner);
                break;
            case NO_RESULT_YET: // nobody has won yet
                nextPlayer();
        }
    }

    public void placeRandom(){ // places a random chip on the board
        int rand = new Random().nextInt(9);
        while (positionState[rand] != EMPTY_FIELD)
            rand = new Random().nextInt(9);
        final int finalRand = rand;
        placeChip((ImageView) boardLayout.getChildAt(finalRand), false);
    }

    public void newGame(Countdown countdown, int firstColor){
        disableUserInput();
        activePlayer = firstColor;

        // Countdown
        this.countdown = countdown;
        countdown.enable();
        countdown.start();

        gameIsRunning = true; // set indicator that a game is running

        soundPlayer.play(R.raw.gong); // play the gong sound

        // shake the chips off the board
        YoYo.with(Techniques.Shake) // animation for the board
                .duration(animationDuration)
                .playOn(boardLayout);
        for(int i = 0; i < 9; i++)
            if(boardLayout.getChildAt(i).getAlpha() == 1f)
                YoYo.with(Techniques.TakingOff) // animation for each chip
                        .duration(animationDuration * 5)
                        .playOn(boardLayout.getChildAt(i));

        // reset positionState
        for(int i = 0; i < positionState.length; i++)
            positionState[i] = EMPTY_FIELD;

        // enable user input after all animations are finished
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enableUserInput();
            }
        }, animationDuration * 5 + 100);

        // call all the listeners
        for(OnNextPlayerListener listener : onNextPlayerListeners)
            listener.onNextPlayer(activePlayer);
    }

    public void disableUserInput(){
        userCanInteract = false;
    }

    public void enableUserInput(){
        userCanInteract = true;
    }

    public int getWinner () {
        // check if someone has won
        for (int[] winningPosition : WINNING_POSITIONS) {
            if (positionState[winningPosition[0]] == activePlayer
                    && positionState[winningPosition[1]] == activePlayer
                    && positionState[winningPosition[2]] == activePlayer)
                return activePlayer;
        }

        // if no one has won, check if any position is empty
        for (int state : positionState){
            if(state == EMPTY_FIELD){ // if any position is empty -> no one has won yet!
                return NO_RESULT_YET;
            }
        }

        // if no position is empty -> its a draw!
        return RESULT_DRAW;
    }

    public ConstraintLayout getBoardLayout(){
        return boardLayout;
    }

    public int[] getPositionState(){
        return positionState;
    }

    private void nextPlayer(){
        activePlayer = (activePlayer == RED_PLAYER) ? YELLOW_PLAYER : RED_PLAYER; // change player color
        enableUserInput(); // enable the board for player input
        countdown.reset();
        countdown.start();
        for(OnNextPlayerListener listener : onNextPlayerListeners) // call the listeners
            listener.onNextPlayer(activePlayer);
    }

    public void addOnGameOverListener(OnGameOverListener listener){
        onGameOverListeners.add(listener);
    }

    public void addOnNextPlayerListener(OnNextPlayerListener listener){
        onNextPlayerListeners.add(listener);
    }

    public interface OnGameOverListener {
        void onGameOver(int winner);
    }

    public interface OnNextPlayerListener{
        void onNextPlayer(int activePlayer);
    }
}