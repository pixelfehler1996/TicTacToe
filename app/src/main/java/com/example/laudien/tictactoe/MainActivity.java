package com.example.laudien.tictactoe;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements StartFragment.OnStartGameListener{

    public static final String NAME_AI_IS_USED = "aiIsUsed";
    GameFragment gameFragment;
    StartFragment startFragment;
    FragmentManager fragmentManager;
    Bundle gameFragmentBundle; // is sent to gameFragment
    FrameLayout foreground, background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startFragment = (StartFragment)Fragment.instantiate(this, StartFragment.class.getName(), null);
        gameFragmentBundle = new Bundle();
        fragmentManager = getSupportFragmentManager();
        foreground = (FrameLayout)findViewById(R.id.foreground_layout);
        background = (FrameLayout)findViewById(R.id.background_layout);

        // load StartFragment
        fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit();
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(gameFragment != null) {
            foreground.setVisibility(View.VISIBLE);
            foreground.animate().translationX(0f).setDuration(500);
            background.animate().alpha(0f).setDuration(500);
            fragmentManager.beginTransaction().replace(R.id.foreground_layout, startFragment).commit();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentManager.beginTransaction().remove(gameFragment).commit();
                    gameFragment = null;
                }
            }, 500);
        }else
            finish();
    }
    @Override
    public void onStartGame(boolean aiIsUsed) {
        gameFragmentBundle.putBoolean(NAME_AI_IS_USED, aiIsUsed);
        gameFragment = (GameFragment)Fragment.instantiate(this,GameFragment.class.getName(), gameFragmentBundle);
        foreground.animate().translationX(-1100f).setDuration(500);
        background.animate().alpha(1f).setDuration(500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                foreground.setVisibility(View.INVISIBLE);
            }
        }, 500);
        fragmentManager.beginTransaction().replace(R.id.background_layout, gameFragment).commit();
    }
}
