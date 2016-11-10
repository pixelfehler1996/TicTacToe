package com.example.laudien.tictactoe;

public class Contract {
    // Preference names in SharedPreferences
    public static final String PREFERENCES = "Settings";
    public static final String PREFERENCE_TIME = "savedTime";
    public static final String PREFERENCE_DIFFICULTY = "difficulty";
    public static final String PREFERENCE_ANIMATION_DURATION = "animationDuration";

    // Difficulties
    public static final int EASY = 0;
    public static final int MEDIUM = 1;
    public static final int HARD = 2;

    // Slider Values
    public static final int DIFFICULTY_DEF = MEDIUM;
    public static final int TIME_DEF = 10;
    public static final int TIME_MIN = 2;
    public static final int ANIMATION_DURATION_DEF = 100;
    public static final int ANIMATION_DURATION_MIN = 50;

    // Player/Chip Colors
    public static final int RED_PLAYER = 0;
    public static final int YELLOW_PLAYER = 1;

    // Board specific values
    public static final int [][] WINNING_POSITIONS = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    public static final int EMPTY_FIELD = 2;
    public static final int RESULT_DRAW = 2;
    public static final int NO_RESULT_YET = 3;

    // Animation duration factors
    // Board
    public static final int DURATION_PLACE_CHIP = 5;
    public static final int DURATION_CHIP_TAKEOFF = 10;
    public static final int DURATION_BOARD_SHAKE = 2;
    // Ship
    public static final int DURATION_SHIP = 32;
    // Countdown
    public static final int DURATION_COUNTDOWN_FLASH = 5;
    public static final int DURATION_COUNTDOWN_SHAKE = 7;
    public static final int DURATION_COUNTDOWN_FLIP_IN = 10;
    public static final int DURATION_COUNTDOWN_FLIP_OUT = 10;
    // Artificial Intelligence
    public static final int DURATION_AI_WAIT = 10;
    // GameFragment
    public static final int DURATION_DIFFICULTY_STAND_UP = 8;
    public static final int DURATION_WINNER_LAYOUT_HINGE = 10;
    public static final int DURATION_WINNER_LAYOUT_BOUNCE_IN = 10;
    // Layout translation
    public static final int DURATION_LAYOUT_TRANSLATION = 2;
}
