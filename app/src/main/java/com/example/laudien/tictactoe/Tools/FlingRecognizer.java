package com.example.laudien.tictactoe.Tools;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class FlingRecognizer extends GestureDetector.SimpleOnGestureListener {
    public static final int FLING_LEFT = 1;
    public static final int FLING_RIGHT = 2;
    private ArrayList<OnFlingListener> onFlingListeners;
    private GestureDetector gestureDetector;
    private View view;

    public FlingRecognizer(Context context, View view){
        onFlingListeners = new ArrayList<>();
        gestureDetector = new GestureDetector(context, this);
        this.view = view;
    }

    public void addOnFlingListener(OnFlingListener listener){
        onFlingListeners.add(listener);
    }

    public interface OnFlingListener{
        void onFling(View view, int type);
    }

    public void onTouchEvent(MotionEvent motionEvent){
        gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        int type;
        if(motionEvent.getX() > motionEvent1.getX()) // swipe from right to left
            type = FLING_LEFT;
        else // swipe from left to right
            type = FLING_RIGHT;

        for (OnFlingListener listener : onFlingListeners)
            listener.onFling(view, type);
        return false;
    }
}
