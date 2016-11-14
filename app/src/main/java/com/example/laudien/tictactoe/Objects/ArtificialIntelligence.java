package com.example.laudien.tictactoe.Objects;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.util.Random;

import static com.example.laudien.tictactoe.Contract.DURATION_AI_WAIT;
import static com.example.laudien.tictactoe.Contract.EASY;
import static com.example.laudien.tictactoe.Contract.EDGES;
import static com.example.laudien.tictactoe.Contract.EMPTY_FIELD;
import static com.example.laudien.tictactoe.Contract.HARD;
import static com.example.laudien.tictactoe.Contract.MEDIUM;
import static com.example.laudien.tictactoe.Contract.NUMBER_OF_TACTICS;
import static com.example.laudien.tictactoe.Contract.OPPOSITE_EDGES;
import static com.example.laudien.tictactoe.Contract.RED_PLAYER;
import static com.example.laudien.tictactoe.Contract.ROWS;
import static com.example.laudien.tictactoe.Contract.TACTIC_EDGE;
import static com.example.laudien.tictactoe.Contract.TACTIC_MIDDLE;
import static com.example.laudien.tictactoe.Contract.WINNING_POSITIONS;
import static com.example.laudien.tictactoe.Contract.YELLOW_PLAYER;
import static com.example.laudien.tictactoe.MainActivity.animationDuration;

public class ArtificialIntelligence implements Board.OnGameOverListener, Board.OnNextPlayerListener {
    private static final int NO_POSITION_FOUND = -1;
    private int difficulty, counter, chipColor, tactic, placedEdge;
    private int[] positionState;
    private Board board;

    public ArtificialIntelligence(Board board, int difficulty, int chipColor){
        this.board = board;
        this.chipColor = chipColor;
        setRandomTactic();
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
        }, animationDuration * DURATION_AI_WAIT + 100);
    }

    private void medium(){
        if(attackAndDefend()) return; // attack and defend
        easy();// if not possible or necessary, place random (like on easy mode):
    }

    private void hard(){
        // standard stuff first:
        if(attackAndDefend()) return;

        switch (tactic){
            case TACTIC_EDGE:
                Log.i("ArtificialIntelligence", "Tactic = Edge");
                tacticEdge();
                break;
            case TACTIC_MIDDLE:
                Log.i("ArtificialIntelligence", "Tactic = Middle");
                tacticMiddle();
                break;
        }
    }

    private void tacticEdge(){
        int edge;
        switch (counter){
            case 1:
            case 2:
                placeChip(getFreeEdge());
                break;
            case 3:
                // if the player thought he can beat the hard bot, defend against it
                if(positionState[1] == getEnemyColor() && positionState[3] == getEnemyColor()) {
                    placeChip(4);
                    break;
                }
                // if no defense is necessary and no immediate win is possible, place on free edge
                edge = getFreeEdge();
                if (edge != NO_POSITION_FOUND){
                    placeChip(edge);
                    break;
                }
                break;
            case 4:
                // if middle field is empty, place there and win
                if (positionState[4] == EMPTY_FIELD) {
                    placeChip(4);
                    break;
                }
                // if no defense is necessary and no immediate win is possible, place on free edge
                edge = getFreeEdge();
                if (edge != NO_POSITION_FOUND) {
                    placeChip(getFreeEdge());
                    break;
                }
                easy(); // 5. if no edge was found, place random (like on easy mode)
                break;
            default:
                // if no defense is necessary and no immediate win is possible, place on free edge
                edge = getFreeEdge();
                if(edge != NO_POSITION_FOUND) {
                    placeChip(getFreeEdge());
                    break;
                }
                easy(); // 4. if no edge was found, place random (like on easy mode)
        }
    }

    private void tacticMiddle(){
        switch (counter){
            case 1: // first place the chip in the middle
                placeChip(4);
                break;
            case 2: // place on the next free edge where on opposite edge is not the player
                for(int edge : EDGES){
                    // if that edge is not free - go to next edge
                    if(positionState[edge] != EMPTY_FIELD)
                        continue;
                    // place on that edge where the enemy is not on opposite edge
                    if(getOppositeEdge(edge) != getEnemyColor()) {
                        placedEdge = edge;
                        placeChip(edge);
                        break;
                    }
                }
                break;
            case 3:
                // if the position between is empty, place on free edge
                int edge = getFreeEdge();
                if(positionState[getPositionBetween(placedEdge, edge)] == EMPTY_FIELD){
                    placeChip(edge);
                    break;
                }
                // if it is not free, place on another edge
                int nextEdge = NO_POSITION_FOUND;
                for(int i = 0; i < EDGES.length - 1; i++){
                    if(EDGES[i] == edge){
                        nextEdge = EDGES[i+1];
                        break;
                    }
                }
                if(nextEdge != NO_POSITION_FOUND) {
                    placeChip(nextEdge);
                }
                break;
            default: // after the standard stuff, it is the same as on easy ;)
                easy();
                break;
        }
    }

    private void setRandomTactic() {
        tactic = new Random().nextInt(NUMBER_OF_TACTICS);
    }

    private boolean attackAndDefend(){
        if(counter > 2){
            int position = searchPositions(getChipColor());
            // 1. Attack (= win the game if possible):
            if (position != NO_POSITION_FOUND) {
                placeChip(position);
                return true;
            }
            // 2. if no immediate win is possible, defend a possible win of the player:
            position = searchPositions(getEnemyColor());
            if (position != NO_POSITION_FOUND) {
                placeChip(position);
                return true;
            }
        }
        return false;
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
        for(int edge:EDGES){
            if(positionState[edge] == EMPTY_FIELD)
                return edge;
        }
        return NO_POSITION_FOUND; // nothing was found
    }

    private int getOppositeEdge(int edge){
        for (int pos[] : OPPOSITE_EDGES){
            if(pos[0] == edge)
                return pos[1];
            if(pos[1] == edge)
                return pos[0];
        }
        return NO_POSITION_FOUND;
    }

    private int getPositionBetween(int pos1, int pos2){
        // takes 2 edges and returns the position between them
        // the two positions must be different:
        if(pos1 == pos2) return NO_POSITION_FOUND;

        // make sure, that pos1 < pos2:
        if(pos1 > pos2){
            int i = pos1;
            pos1 = pos2;
            pos2 = i;
        }

        // make sure that the positions really are edges:
        boolean pos1_isEdge = false;
        boolean pos2_isEdge = false;
        for(int edge : EDGES){
            if(pos1 == edge) pos1_isEdge = true;
            if(pos2 == edge) pos2_isEdge = true;
        }
        if(!pos1_isEdge || !pos2_isEdge)
            return NO_POSITION_FOUND;

        // if the edges are opposite of each other, result = middle position
        if(getOppositeEdge(pos1) != pos2) return 4;

        // check, if the edges are on the same row
        boolean sameRow = false;
        for(int[] row : ROWS){ // every row
            boolean pos1_in_row = false;
            boolean pos2_in_row = false;
            for(int position : row){ // every position on that row
                if(position == pos1) pos1_in_row = true;
                if(position == pos2) pos2_in_row = true;
                if(pos1_in_row && pos2_in_row){
                    sameRow = true;
                    break;
                }
            }
        }

        // get position between if positions are in the same row
        if(sameRow) return pos2 - 1;

        // get position between if not in same row
        return pos1 + 3;
    }

    private void placeChip(final int position){
        // place the chip after all the animations are definitely finished
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                board.placeChip((ImageView) board.getBoardLayout().getChildAt(position), false);
                board.enableUserInput();
            }
        }, animationDuration * DURATION_AI_WAIT + 100);
    }

    @Override
    public void onGameOver(int winner) {
        resetCounter();
        setRandomTactic();
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
