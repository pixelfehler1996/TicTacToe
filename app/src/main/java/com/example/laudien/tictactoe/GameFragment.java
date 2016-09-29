package com.example.laudien.tictactoe;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private int activePlayer = 0; // 0 = red, 1 = yellow player
    private int aiPlayer; // determine which player is the ai
    public final static int [][] winningPositions = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    private int[] positionState = {2,2,2,2,2,2,2,2,2}; //shows what chips are placed (0=red, 1=yellow, 2=free/no Chip)
    private View layout;
    private LinearLayout winnerLayout;
    private GridLayout board;
    private boolean gameIsRunning = true;
    private boolean aiIsUsed;
    private int winner = 2;
    private MediaPlayer mediaPlayer;
    private ImageView ship;
    private ImageView chip;
    private CountDownTimer timer;
    private TextView counterTextView;
    private Long playerTime; // time for each move
    private ArtificialIntelligence computer;
    int difficulty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_game, container, false);
        board = (GridLayout)layout.findViewById(R.id.board);
        ship = (ImageView)layout.findViewById(R.id.shipView);
        counterTextView = (TextView)layout.findViewById(R.id.counterTextView);
        winnerLayout = (LinearLayout)layout.findViewById(R.id.winnerLayout);
        sharedPreferences = this.getActivity().getSharedPreferences("com.example.laudien.tictactoe", 0);
        Button btn_newGame = (Button)layout.findViewById(R.id.newGame);
        aiIsUsed = getArguments().getBoolean(MainActivity.NAME_AI_IS_USED);

        // create computer if ai is used
        if(aiIsUsed){
            difficulty = sharedPreferences.getInt("difficulty", 1);
            computer = new ArtificialIntelligence(this, difficulty);
        }

        // set the onClick Listeners
        btn_newGame.setOnClickListener(this);
        for(int i = 0; i <= 8; i++)
            board.getChildAt(i).setOnClickListener(this);

        // start the game
        startGame();
        return layout;
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
        difficulty = sharedPreferences.getInt("difficulty", 1);
        if(computer == null)
            computer = new ArtificialIntelligence(this, difficulty);
        else
            computer.setDifficulty(difficulty);
        if(mediaPlayer != null) mediaPlayer.start();
        if(timer!= null) timer.start();
    }
    public void startGame(){
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop(); // stop the MediaPlayer if playing
        }
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.gong); // set the MediaPlayer to gong
        if(aiIsUsed)computer.resetCounter(); // reset AI
        activePlayer = 0;// red is beginning every time

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

        ship.setVisibility(View.INVISIBLE); // make the ship invisible

        // reset and (re-)start the timer
        if(timer != null) timer.cancel();
        resetTimer();
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
    public void placeChip(View view){
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
                            mediaPlayer = MediaPlayer.create(getContext(), R.raw.kid_laugh);
                            winnerMessage = getContext().getString(R.string.you_lose);
                        }else{ // player wins against ai
                            mediaPlayer = MediaPlayer.create(getContext(), R.raw.small_crowd_applause);
                            winnerMessage = getContext().getString(R.string.you_win);
                        }
                    }else { // player vs. player
                        mediaPlayer = MediaPlayer.create(getContext(), R.raw.small_crowd_applause);
                        if (winner == 1) // yellow player wins
                            winnerMessage = getContext().getString(R.string.yellow_wins);
                        else // red player wins
                            winnerMessage = getContext().getString(R.string.red_wins);
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
                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.monkeys);
                    winnerMessage = getContext().getString(R.string.draw);
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
    }
    private void showShip(){
        timer.cancel();
        int rand = new Random().nextInt(9);
        mediaPlayer = MediaPlayer.create(getContext(),R.raw.foghorn);

        ship.setVisibility(View.VISIBLE); // make the ship visable

        // get an empty field
        while (positionState[rand] != 2)
            rand = new Random().nextInt(9);
        chip = (ImageView) board.getChildAt(rand);

        mediaPlayer.start();
        ship.setTranslationX(0f);
        placeChip(chip);
        ship.animate().translationX(-1500f).setDuration(5000);
        Toast.makeText(getContext(), "Arrr!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ship.setVisibility(View.INVISIBLE); // make the ship invisable again
            }
        },5000);
    }
    private void resetTimer(){
        playerTime = (long)1000 * sharedPreferences.getInt("savedTime",20);
        timer = new CountDownTimer(100 + playerTime,1000) {
            @Override
            public void onTick(long l) {
                counterTextView.setText(Long.toString(l/1000));
            }

            @Override
            public void onFinish() {
                if(gameIsRunning) showShip();
            }
        };
    }
    public void setGameIsRunning(boolean gameIsRunning) {
        this.gameIsRunning = gameIsRunning;
    }
    public GridLayout getBoard() {
        return board;
    }
    public int[] getPositionState() {
        return positionState;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.newGame)
            startGame();
        else if(v.getParent() == board)
            placeChip(v);
    }
}
