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
    public static final int NO_COLOR = -1;
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
    public static int playerColor, botColor;
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
        countdown.pause();
        soundPlayer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        countdown.start();
    }

    public void startGame(boolean aiIsUsed){
        if(aiIsUsed) {
            colorDialog.show();
        }else {
            computer = null;
            board.newGame(countdown, playerColor);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.newGame) { // click on "new game" button
            if(computer != null && difficultyChanged) {
                difficultyChanged = false;
                computer.setDifficulty(difficulty);
                difficultyTextView.setText(SettingsFragment.difficultyToString(getContext(), difficulty));
                YoYo.with(Techniques.StandUp) // stand up animation
                        .duration(animationDuration * 4)
                        .playOn(difficultyTextView);
            }
            board.newGame(countdown, (difficulty == HARD)? botColor : playerColor);
            YoYo.with(Techniques.Hinge)
                    .duration(animationDuration * 5)
                    .playOn(winnerLayout);
        }else if(v.getParent() == boardLayout){ // if any chip was clicked
            board.placeChip((ImageView) v, true);
        }else if(v.getId() == R.id.btn_red || v.getId() == R.id.btn_yellow){ // color chooser
            difficultyTextView.setText(SettingsFragment.difficultyToString(getContext(), difficulty));
            playerColor = (v.getId() == R.id.btn_red)? RED_PLAYER : YELLOW_PLAYER;
            botColor = (v.getId() == R.id.btn_red)? YELLOW_PLAYER : RED_PLAYER;
            if(computer == null)
                computer = new ArtificialIntelligence(board, difficulty, botColor);
            else
                computer.setChipColor(botColor);

            board.newGame(countdown, (difficulty == HARD)? botColor : playerColor);
            colorDialog.dismiss();
        }

    }

    @Override
    public void onGameOver(int winner) {
        Log.i("GameFragment", "Game over! Winner = " + winner);
        YoYo.with(Techniques.BounceInLeft)
                .duration(animationDuration * 5)
                .playOn(winnerLayout);
        countdown.disable();
        // bot game
        if(computer != null){
            if(winner == computer.getChipColor()) {
                soundPlayer.play(R.raw.kid_laugh);
                winnerText.setText(getString(R.string.you_lose));
            }else if(winner != RESULT_DRAW) {
                soundPlayer.play(R.raw.small_crowd_applause);
                winnerText.setText(getString(R.string.you_win));
            }else {
                soundPlayer.play(R.raw.monkeys);
                winnerText.setText(getString(R.string.draw));
            }
            return;
        }
        // player vs. player
        switch (winner){
            case RED_PLAYER:
                soundPlayer.play(R.raw.small_crowd_applause);
                winnerText.setText(getString(R.string.red_wins));
                break;
            case YELLOW_PLAYER:
                soundPlayer.play(R.raw.small_crowd_applause);
                winnerText.setText(getString(R.string.yellow_wins));
                break;
            case RESULT_DRAW:
                soundPlayer.play(R.raw.monkeys);
                winnerText.setText(getString(R.string.draw));
                break;
        }
    }

    @Override
    public void onSettingsChanged(String preference) {
        switch (preference){
            case PREFERENCE_TIME:
                countdown.setTime(sharedPreferences.getInt(preference, 20) * 1000);
                break;
            case PREFERENCE_DIFFICULTY:
                if(computer != null) { // only on bot game
                    Toast.makeText(getContext(), getString(R.string.new_difficulty), Toast.LENGTH_SHORT).show();
                }
                difficulty = sharedPreferences.getInt(preference, MEDIUM);
                difficultyChanged = true;
                break;
        }
    }
}
