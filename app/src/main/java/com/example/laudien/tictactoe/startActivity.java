package com.example.laudien.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class StartActivity extends AppCompatActivity
        implements GameFragment.OnFragmentInteractionListener, StartFragment.OnFragmentInteractionListener{

    SharedPreferences sharedPreferences;
    FragmentTransaction transaction;
    int activePlayer = 0; // 0 = red, 1 = yellow player
    int aiPlayer; // determine which player is the ai
    int [][] winningPositions = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    int[] positionState = {2,2,2,2,2,2,2,2,2}; //shows what chips are placed (0=red, 1=yellow, 2=free/no Chip)
    boolean gameIsRunning = true;
    boolean aiIsUsed;
    boolean isFirstRound;
    int winner = 2;
    MediaPlayer finalPlayer;
    MediaPlayer gong;
    MediaPlayer shipSound;
    ImageView ship;
    ImageView chip;
    CountDownTimer timer;
    TextView counterTextView;
    Long playerTime; // time for each move
    GridLayout board;
    int difficulty; // 0 = easy, 1 = normal, 2 = unbeatable
    ArtificialIntelligence artificialIntelligence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sharedPreferences = getSharedPreferences("com.example.laudien.tictactoe", 0);

        // get all saved values from the shared preferences
        playerTime = (long)1000 * sharedPreferences.getInt("savedTime",20);
        difficulty = sharedPreferences.getInt("difficulty", 1);

        artificialIntelligence = new ArtificialIntelligence(difficulty); // new AI object

        // load StartActivity Fragment
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new StartFragment()).commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void playerVsPlayer(View view){
        aiIsUsed = false;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new GameFragment()).commit();
        getSupportFragmentManager().executePendingTransactions(); //ensure that transaction is completed
        startGame();
    }
    public void playerVsKI(View view){
        aiIsUsed = true;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new GameFragment()).commit();
        getSupportFragmentManager().executePendingTransactions(); //ensure that transaction is completed
        startGame();
    }
    void startGame(){
        board = (GridLayout)findViewById(R.id.board);
        shipSound = MediaPlayer.create(this,R.raw.foghorn);
        ship = (ImageView)findViewById(R.id.shipView);
        finalPlayer = MediaPlayer.create(this,R.raw.small_crowd_applause);
        gong = MediaPlayer.create(this,R.raw.gong);
        isFirstRound = true;

        ship.setTranslationX(+1000f); // get the ship out of display

        // set the counterView to the set time
        counterTextView = (TextView)findViewById(R.id.counterTextView);
        counterTextView.setText(Long.toString(playerTime/1000));

        // initialize the counter
        timer = new CountDownTimer(100 + playerTime,1000) {
            @Override
            public void onTick(long l) {
                counterTextView.setText(Long.toString(l/1000));
            }

            @Override
            public void onFinish() {
                if(gameIsRunning){showShip();}
            }
        };
        gong.start(); // Play the gong sound
        timer.start(); // start the timer

        // let the KI set its stone
        if (aiIsUsed) {
            if(difficulty == 2) {
                aiPlayer = 0;
                gameIsRunning = false; // first disable the playground for user input
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        artificialIntelligence.attack();
                    }
                }, 1000);
            }else{
                aiPlayer = 1;
            }
        }
    }
    public void newGame(View view){
        //reload sharedPreferences and reset AI
        difficulty = sharedPreferences.getInt("difficulty", 1);
        artificialIntelligence.setDifficulty(difficulty);
        artificialIntelligence.resetCounter();

        // Rot beginnt wieder
        activePlayer = 0;

        // Setzt den Gewinner zurück
        winner = 2;

        // Spielfeld zurücksetzen
        for(int i = 0; i <= 8; i++){
            positionState[i] = 2;
        }

        // Chips entfernen
        GridLayout board = (GridLayout)findViewById(R.id.board);
        for(int i = 0; i <= 8; i++){
            ((ImageView)board.getChildAt(i)).setImageResource(0);
        }

        // set to firstRound
        isFirstRound = true;

        // winnerLayout wieder unsichtbar machen
        LinearLayout winnerLayout = (LinearLayout) findViewById(R.id.winnerLayout);
        winnerLayout.setVisibility(View.INVISIBLE);

        // den Applaus stoppen
        finalPlayer.stop();

        // Timer neustarten, den Countdown sichtbar machen und auf playerTime setzen
        timer.cancel();
        counterTextView.setText(Long.toString(playerTime/1000));
        counterTextView.setVisibility(View.VISIBLE);
        timer.start();

        // Gong resetten und starten
        gong.stop();
        gong = MediaPlayer.create(this,R.raw.gong);
        gong.start();

        // Gibt das Spielfeld für Benutzereingaben frei
        gameIsRunning = true;

        // let the KI set its stone
        if (aiIsUsed) {
            if(difficulty == 2) {
                aiPlayer = 0;
                gameIsRunning = false; // first disable the playground for user input
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        artificialIntelligence.attack();
                    }
                }, 1000);
            }else{
                aiPlayer = 1;
            }
        }
    }
    public void placeChip(View view){
        chip = (ImageView) view;
        TextView winnerText = (TextView) findViewById(R.id.winnerText);
        LinearLayout winnerLayout = (LinearLayout) findViewById(R.id.winnerLayout);
        String winnerMessage = "";

        if(positionState[Integer.parseInt(chip.getTag().toString())] == 2
                && gameIsRunning) {
            timer.start();
            // set chip color and animation and show animation
            chip.setTranslationY(-1000f);
            if (activePlayer == 1) {
                chip.setImageResource(R.drawable.yellow);
            }else{
                chip.setImageResource(R.drawable.red);
            }
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
                            finalPlayer = MediaPlayer.create(this, R.raw.kid_laugh);
                            winnerMessage = getString(R.string.you_lose);
                        }else{ // player wins against ai
                            finalPlayer = MediaPlayer.create(this, R.raw.small_crowd_applause);
                            winnerMessage = getString(R.string.you_win);
                        }
                    }else { // player vs. player
                        finalPlayer = MediaPlayer.create(this, R.raw.small_crowd_applause);
                        if (winner == 1) { // yellow player wins
                            winnerMessage = getString(R.string.yellow_wins);
                        }else{ // red player wins
                            winnerMessage = getString(R.string.red_wins);
                        }
                    }

                    gameIsRunning = false; // disable playground for user input
                }
            }

            // check for draw
            if (gameIsRunning && winner == 2) {
                boolean isUndecided = true;
                for (int currentPosition : positionState) {
                    if (currentPosition == 2) {
                        isUndecided = false;
                    }
                }
                // set sound and winnerMessage
                if (isUndecided) {
                    finalPlayer = MediaPlayer.create(this, R.raw.monkeys);
                    winnerMessage = getString(R.string.draw);
                    gameIsRunning = false; // disable playground for user input
                }
            }

            if (!gameIsRunning) {
                // start MediaPlayer, show winnerLayout with winnerMessage, cancel timer
                finalPlayer.start();
                timer.cancel();
                counterTextView.setVisibility(View.INVISIBLE);
                winnerText.setText(winnerMessage);
                winnerLayout.setVisibility(View.VISIBLE);
            }

            // change active player
            if (activePlayer == 1) {
                activePlayer = 0;
            } else {
                activePlayer = 1;
            }
        }
        // let the AI set its chip
        if (activePlayer == aiPlayer && aiIsUsed && gameIsRunning) {
            // first disable the playground for user input
            gameIsRunning = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    artificialIntelligence.attack();
                }
            },1000);
        }

        // set firstRound to false
        isFirstRound = false;
    }
    void showShip(){
        timer.cancel();
        int rand = new Random().nextInt(9);

        // get an empty field
        while (positionState[rand] != 2) {
            rand = new Random().nextInt(9);
        }
        chip = (ImageView) board.getChildAt(rand);

        shipSound.start();
        ship.setTranslationX(0f);
        placeChip(chip);
        ship.animate().translationX(-1000f).setDuration(5000);
    }
    class ArtificialIntelligence{
        int diff;
        int counter; // counts the moves

        public ArtificialIntelligence(int difficulty){
            setDifficulty(difficulty);
            resetCounter();
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
                while (positionState[rand] != 2) {
                    rand = new Random().nextInt(9);
                }
                placeChip(board.getChildAt(rand));
            } else if(difficulty == 1) { // medium
                position = searchPositions(1); // every time its the yellow player!

                if (position != -1) { // 1. Attack (= win the game if possible):
                    placeChip(board.getChildAt(position));
                } else { // 2. if no immediate win is possible, defense a possible win of the player:
                    position = searchPositions(0);
                    if (position != -1) {
                        placeChip(board.getChildAt(position));
                    } else { // 3. if no possible win was found, place random:
                        rand = new Random().nextInt(9);
                        while (positionState[rand] != 2) {
                            rand = new Random().nextInt(9);
                        }
                        placeChip(board.getChildAt(rand));
                    }
                }
            } else if(difficulty == 2) { // hard - every time its the red player!
                if(counter == 1 || counter == 2) {
                    placeChip(board.getChildAt(getFreeEdge()));
                }else if(counter == 3){
                    position = searchPositions(0);
                    if(position != -1){ // 1. Attack (= win the game if possible):
                        placeChip(board.getChildAt(position));
                    }else { // 2. if not immediate win is possible, defense a possible win of the player:
                        position = searchPositions(1);
                        if(position != -1){
                            placeChip(board.getChildAt(position));
                        }else{ // if no defense is necessary, place on free edge
                            placeChip(board.getChildAt(getFreeEdge()));
                        }
                    }
                }else if(counter == 4){
                    if(positionState[4] == 2){ // if middle field is empty, place there and win
                        placeChip(board.getChildAt(4));
                    }else{
                        position = searchPositions(0);
                        if(position != -1){ // 1. Attack (= win the game if possible):
                            placeChip(board.getChildAt(position));
                        }else { // 2. if not immediate win is possible, defense a possible win of the player:
                            position = searchPositions(1);
                            if(position != -1){
                                placeChip(board.getChildAt(position));
                            }else{ // if no defense is necessary, place on free edge
                                placeChip(board.getChildAt(getFreeEdge()));
                            }
                        }
                    }
                }
                else if(counter > 4){
                    position = searchPositions(0);
                    if(position != -1){ // 1. Attack (= win the game if possible):
                        placeChip(board.getChildAt(position));
                    }else { // 2. if not immediate win is possible, defense a possible win of the player:
                        position = searchPositions(1);
                        if(position != -1){
                            placeChip(board.getChildAt(position));
                        }else{ // if no defense is necessary, place on free edge
                            placeChip(board.getChildAt(getFreeEdge()));
                        }
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
                if(positionState[possiblePosition[0]] == playerID){
                    counter++;
                }
                if(positionState[possiblePosition[1]] == playerID){
                    counter++;
                }
                if(positionState[possiblePosition[2]] == playerID){
                    counter++;
                }
                if(counter >= 2){
                    // give back the position of the empty field that is needed for player with playerID to win
                    if(positionState[possiblePosition[0]] == 2){
                        return possiblePosition[0];
                    }
                    if(positionState[possiblePosition[1]] == 2){
                        return possiblePosition[1];
                    }
                    if(positionState[possiblePosition[2]] == 2){
                        return possiblePosition[2];
                    }
                }
            }
            // if nothing was found, return -1
            return -1;
        }
        int getFreeEdge(){
            // searches for the next free edge
            int[] edges = {0,2,6,8};
            for(int edge:edges){
                if(positionState[edge] == 2){
                    return edge;
                }
            }
            return -1; // if nothing was found, return -1
        }
    }
}
