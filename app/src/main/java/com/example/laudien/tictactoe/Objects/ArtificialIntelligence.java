package com.example.laudien.tictactoe.Objects;

import android.os.Handler;
import android.widget.ImageView;

import com.example.laudien.tictactoe.Objects.Board;

import static com.example.laudien.tictactoe.Objects.Board.EMPTY_FIELD;
import static com.example.laudien.tictactoe.Objects.Board.RED_PLAYER;
import static com.example.laudien.tictactoe.Objects.Board.WINNING_POSITIONS;
import static com.example.laudien.tictactoe.Objects.Board.YELLOW_PLAYER;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class ArtificialIntelligence implements Board.OnGameOverListener, Board.OnNextPlayerListener {
    private final static int NO_POSITION_FOUND = -1;
    public final static int EASY = 0;
    public final static int MEDIUM = 1;
    public final static int HARD = 2;
    private int difficulty;
    private int counter; // counts the moves
    private Board board;
    private int[] positionState;
    private int chipColor;

    public ArtificialIntelligence(Board board, int difficulty, int chipColor){
        resetCounter();
        this.board = board;
        this.chipColor = chipColor;
        setDifficulty(difficulty);
        board.addOnNextPlayerListener(this);
        board.addOnGameOverListener(this);
    }

    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    public void setChipColor(int color){
        chipColor = color;
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
            case EASY: //easy
                easy();
                break;
            case MEDIUM: // medium
                medium();
                break;
            case HARD: // hard - every time its the red player!
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

    private void hard(){ // every time its the red player (0)!
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
                // 3. if not immediate win is possible, defense a possible win of the player:
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
                // 2. if not immediate win is possible, defense a possible win of the player:
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                board.placeChip((ImageView) board.getBoardLayout().getChildAt(position), false);
                board.enableUserInput();
            }
        }, animationDuration * 5 + 100);
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
