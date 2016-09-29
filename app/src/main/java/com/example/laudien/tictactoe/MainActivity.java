package com.example.laudien.tictactoe;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements StartFragment.OnStartGameListener{

    GameFragment gameFragment;
    StartFragment startFragment;
    FragmentManager fragmentManager;
    Bundle gameFragmentBundle; // is sent to gameFragment
    public static final String NAME_AI_IS_USED = "aiIsUsed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startFragment = (StartFragment)Fragment.instantiate(this, StartFragment.class.getName(), null);
        gameFragmentBundle = new Bundle();
        fragmentManager = getSupportFragmentManager();

        // load StartFragment
        fragmentManager.beginTransaction().replace(R.id.frameLayout, startFragment).commit();
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
        fragmentManager.beginTransaction().replace(R.id.frameLayout, startFragment).commit();
    }
    @Override
    public void onStartGame(boolean aiIsUsed) {
        gameFragmentBundle.putBoolean(NAME_AI_IS_USED, aiIsUsed);
        gameFragment = (GameFragment)Fragment.instantiate(this,GameFragment.class.getName(), gameFragmentBundle);
        fragmentManager.beginTransaction().replace(R.id.frameLayout, gameFragment).commit();
    }
}
