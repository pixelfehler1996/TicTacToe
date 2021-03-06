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
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.laudien.tictactoe.Fragments.GameFragment;
import com.example.laudien.tictactoe.Fragments.SettingsFragment;
import com.example.laudien.tictactoe.Fragments.StartFragment;
import com.example.laudien.tictactoe.Tools.FlingRecognizer;

import static com.example.laudien.tictactoe.Contract.ANIMATION_DURATION_DEF;
import static com.example.laudien.tictactoe.Contract.DURATION_BACK_TO_EXIT;
import static com.example.laudien.tictactoe.Contract.DURATION_LAYOUT_TRANSLATION;
import static com.example.laudien.tictactoe.Contract.PREFERENCES;
import static com.example.laudien.tictactoe.Tools.FlingRecognizer.FLING_LEFT;
import static com.example.laudien.tictactoe.Tools.FlingRecognizer.FLING_RIGHT;

public class MainActivity extends AppCompatActivity implements StartFragment.OnStartGameListener,
        FlingRecognizer.OnFlingListener {
    public static long animationDuration;
    public static int displayWidth;
    GameFragment gameFragment;
    StartFragment startFragment;
    SettingsFragment settingsFragment;
    FragmentManager fragmentManager;
    FrameLayout mainMenu, gameLayout, settings, lastLayout;
    private SharedPreferences sharedPreferences;
    boolean backPressed;
    FlingRecognizer flingRecognizer;

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
        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        animationDuration = ANIMATION_DURATION_DEF;

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

        flingRecognizer = new FlingRecognizer(this, settings);
        flingRecognizer.addOnFlingListener(this);
        mainMenu.setOnTouchListener(flingRecognizer);
        settings.setOnTouchListener(flingRecognizer);
        gameLayout.setOnTouchListener(flingRecognizer);
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
            openSettings();
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
            settings.animate().translationX(displayWidth).setDuration(animationDuration * DURATION_LAYOUT_TRANSLATION);
            gameLayout.animate().alpha(1f).setDuration(animationDuration * DURATION_LAYOUT_TRANSLATION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(gameFragment != null)
                        gameFragment.onResume();
                }
            }, animationDuration * DURATION_LAYOUT_TRANSLATION);

        }else if(getTopMostLayout() == gameLayout){
                mainMenu.setVisibility(View.VISIBLE);
                mainMenu.animate().translationX(0f).setDuration(animationDuration * DURATION_LAYOUT_TRANSLATION);
                gameLayout.animate().alpha(0f).setDuration(animationDuration * DURATION_LAYOUT_TRANSLATION);
                fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().remove(gameFragment).commit();
                        gameFragment = null;
                    }
                }, animationDuration * DURATION_LAYOUT_TRANSLATION);
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
                }, 1000 * DURATION_BACK_TO_EXIT);
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
        mainMenu.animate().translationX(-displayWidth).setDuration(animationDuration * DURATION_LAYOUT_TRANSLATION);
        gameLayout.animate().alpha(1f).setDuration(animationDuration * DURATION_LAYOUT_TRANSLATION);
        fragmentManager.beginTransaction().replace(R.id.background_layout, gameFragment).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainMenu.setVisibility(View.INVISIBLE);
                gameFragment.startGame(aiIsUsed);
            }
        }, animationDuration * DURATION_LAYOUT_TRANSLATION);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        flingRecognizer.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onFling(View view, int type) {
        if(view == settings){
            switch (type){
                case FLING_RIGHT:
                    if(getTopMostLayout() == settings)
                        onBackPressed();
                    break;
                case FLING_LEFT:
                    openSettings();
                    break;
            }
        }
    }

    private FrameLayout getTopMostLayout(){
        if(settings.getVisibility() == View.VISIBLE && settings.getTranslationX() == 0f)
            return settings;
        else if(mainMenu.getVisibility() == View.VISIBLE
                && mainMenu.getAlpha() == 1f && mainMenu.getTranslationX() == 0f)
            return mainMenu;
        else return gameLayout;
    }

    private void openSettings(){
        lastLayout = getTopMostLayout();
        if(lastLayout != settings) {
            if (lastLayout == gameLayout)
                gameFragment.onPause();
            settings.setVisibility(View.VISIBLE);
            settings.setTranslationX(displayWidth);
            settings.animate().translationX(0f).setDuration(animationDuration * DURATION_LAYOUT_TRANSLATION);
        }
    }
}
