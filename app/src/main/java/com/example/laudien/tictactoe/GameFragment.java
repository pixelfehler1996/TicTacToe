package com.example.laudien.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class GameFragment extends Fragment {
    static SharedPreferences sharedPreferences;
    static int activePlayer = 0; // 0 = red, 1 = yellow player
    static int aiPlayer; // determine which player is the ai
    static int [][] winningPositions = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    static int[] positionState = {2,2,2,2,2,2,2,2,2}; //shows what chips are placed (0=red, 1=yellow, 2=free/no Chip)
    static View layout;
    static LinearLayout winnerLayout;
    static GridLayout board;
    static boolean gameIsRunning = true;
    static boolean aiIsUsed;
    static boolean isFirstRound;
    static int winner = 2;
    static MediaPlayer mediaPlayer;
    static ImageView ship;
    static ImageView chip;
    static CountDownTimer timer;
    static TextView counterTextView;
    static Long playerTime; // time for each move
    static int difficulty; // 0 = easy, 1 = normal, 2 = unbeatable
    static ArtificialIntelligence computer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_game,null);
        board = (GridLayout)layout.findViewById(R.id.board);
        ship = (ImageView)layout.findViewById(R.id.shipView);
        counterTextView = (TextView)layout.findViewById(R.id.counterTextView);
        winnerLayout = (LinearLayout)layout.findViewById(R.id.winnerLayout);
        sharedPreferences = this.getActivity().getSharedPreferences("com.example.laudien.tictactoe", 0);
        return layout;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aiIsUsed = getArguments().getBoolean(StartActivity.NAME_AI_IS_USED);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(mediaPlayer != null) mediaPlayer.pause();
        if(timer != null) timer.cancel();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mediaPlayer != null) mediaPlayer.start();
        if(timer!= null) timer.start();
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public static void startGame(Context context){
        mediaPlayer = MediaPlayer.create(context, R.raw.gong); // set the MediaPlayer to gong

        // reload sharedPreferences and reset AI
        difficulty = sharedPreferences.getInt("difficulty", 1);
        if(computer != null) {
            computer.setDifficulty(difficulty);
            computer.resetCounter();
        }else
            computer = new ArtificialIntelligence(difficulty, context);

        activePlayer = 0;// red is beginning every time
        isFirstRound = true; // set to firstRound
        mediaPlayer.stop(); // stop the MediaPlayer

        // reset winner and positionStates
        winner = 2;
        for(int i = 0; i <= 8; i++)
            positionState[i] = 2;

        // remove chips
        for(int i = 0; i <= 8; i++)
            ((ImageView)board.getChildAt(i)).setImageResource(0);

        // make winnerLayout invisible and the countdown visible
        winnerLayout.setVisibility(View.INVISIBLE);
        counterTextView.setVisibility(View.VISIBLE);

        ship.setTranslationX(+1000f); // get the ship out of display

        // reset and (re-)start the timer
        if(timer != null) timer.cancel();
        resetTimer(context);
        counterTextView.setText(Long.toString(playerTime/1000)); // set the counterTextView to the set time
        timer.start();

        mediaPlayer.start(); // play the gong sound

        // let the KI set its chip
        if (aiIsUsed) {
            if(difficulty == 2) {
                aiPlayer = 0;
                gameIsRunning = false; // first disable the playground for user input
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        computer.attack();
                    }
                }, 1000);
            }else
                aiPlayer = 1;
        }

        gameIsRunning = true; // enable playground for user input
    }
    public static void placeChip(View view, Context context){
        chip = (ImageView) view;
        TextView winnerText = (TextView) layout.findViewById(R.id.winnerText);
        winnerLayout = (LinearLayout) layout.findViewById(R.id.winnerLayout);
        String winnerMessage = "";

        if(positionState[Integer.parseInt(chip.getTag().toString())] == 2
                && gameIsRunning) {
            timer.start();
            // set chip color and animation and show animation
            chip.setTranslationY(-1000f);
            if (activePlayer == 1)
                chip.setImageResource(R.drawable.yellow);
            else
                chip.setImageResource(R.drawable.red);
            chip.animate().translationY(0f).rotation(3600).setDuration(300);

            // save the positionState of the position that was clicked to the used color
            positionState[Integer.parseInt(chip.getTag().toString())] = activePlayer;

            // check if someone has won
            for (int[] winningPosition : winningPositions) {
                if (positionState[winningPosition[0]] == activePlayer
                        && positionState[winningPosition[1]] == activePlayer
                        && positionState[winningPosition[2]] == activePlayer) {

                    winner = activePlayer;

                    // set sound and winnerMessage
                    if(aiIsUsed){ // bot game
                        if(aiPlayer == winner){ // ai wins against player
                            mediaPlayer = MediaPlayer.create(context, R.raw.kid_laugh);
                            winnerMessage = context.getString(R.string.you_lose);
                        }else{ // player wins against ai
                            mediaPlayer = MediaPlayer.create(context, R.raw.small_crowd_applause);
                            winnerMessage = context.getString(R.string.you_win);
                        }
                    }else { // player vs. player
                        mediaPlayer = MediaPlayer.create(context, R.raw.small_crowd_applause);
                        if (winner == 1) // yellow player wins
                            winnerMessage = context.getString(R.string.yellow_wins);
                        else // red player wins
                            winnerMessage = context.getString(R.string.red_wins);
                    }

                    gameIsRunning = false; // disable playground for user input
                }
            }

            // check for draw
            if (gameIsRunning && winner == 2) {
                boolean isUndecided = true;
                for (int currentPosition : positionState) {
                    if (currentPosition == 2)
                        isUndecided = false;
                }
                // set sound and winnerMessage
                if (isUndecided) {
                    mediaPlayer = MediaPlayer.create(context, R.raw.monkeys);
                    winnerMessage = context.getString(R.string.draw);
                    gameIsRunning = false; // disable playground for user input
                }
            }

            if (!gameIsRunning) {
                // start MediaPlayer, show winnerLayout with winnerMessage, cancel timer
                mediaPlayer.start();
                timer.cancel();
                counterTextView.setVisibility(View.INVISIBLE);
                winnerText.setText(winnerMessage);
                winnerLayout.setVisibility(View.VISIBLE);
            }

            // change active player
            if (activePlayer == 1)
                activePlayer = 0;
            else
                activePlayer = 1;
        }
        // let the AI set its chip
        if (activePlayer == aiPlayer && aiIsUsed && gameIsRunning) {
            // first disable the playground for user input
            gameIsRunning = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    computer.attack();
                }
            },1000);
        }

        // set firstRound to false
        isFirstRound = false;
    }
    static void showShip(Context context){
        timer.cancel();
        int rand = new Random().nextInt(9);
        mediaPlayer = MediaPlayer.create(context,R.raw.foghorn);

        // get an empty field
        while (positionState[rand] != 2)
            rand = new Random().nextInt(9);
        chip = (ImageView) board.getChildAt(rand);

        mediaPlayer.start();
        ship.setTranslationX(0f);
        placeChip(chip, context);
        ship.animate().translationX(-1000f).setDuration(5000);
    }
    static void resetTimer(final Context context){
        playerTime = (long)1000 * sharedPreferences.getInt("savedTime",20);
        timer = new CountDownTimer(100 + playerTime,1000) {
            @Override
            public void onTick(long l) {
                counterTextView.setText(Long.toString(l/1000));
            }

            @Override
            public void onFinish() {
                if(gameIsRunning) showShip(context);
            }
        };
    }
    static class ArtificialIntelligence{
        int diff;
        int counter; // counts the moves
        Context context;

        public ArtificialIntelligence(int difficulty, Context con){
            setDifficulty(difficulty);
            resetCounter();
            context = con;
        }
        public void setDifficulty(int difficulty){
            diff = difficulty;
        }
        public void resetCounter(){
            counter = 1;
        }
        public void attack() {
            int position;
            int rand = 0;

            // enable the playground
            gameIsRunning = true;

            if(difficulty == 0) { //easy
                // just place random
                rand = new Random().nextInt(9);
                while (positionState[rand] != 2)
                    rand = new Random().nextInt(9);
                placeChip(board.getChildAt(rand), context);
            } else if(difficulty == 1) { // medium
                position = searchPositions(1); // every time its the yellow player!

                if (position != -1) // 1. Attack (= win the game if possible):
                    placeChip(board.getChildAt(position), context);
                else { // 2. if no immediate win is possible, defense a possible win of the player:
                    position = searchPositions(0);
                    if (position != -1)
                        placeChip(board.getChildAt(position), context);
                    else { // 3. if no possible win was found, place random:
                        rand = new Random().nextInt(9);
                        while (positionState[rand] != 2)
                            rand = new Random().nextInt(9);
                        placeChip(board.getChildAt(rand), context);
                    }
                }
            } else if(difficulty == 2) { // hard - every time its the red player!
                if(counter == 1 || counter == 2)
                    placeChip(board.getChildAt(getFreeEdge()), context);
                else if(counter == 3){
                    position = searchPositions(0);
                    if(position != -1) // 1. Attack (= win the game if possible):
                        placeChip(board.getChildAt(position), context);
                    else { // 2. if not immediate win is possible, defense a possible win of the player:
                        position = searchPositions(1);
                        if(position != -1)
                            placeChip(board.getChildAt(position), context);
                        else // if no defense is necessary, place on free edge
                            placeChip(board.getChildAt(getFreeEdge()), context);
                    }
                }else if(counter == 4){
                    if(positionState[4] == 2) // if middle field is empty, place there and win
                        placeChip(board.getChildAt(4), context);
                    else{
                        position = searchPositions(0);
                        if(position != -1) // 1. Attack (= win the game if possible):
                            placeChip(board.getChildAt(position), context);
                        else { // 2. if not immediate win is possible, defense a possible win of the player:
                            position = searchPositions(1);
                            if(position != -1)
                                placeChip(board.getChildAt(position), context);
                            else // if no defense is necessary, place on free edge
                                placeChip(board.getChildAt(getFreeEdge()), context);
                        }
                    }
                }
                else if(counter > 4){
                    position = searchPositions(0);
                    if(position != -1) // 1. Attack (= win the game if possible):
                        placeChip(board.getChildAt(position), context);
                    else { // 2. if not immediate win is possible, defense a possible win of the player:
                        position = searchPositions(1);
                        if(position != -1)
                            placeChip(board.getChildAt(position), context);
                        else // if no defense is necessary, place on free edge
                            placeChip(board.getChildAt(getFreeEdge()), context);
                    }
                }
            }
            counter++;
        }
        int searchPositions(int playerID){
            // searches for a position where are 2/3 used from the player with playerID
            int counter;
            for(int[] possiblePosition : winningPositions){
                counter = 0;
                if(positionState[possiblePosition[0]] == playerID)
                    counter++;
                if(positionState[possiblePosition[1]] == playerID)
                    counter++;
                if(positionState[possiblePosition[2]] == playerID)
                    counter++;
                if(counter >= 2){
                    // give back the position of the empty field that is needed for player with playerID to win
                    if(positionState[possiblePosition[0]] == 2)
                        return possiblePosition[0];
                    if(positionState[possiblePosition[1]] == 2)
                        return possiblePosition[1];
                    if(positionState[possiblePosition[2]] == 2)
                        return possiblePosition[2];
                }
            }
            return -1; // if nothing was found, return -1
        }
        int getFreeEdge(){
            // searches for the next free edge
            int[] edges = {0,2,6,8};
            for(int edge:edges){
                if(positionState[edge] == 2)
                    return edge;
            }
            return -1; // if nothing was found, return -1
        }
    }
}
