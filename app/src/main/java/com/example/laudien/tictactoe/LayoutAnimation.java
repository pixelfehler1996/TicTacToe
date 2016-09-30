package com.example.laudien.tictactoe;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class LayoutAnimation {
    private ArrayList<FrameLayout> layoutList;
    private long animationDuration;
    private int currentPage;

    public LayoutAnimation(ArrayList<FrameLayout> layoutList, long animationDuration, int currentPage){
        this.layoutList = layoutList;
        this.animationDuration = animationDuration;
        this.currentPage = currentPage;
    }
    public void nextPage(){
        // slides in the next page from the right
        if(currentPage >= layoutList.size()) {
            Log.e("LayoutAnimation", "There is no next page!");
            return;
        }
        FrameLayout nextPage = layoutList.get(currentPage + 1);
        nextPage.setVisibility(View.VISIBLE);
        nextPage.setTranslationX(+1100f);
        nextPage.animate().translationX(0f).setDuration(animationDuration);
        currentPage++;
    }
    public void lastPage(){
        // slides out the top most page to the right
        if(currentPage < 1) {
            Log.e("LayoutAnimation", "There is no last page!");
            return;
        }
        FrameLayout topMostPage = layoutList.get(currentPage);
        FrameLayout lastPage = layoutList.get(currentPage - 1);
        lastPage.setVisibility(View.VISIBLE);
        topMostPage.animate().translationX(+1100f).setDuration(animationDuration);
        currentPage--;
    }
}
