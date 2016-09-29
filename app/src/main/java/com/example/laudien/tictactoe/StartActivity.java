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
        implements View.OnClickListener
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
    public void onBackPressed() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, startFragment).commit();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_player_vs_player:
                gameFragmentBundle.putBoolean(NAME_AI_IS_USED, false);
                gameFragment = (GameFragment)Fragment.instantiate(this,GameFragment.class.getName(), gameFragmentBundle);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, gameFragment).commit();
                getSupportFragmentManager().executePendingTransactions(); //ensure that transaction is completed
                gameFragment.startGame();
                break;
            case R.id.btn_player_vs_ki:
                gameFragmentBundle.putBoolean(NAME_AI_IS_USED, true);
                gameFragment = (GameFragment)Fragment.instantiate(this,GameFragment.class.getName(), gameFragmentBundle);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, gameFragment).commit();
                getSupportFragmentManager().executePendingTransactions(); //ensure that transaction is completed
                gameFragment.startGame();
                break;
        }
    }
}
