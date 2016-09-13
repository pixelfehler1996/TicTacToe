package com.example.laudien.tictactoe;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class StartActivity extends AppCompatActivity
        implements GameFragment.OnFragmentInteractionListener,
        StartFragment.OnFragmentInteractionListener
{

    GameFragment gameFragment;
    StartFragment startFragment;
    FragmentTransaction transaction;
    Bundle gameFragmentBundle; // is sent to gameFragment
    public static final String NAME_AI_IS_USED = "aiIsUsed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startFragment = (StartFragment)Fragment.instantiate(this, StartFragment.class.getName(), null);
        gameFragmentBundle = new Bundle();

        // load StartActivity Fragment
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, startFragment).commit();
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, startFragment).commit();
    }

    public void playerVsPlayer(View view){
        gameFragmentBundle.putBoolean(NAME_AI_IS_USED, false);
        gameFragment = (GameFragment)Fragment.instantiate(this,GameFragment.class.getName(), gameFragmentBundle);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, gameFragment).commit();
        getSupportFragmentManager().executePendingTransactions(); //ensure that transaction is completed
        newGame(view);
    }
    public void playerVsKI(View view){
        gameFragmentBundle.putBoolean(NAME_AI_IS_USED, true);
        gameFragment = (GameFragment)Fragment.instantiate(this,GameFragment.class.getName(), gameFragmentBundle);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, gameFragment).commit();
        getSupportFragmentManager().executePendingTransactions(); //ensure that transaction is completed
        newGame(view);
    }
    public void newGame(View view){
        gameFragment.startGame(this);
    }
    public void placeChip(View view){
        gameFragment.placeChip(view, this);
    }
}
