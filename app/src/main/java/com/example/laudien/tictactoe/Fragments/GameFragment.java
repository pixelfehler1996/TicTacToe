package com.example.laudien.tictactoe.Fragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private SharedPreferences sharedPreferences;
    private ConstraintLayout boardLayout;
    private LinearLayout winnerLayout;
    private TextView counterTextView, winnerText;
    private SoundPlayer soundPlayer;
    private ImageView shipImage;
    private Board board;
    private Countdown countdown;
    private Ship ship;
    private ArtificialIntelligence computer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long time = sharedPreferences.getInt(PREFERENCE_TIME, 20) * 1000;

        soundPlayer = new SoundPlayer(getContext());

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
            int difficulty = sharedPreferences.getInt(PREFERENCE_DIFFICULTY, MEDIUM);
            if(computer == null)
                computer = new ArtificialIntelligence(board, difficulty,
                        (difficulty == HARD) ? RED_PLAYER : YELLOW_PLAYER);

        }else
            computer = null;
        board.newGame(countdown);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.newGame) { // click on "new game" button
            board.newGame(countdown);
            YoYo.with(Techniques.Hinge)
                    .duration(animationDuration * 5)
                    .playOn(winnerLayout);
        }else if(v.getParent() == boardLayout){ // if any chip was clicked
            board.placeChip((ImageView) v, true);
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
                if(computer != null)
                    computer.setDifficulty(sharedPreferences.getInt(preference, 1));
                break;
        }
    }
}
