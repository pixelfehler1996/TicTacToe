package com.example.laudien.tictactoe;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.laudien.tictactoe.Fragments.GameFragment;
import com.example.laudien.tictactoe.Fragments.SettingsFragment;
import com.example.laudien.tictactoe.Fragments.StartFragment;

import java.util.ArrayList;

import static com.example.laudien.tictactoe.Fragments.SettingsFragment.ANIMATION_DURATION_DEF;
import static com.example.laudien.tictactoe.Fragments.SettingsFragment.PREFERENCE_ANIMATION_DURATION;

public class MainActivity extends AppCompatActivity implements StartFragment.OnStartGameListener {
    public static long animationDuration;
    public static int displayWidth;
    GameFragment gameFragment;
    StartFragment startFragment;
    SettingsFragment settingsFragment;
    FragmentManager fragmentManager;
    FrameLayout mainMenu, gameLayout, settings, lastLayout;
    private SharedPreferences sharedPreferences;
    boolean backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startFragment = (StartFragment)Fragment.instantiate(this, StartFragment.class.getName(), null);
        gameFragment = new GameFragment();
        settingsFragment = new SettingsFragment();
        fragmentManager = getSupportFragmentManager();
        mainMenu = (FrameLayout)findViewById(R.id.foreground_layout);
        gameLayout = (FrameLayout)findViewById(R.id.background_layout);
        settings = (FrameLayout)findViewById(R.id.settings_layout);
        sharedPreferences = getSharedPreferences("com.example.laudien.tictactoe", 0);
        animationDuration = ANIMATION_DURATION_DEF * 2;

        // get display width
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        displayWidth = metrics.widthPixels;

        // load Fragments
        fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit(); // start fragment with main manu
        fragmentManager.beginTransaction().replace(R.id.settings_layout, settingsFragment).commit(); // settings fragment
        fragmentManager.beginTransaction().replace(R.id.background_layout, gameFragment).commit(); // game fragment
        fragmentManager.executePendingTransactions();
        settingsFragment.addOnSettingsChangedListener(new SettingsFragment.OnSettingsChangedListener() {
            @Override
            public void onSettingsChanged(String preference) {
                if(gameFragment != null)
                    gameFragment.onSettingsChanged(preference);
            }
        });
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
                settings.setTranslationX(displayWidth);
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
            settings.animate().translationX(displayWidth).setDuration(animationDuration);
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
            if(!backPressed) {
                backPressed = true;
                Toast.makeText(getApplicationContext(), getString(R.string.press_again_to_exit),
                        Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressed = false;
                    }
                }, 3000);
            }else {
                finish();
                System.exit(0);
            }
        }
    }
    @Override
    public void onStartGame(final boolean aiIsUsed) {
        if(gameFragment == null)
            gameFragment = new GameFragment();
        mainMenu.animate().translationX(displayWidth).setDuration(animationDuration);
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
