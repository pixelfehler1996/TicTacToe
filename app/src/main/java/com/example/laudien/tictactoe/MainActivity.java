package com.example.laudien.tictactoe;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StartFragment.OnStartGameListener{

    public static final String NAME_AI_IS_USED = "aiIsUsed";
    public static final String ANIMATION_DURATION = "animationDuration";
    public static long animationDuration;
    GameFragment gameFragment;
    StartFragment startFragment;
    SettingsFragment settingsFragment;
    FragmentManager fragmentManager;
    FrameLayout mainMenu, gameLayout, settings, lastLayout;
    LayoutAnimation animation;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startFragment = (StartFragment)Fragment.instantiate(this, StartFragment.class.getName(), null);
        settingsFragment = new SettingsFragment();
        gameFragment = new GameFragment();
        fragmentManager = getSupportFragmentManager();
        mainMenu = (FrameLayout)findViewById(R.id.foreground_layout);
        gameLayout = (FrameLayout)findViewById(R.id.background_layout);
        settings = (FrameLayout)findViewById(R.id.settings_layout);
        ArrayList<FrameLayout> layoutList = new ArrayList<>();
        layoutList.add(mainMenu);
        layoutList.add(gameLayout);
        layoutList.add(settings);
        animation = new LayoutAnimation(layoutList, animationDuration, 0);
        sharedPreferences = getSharedPreferences("com.example.laudien.tictactoe", 0);
        animationDuration = sharedPreferences.getLong(ANIMATION_DURATION, 200);

        // load Fragments
        fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit(); // start fragment with main manu
        fragmentManager.beginTransaction().replace(R.id.settings_layout, settingsFragment).commit(); // settings fragment
        fragmentManager.beginTransaction().replace(R.id.background_layout, gameFragment).commit(); // game fragment
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if(getTopMostLayout() == settings) {
                onBackPressed();
                return true;
            }
            lastLayout = getTopMostLayout();
            if(lastLayout != settings) {
                if (lastLayout == gameLayout)
                    gameFragment.onPause();
                settings.setVisibility(View.VISIBLE);
                settings.setTranslationX(+1100f);
                settings.animate().translationX(0f).setDuration(animationDuration);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(getTopMostLayout() == settings){
            if(settingsFragment != null)
                settingsFragment.onPause();
            gameLayout.setAlpha(0f);
            settings.animate().translationX(+1100f).setDuration(animationDuration);
            gameLayout.animate().alpha(1f).setDuration(animationDuration);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(gameFragment != null)
                        gameFragment.onResume();
                }
            }, animationDuration);

        }else if(getTopMostLayout() == gameLayout){
                mainMenu.setVisibility(View.VISIBLE);
                mainMenu.animate().translationX(0f).setDuration(animationDuration);
                gameLayout.animate().alpha(0f).setDuration(animationDuration);
                fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().remove(gameFragment).commit();
                        gameFragment = null;
                    }
                }, animationDuration);
        } else { // exit app
            finish();
            System.exit(0);
        }
    }
    @Override
    public void onStartGame(final boolean aiIsUsed) {
        if(gameFragment == null)
            gameFragment = new GameFragment();
        mainMenu.animate().translationX(-1100f).setDuration(animationDuration);
        gameLayout.animate().alpha(1f).setDuration(animationDuration);
        fragmentManager.beginTransaction().replace(R.id.background_layout, gameFragment).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainMenu.setVisibility(View.INVISIBLE);
                gameFragment.startGame(aiIsUsed);
            }
        }, animationDuration);
    }
    private FrameLayout getTopMostLayout(){
        if(settings.getVisibility() == View.VISIBLE && settings.getTranslationX() == 0f)
            return settings;
        else if(mainMenu.getVisibility() == View.VISIBLE
                && mainMenu.getAlpha() == 1f && mainMenu.getTranslationX() == 0f)
            return mainMenu;
        else return gameLayout;
    }
}
