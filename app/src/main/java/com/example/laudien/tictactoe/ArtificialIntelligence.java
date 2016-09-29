package com.example.laudien.tictactoe;

import java.util.Random;

class ArtificialIntelligence {
    private int difficulty;
    private int counter; // counts the moves
    private GameFragment gameFragment;

    ArtificialIntelligence(GameFragment gameFragment){
        resetCounter();
        this.gameFragment = gameFragment;
    }
    void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }
    void resetCounter(){
        counter = 1;
    }
    void attack() {
        int position;
        int rand;
        int[] positionState = gameFragment.getPositionState();

        // enable the playground
        gameFragment.setGameIsRunning(true);

        if(difficulty == 0) { //easy
            // just place random
            rand = new Random().nextInt(9);
            while (positionState[rand] != 2)
                rand = new Random().nextInt(9);
            gameFragment.placeChip(gameFragment.getBoard().getChildAt(rand));
        } else if(difficulty == 1) { // medium
            position = searchPositions(1, positionState); // every time its the yellow player!

            if (position != -1) // 1. Attack (= win the game if possible):
                gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
            else { // 2. if no immediate win is possible, defense a possible win of the player:
                position = searchPositions(0, positionState);
                if (position != -1)
                    gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
                else { // 3. if no possible win was found, place random:
                    rand = new Random().nextInt(9);
                    while (positionState[rand] != 2)
                        rand = new Random().nextInt(9);
                    gameFragment.placeChip(gameFragment.getBoard().getChildAt(rand));
                }
            }
        } else if(difficulty == 2) { // hard - every time its the red player!
            if(counter == 1 || counter == 2)
                gameFragment.placeChip(gameFragment.getBoard().getChildAt(getFreeEdge(positionState)));
            else if(counter == 3){
                position = searchPositions(0, positionState);
                if(position != -1) // 1. Attack (= win the game if possible):
                    gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
                else { // 2. if not immediate win is possible, defense a possible win of the player:
                    position = searchPositions(1, positionState);
                    if(position != -1)
                        gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
                    else // if no defense is necessary, place on free edge
                        gameFragment.placeChip(gameFragment.getBoard().getChildAt(getFreeEdge(positionState)));
                }
            }else if(counter == 4){
                if(positionState[4] == 2) // if middle field is empty, place there and win
                    gameFragment.placeChip(gameFragment.getBoard().getChildAt(4));
                else{
                    position = searchPositions(0, positionState);
                    if(position != -1) // 1. Attack (= win the game if possible):
                        gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
                    else { // 2. if not immediate win is possible, defense a possible win of the player:
                        position = searchPositions(1, positionState);
                        if(position != -1)
                            gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
                        else // if no defense is necessary, place on free edge
                            gameFragment.placeChip(gameFragment.getBoard().getChildAt(getFreeEdge(positionState)));
                    }
                }
            }
            else if(counter > 4){
                position = searchPositions(0, positionState);
                if(position != -1) // 1. Attack (= win the game if possible):
                    gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
                else { // 2. if not immediate win is possible, defense a possible win of the player:
                    position = searchPositions(1, positionState);
                    if(position != -1)
                        gameFragment.placeChip(gameFragment.getBoard().getChildAt(position));
                    else // if no defense is necessary, place on free edge
                        gameFragment.placeChip(gameFragment.getBoard().getChildAt(getFreeEdge(positionState)));
                }
            }
        }
        counter++;
    }
    private int searchPositions(int playerID, int[] positionState){
        // searches for a position where are 2/3 used from the player with playerID
        int counter;
        for(int[] possiblePosition : GameFragment.winningPositions){
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
    private int getFreeEdge(int[] positionState){
        // searches for the next free edge
        int[] edges = {0,2,6,8};
        for(int edge:edges){
            if(positionState[edge] == 2)
                return edge;
        }
        return -1; // if nothing was found, return -1
    }
}
