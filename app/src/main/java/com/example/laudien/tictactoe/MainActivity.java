package com.example.laudien.tictactoe;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements StartFragment.OnStartGameListener{

    public static final String NAME_AI_IS_USED = "aiIsUsed";
    GameFragment gameFragment;
    StartFragment startFragment;
    FragmentManager fragmentManager;
    Bundle gameFragmentBundle; // is sent to gameFragment
    FrameLayout mainMenu, gameLayout, settings, lastLayout;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startFragment = (StartFragment)Fragment.instantiate(this, StartFragment.class.getName(), null);
        gameFragmentBundle = new Bundle();
        fragmentManager = getSupportFragmentManager();
        mainMenu = (FrameLayout)findViewById(R.id.foreground_layout);
        gameLayout = (FrameLayout)findViewById(R.id.background_layout);
        settings = (FrameLayout)findViewById(R.id.settings_layout);
        sharedPreferences = getSharedPreferences("com.example.laudien.tictactoe", 0);

        // load Fragments
        fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit(); // start fragment with main manu
        fragmentManager.beginTransaction().replace(R.id.settings_layout, new SettingsFragment()).commit(); // settings fragment
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
            lastLayout = getTopMostLayout();
            if(lastLayout != settings) {
                if (lastLayout == gameLayout)
                    gameFragment.onPause();
                settings.setVisibility(View.VISIBLE);
                settings.setTranslationX(+1100f);
                settings.animate().translationX(0f).setDuration(500);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(getTopMostLayout() == settings){
            gameLayout.setAlpha(0f);
            settings.animate().translationX(+1100f).setDuration(500);
            gameLayout.animate().alpha(1f).setDuration(500);
            if(gameFragment != null)
                gameFragment.onResume();
        }else {
            if (gameFragment != null) {
                mainMenu.setVisibility(View.VISIBLE);
                mainMenu.animate().translationX(0f).setDuration(500);
                gameLayout.animate().alpha(0f).setDuration(500);
                fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().remove(gameFragment).commit();
                        gameFragment = null;
                    }
                }, 500);
            } else
                finish();
        }
    }
    @Override
    public void onStartGame(boolean aiIsUsed) {
        gameFragmentBundle.putBoolean(NAME_AI_IS_USED, aiIsUsed);
        gameFragment = (GameFragment)Fragment.instantiate(this,GameFragment.class.getName(), gameFragmentBundle);
        mainMenu.animate().translationX(-1100f).setDuration(500);
        gameLayout.animate().alpha(1f).setDuration(500);
        fragmentManager.beginTransaction().replace(R.id.background_layout, gameFragment).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainMenu.setVisibility(View.INVISIBLE);
            }
        }, 500);
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
