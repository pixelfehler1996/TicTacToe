package com.example.laudien.tictactoe.Objects;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import static com.example.laudien.tictactoe.Contract.EASY;
import static com.example.laudien.tictactoe.Contract.EMPTY_FIELD;
import static com.example.laudien.tictactoe.Contract.HARD;
import static com.example.laudien.tictactoe.Contract.MEDIUM;
import static com.example.laudien.tictactoe.Contract.RED_PLAYER;
import static com.example.laudien.tictactoe.Contract.WINNING_POSITIONS;
import static com.example.laudien.tictactoe.Contract.YELLOW_PLAYER;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class ArtificialIntelligence implements Board.OnGameOverListener, Board.OnNextPlayerListener {
    private static final int NO_POSITION_FOUND = -1;
    private int difficulty, counter, chipColor;
    private int[] positionState;
    private Board board;

    public ArtificialIntelligence(Board board, int difficulty, int chipColor){
        this.board = board;
        this.chipColor = chipColor;
        positionState = board.getPositionState();
        resetCounter();
        setDifficulty(difficulty);
        board.addOnNextPlayerListener(this);
        board.addOnGameOverListener(this);
    }

    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    public void setChipColor(int chipColor){
        this.chipColor = chipColor;
    }

    public int getChipColor(){
        return chipColor;
    }

    private int getEnemyColor(){
        return (getChipColor() == YELLOW_PLAYER) ? RED_PLAYER : YELLOW_PLAYER;
    }

    private void resetCounter(){
        counter = 1;
    }

    private void attack() {
        switch (difficulty) {
            case EASY: //easy
                Log.i("ArtificialIntelligence", "Doing an easy move...");
                easy();
                break;
            case MEDIUM: // medium
                Log.i("ArtificialIntelligence", "Doing a medium move...");
                medium();
                break;
            case HARD: // hard
                Log.i("ArtificialIntelligence", "Doing a hard move...");
                hard();
                break;
        }
        counter++;
    }

    private void easy() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                board.placeRandom();
            }
        }, animationDuration * 10 + 100);
    }

    private void medium(){
        int position = searchPositions(getChipColor());

        // 1. Attack (= win the game if possible):
        if (position != NO_POSITION_FOUND) {
            placeChip(position);
            return;
        }
        // 2. if no immediate win is possible, defense a possible win of the player:
        position = searchPositions(getEnemyColor());
        if (position != NO_POSITION_FOUND) {
            placeChip(position);
            return;
        }
        // 3. if no possible win was found, place random (like on easy mode):
        easy();
    }

    private void hard(){
        int position;
        int edge;
        switch (counter){
            case 1:
            case 2:
                placeChip(getFreeEdge());
                break;
            case 3:
                position = searchPositions(getChipColor());
                // 1. Attack (= win the game if possible):
                if (position != NO_POSITION_FOUND){
                    placeChip(position);
                    break;
                }
                // 2. if the player thought he can beat the hard bot, defend against it
                if(positionState[1] == getEnemyColor() && positionState[3] == getEnemyColor()) {
                    placeChip(4);
                    break;
                }
                // 3. if no immediate win is possible, defense a possible win of the player:
                position = searchPositions(getEnemyColor());
                if (position != NO_POSITION_FOUND){
                    placeChip(position);
                    break;
                }
                // 4. if no defense is necessary, place on free edge
                edge = getFreeEdge();
                if (edge != NO_POSITION_FOUND){
                    placeChip(getFreeEdge());
                    break;
                }
                // 5. if no edge was found, place random (like on easy mode)
                easy();
                break;
            case 4:
                // 1. if middle field is empty, place there and win
                if (positionState[4] == EMPTY_FIELD) {
                    placeChip(4);
                    break;
                }
                // 2. Attack (= win the game if possible):
                position = searchPositions(getChipColor());
                if (position != NO_POSITION_FOUND) {
                    placeChip(position);
                    break;
                }
                // 3. if not immediate win is possible, defend a possible win of the player:
                position = searchPositions(getEnemyColor());
                if (position != NO_POSITION_FOUND) {
                    placeChip(position);
                    break;
                }
                // 4. if no defense is necessary, place on free edge
                edge = getFreeEdge();
                if (edge != NO_POSITION_FOUND) {
                    placeChip(getFreeEdge());
                    break;
                }
                // 5. if no edge was found, place random (like on easy mode)
                easy();
                break;
            default:
                position = searchPositions(getChipColor());
                // 1. Attack (= win the gameLayout if possible):
                if (position != NO_POSITION_FOUND) {
                    placeChip(position);
                    break;
                }
                // 2. if not immediate win is possible, defend a possible win of the player:
                position = searchPositions(getEnemyColor());
                if (position != NO_POSITION_FOUND){
                    placeChip(position);
                    break;
                }
                // 3. if no defense is necessary, place on free edge
                edge = getFreeEdge();
                if(edge != NO_POSITION_FOUND) {
                    placeChip(getFreeEdge());
                    break;
                }
                // 4. if no edge was found, place random (like on easy mode)
                easy();
        }
    }

    private int searchPositions(int playerID){
        // searches for a position where are 2/3 used from the player with playerID
        int counter;
        for(int[] possiblePosition : WINNING_POSITIONS){
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
        return NO_POSITION_FOUND; // if nothing was found, return -1
    }

    private int getFreeEdge(){
        // searches for the next free edge
        int[] edges = {0,2,6,8};
        for(int edge:edges){
            if(positionState[edge] == EMPTY_FIELD)
                return edge;
        }
        return NO_POSITION_FOUND; // nothing was found
    }

    private void placeChip(final int position){
        // place the chip after all the animations are definitely finished
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                board.placeChip((ImageView) board.getBoardLayout().getChildAt(position), false);
                board.enableUserInput();
            }
        }, animationDuration * 10 + 100);
    }

    @Override
    public void onGameOver(int winner) {
        resetCounter();
    }

    @Override
    public void onNextPlayer(int activePlayer) {
        // attack if the bot is the activePlayer
        if(getChipColor() == activePlayer) {
            board.disableUserInput();
            attack();
        }
    }
}
