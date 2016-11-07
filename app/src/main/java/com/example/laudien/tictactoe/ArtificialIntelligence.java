package com.example.laudien.tictactoe;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import static com.example.laudien.tictactoe.Board.EMPTY_FIELD;
import static com.example.laudien.tictactoe.Board.RED_PLAYER;
import static com.example.laudien.tictactoe.Board.WINNING_POSITIONS;
import static com.example.laudien.tictactoe.Board.YELLOW_PLAYER;

class ArtificialIntelligence implements Board.OnGameOverListener, Board.OnNextPlayerListener {
    private final static int NO_POSITION_FOUND = -1;
    private int difficulty;
    private int counter; // counts the moves
    private Board board;
    private int[] positionState;
    private int chipColor;

    ArtificialIntelligence(Board board, int difficulty, int chipColor){
        resetCounter();
        this.board = board;
        this.chipColor = chipColor;
        setDifficulty(difficulty);
        board.addOnNextPlayerListener(this);
        board.addOnGameOverListener(this);
    }
    void setDifficulty(int difficulty){
        this.difficulty = difficulty;
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
        positionState = board.getPositionState();

        switch (difficulty) {
            case 0: //easy
                easy();
                break;
            case 1: // medium
                medium();
                break;
            case 2: // hard - every time its the red player!
                hard();
                break;
        }
        counter++;
    }

    private void easy() {
        board.placeRandom();
    }

    private void medium(){
        int position = searchPositions(getChipColor()); // every time its the yellow player!

        if (position != NO_POSITION_FOUND) // 1. Attack (= win the game if possible):
            placeChip(position);
        else { // 2. if no immediate win is possible, defense a possible win of the player:
            position = searchPositions(getEnemyColor());
            if (position != NO_POSITION_FOUND)
                placeChip(position);
            else { // 3. if no possible win was found, place random:
                board.placeRandom();
            }
        }
    }

    private void hard(){ // every time its the red player (0)!
        int position;
        if (counter == 1 || counter == 2)
            placeChip(getFreeEdge());
        else if (counter == 3) {
            position = searchPositions(getChipColor());
            if (position != NO_POSITION_FOUND) // 1. Attack (= win the game if possible):
                placeChip(position);
            else { // 2. if not immediate win is possible, defense a possible win of the player:
                position = searchPositions(getEnemyColor());
                if (position != NO_POSITION_FOUND)
                    placeChip(position);
                else // if no defense is necessary, place on free edge
                    placeChip(getFreeEdge());
            }
        } else if (counter == 4) {
            if (positionState[4] == EMPTY_FIELD) // if middle field is empty, place there and win
                placeChip(4);
            else {
                position = searchPositions(getChipColor());
                if (position != NO_POSITION_FOUND) // 1. Attack (= win the game if possible):
                    placeChip(position);
                else { // 2. if not immediate win is possible, defense a possible win of the player:
                    position = searchPositions(getEnemyColor());
                    if (position != NO_POSITION_FOUND)
                        placeChip(position);
                    else // if no defense is necessary, place on free edge
                        placeChip(getFreeEdge());
                }
            }
        } else if (counter > 4) {
            position = searchPositions(getChipColor());
            if (position != NO_POSITION_FOUND) // 1. Attack (= win the gameLayout if possible):
                placeChip(position);
            else { // 2. if not immediate win is possible, defense a possible win of the player:
                position = searchPositions(getEnemyColor());
                if (position != NO_POSITION_FOUND)
                    placeChip(position);
                else // if no defense is necessary, place on free edge
                    placeChip(getFreeEdge());
            }
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
        return -1; // if nothing was found, return -1
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                board.placeChip((ImageView) board.getBoardLayout().getChildAt(position), false);
                board.enableUserInput();
            }
        }, 1100);
    }

    @Override
    public void onGameOver(int winner) {
        resetCounter();
    }

    @Override
    public void onNextPlayer(int activePlayer) {
        if(getChipColor() == activePlayer) {
            board.disableUserInput();
            attack();
        }
    }
}
