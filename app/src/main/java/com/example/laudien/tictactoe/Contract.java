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
    public final static int [][] WINNING_POSITIONS = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    public final static int EMPTY_FIELD = 2;
    public final static int RESULT_DRAW = 2;
    public final static int NO_RESULT_YET = 3;
}
