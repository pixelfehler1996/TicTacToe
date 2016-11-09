package com.example.laudien.tictactoe.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.laudien.tictactoe.Objects.ArtificialIntelligence;
import com.example.laudien.tictactoe.Objects.Board;
import com.example.laudien.tictactoe.Objects.Countdown;
import com.example.laudien.tictactoe.Objects.Ship;
import com.example.laudien.tictactoe.Objects.SoundPlayer;
import com.example.laudien.tictactoe.R;
import static com.example.laudien.tictactoe.Objects.ArtificialIntelligence.HARD;
import static com.example.laudien.tictactoe.Objects.ArtificialIntelligence.MEDIUM;
import static com.example.laudien.tictactoe.Objects.Board.RED_PLAYER;
import static com.example.laudien.tictactoe.Objects.Board.RESULT_DRAW;
import static com.example.laudien.tictactoe.Objects.Board.YELLOW_PLAYER;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;
import static com.example.laudien.tictactoe.Fragments.SettingsFragment.PREFERENCES;
import static com.example.laudien.tictactoe.Fragments.SettingsFragment.PREFERENCE_DIFFICULTY;
import static com.example.laudien.tictactoe.Fragments.SettingsFragment.PREFERENCE_TIME;

public class GameFragment extends Fragment implements View.OnClickListener, Board.OnGameOverListener, SettingsFragment.OnSettingsChangedListener {

    public static int playerColor, botColor;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout boardLayout;
    private LinearLayout winnerLayout;
    private TextView counterTextView, winnerText, difficultyTextView;
    private SoundPlayer soundPlayer;
    private ImageView shipImage;
    private Board board;
    private Countdown countdown;
    private Ship ship;
    private ArtificialIntelligence computer;
    private Dialog colorDialog;
    private int difficulty;
    private boolean difficultyChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        // load data from SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long time = sharedPreferences.getInt(PREFERENCE_TIME, 20) * 1000;
        difficulty = sharedPreferences.getInt(PREFERENCE_DIFFICULTY, MEDIUM);

        // create new instance of SoundPlayer
        soundPlayer = new SoundPlayer(getContext());

        // difficulty textView
        difficultyTextView = (TextView) view.findViewById(R.id.difficulty_text_view);

        // Countdown
        counterTextView = (TextView) view.findViewById(R.id.counterTextView);
        countdown = new Countdown(counterTextView, time);
        countdown.setOnFinishedListener(new Countdown.OnFinishedListener() {
            @Override
            public void onFinish() {
                ship.show();
            }
        });

        // Board
        boardLayout = (ConstraintLayout) view.findViewById(R.id.board);
        board = new Board(boardLayout, countdown, soundPlayer);
        board.addOnGameOverListener(this);

        // Ship
        shipImage = (ImageView) view.findViewById(R.id.shipView);
        ship = new Ship(getContext(), board, shipImage, soundPlayer);

        // onClick listeners for the chips
        for(int i = 0; i < 9; i++)
            boardLayout.getChildAt(i).setOnClickListener(this);

        // winnerLayout and all it's elements
        winnerLayout = (LinearLayout) view.findViewById(R.id.winnerLayout);
        winnerLayout.setAlpha(0f);
        winnerLayout.setVisibility(View.VISIBLE);
        Button btnNewGame = (Button) view.findViewById(R.id.newGame);
        btnNewGame.setOnClickListener(this);
        winnerText = (TextView) view.findViewById(R.id.winnerText);

        // color colorDialog
        colorDialog = new Dialog(getContext());
        colorDialog.setTitle("Choose your color:");
        colorDialog.setContentView(R.layout.dialog_choose_color);
        ImageButton btn_red = (ImageButton) colorDialog.findViewById(R.id.btn_red);
        ImageButton btn_yellow = (ImageButton) colorDialog.findViewById(R.id.btn_yellow);
        btn_red.setOnClickListener(this);
        btn_yellow.setOnClickListener(this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        countdown.pause(); // pause the countdown
        soundPlayer.stop(); // stop any sound that is playing
    }

    @Override
    public void onResume() {
        super.onResume();
        countdown.start(); // start the countdown again
    }

    @Override
    public void onClick(View v) {
        if(v.getParent() == boardLayout){ // if any chip was clicked
            board.placeChip((ImageView) v, true); // place the chip on the corresponding place on the board
            return;
        }

        switch (v.getId()){
            case R.id.newGame: // "New Game" button
                // change the difficulty of the bot (if setting was changed and bot is enabled)
                if(computer != null && difficultyChanged) {
                    difficultyChanged = false; // reset the indicator
                    computer.setDifficulty(difficulty); // set the new difficulty
                    difficultyTextView.setText(SettingsFragment. // set difficulty textView
                            difficultyToString(getContext(), difficulty));
                    YoYo.with(Techniques.StandUp) // stand up animation of the difficulty textView
                            .duration(animationDuration * 4)
                            .playOn(difficultyTextView);
                }

                // start a new game and hide the winnerLayout with an animation
                board.newGame(countdown, (difficulty == HARD)? botColor : playerColor);
                YoYo.with(Techniques.Hinge)
                        .duration(animationDuration * 5)
                        .playOn(winnerLayout);
                break;
            case R.id.btn_red: // Red on color chooser
            case R.id.btn_yellow: // Yellow on color chooser
                // show the difficulty in the textView
                difficultyTextView.setText(SettingsFragment.difficultyToString(getContext(), difficulty));

                // get the colors of bot and player
                playerColor = (v.getId() == R.id.btn_red)? RED_PLAYER : YELLOW_PLAYER;
                botColor = (v.getId() == R.id.btn_red)? YELLOW_PLAYER : RED_PLAYER;

                // create a new bot with that color or set the new color on the existing bot
                if(computer == null)
                    computer = new ArtificialIntelligence(board, difficulty, botColor);
                else
                    computer.setChipColor(botColor);

                setBoardSize(); // change the size of the board layout (height = width)

                // start a new game on the board and close the color chooser
                board.newGame(countdown, (difficulty == HARD)? botColor : playerColor);
                colorDialog.dismiss();
                break;
        }
    }

    @Override
    public void onGameOver(int winner) {
        Log.i("GameFragment", "Game over! Winner = " + winner);

        // show the winnerLayout with an animation
        YoYo.with(Techniques.BounceInLeft)
                .duration(animationDuration * 5)
                .playOn(winnerLayout);

        // disable the countdown
        countdown.disable();

        // if it is a bot game
        if(computer != null){
            if(winner == computer.getChipColor()) { // if the bot has won
                soundPlayer.play(R.raw.kid_laugh);
                winnerText.setText(getString(R.string.you_lose));
            }else if(winner != RESULT_DRAW) { // if the player has won
                soundPlayer.play(R.raw.small_crowd_applause);
                winnerText.setText(getString(R.string.you_win));
            }else { // if the result is draw
                soundPlayer.play(R.raw.monkeys);
                winnerText.setText(getString(R.string.draw));
            }
            return;
        }

        // if it is player vs. player
        switch (winner){
            case RED_PLAYER: // red wins
                soundPlayer.play(R.raw.small_crowd_applause);
                winnerText.setText(getString(R.string.red_wins));
                break;
            case YELLOW_PLAYER: // yellow wins
                soundPlayer.play(R.raw.small_crowd_applause);
                winnerText.setText(getString(R.string.yellow_wins));
                break;
            case RESULT_DRAW: // draw
                soundPlayer.play(R.raw.monkeys);
                winnerText.setText(getString(R.string.draw));
                break;
        }
    }

    @Override
    public void onSettingsChanged(String preference) {
        switch (preference){
            case PREFERENCE_TIME: // if the time in the settings changed
                // set the new time in the countdown
                countdown.setTime(sharedPreferences.getInt(preference, 20) * 1000);
                break;
            case PREFERENCE_DIFFICULTY: // if the difficulty was changed
                // if it is a bot game, show this toast to the player
                if(computer != null)
                    Toast.makeText(getContext(), getString(R.string.new_difficulty), Toast.LENGTH_SHORT).show();

                // remember that this setting was changed to change it in the game on "New Game"
                difficulty = sharedPreferences.getInt(preference, MEDIUM);
                difficultyChanged = true;
                break;
        }
    }

    public void startGame(boolean aiIsUsed){
        if(aiIsUsed) { // bot game
            colorDialog.show(); // show the color chooser (it will start the game after choosing automatically)
        }else { // player vs player
            computer = null;
            board.newGame(countdown, playerColor); // start a new game
            setBoardSize();
        }
    }

    private void setBoardSize(){
        // change the size of the board layout (height = width)
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) boardLayout.getLayoutParams();
        params.height = boardLayout.getWidth();
        boardLayout.setLayoutParams(params);
    }
}
