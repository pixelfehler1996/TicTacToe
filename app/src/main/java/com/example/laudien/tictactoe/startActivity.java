package com.example.laudien.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    TextView timeTextView;
    SeekBar seekBar;

    public void startGame(View view){
        SharedPreferences kiIsUsed = getSharedPreferences("kiIsUsed",0);
        SharedPreferences.Editor editor = kiIsUsed.edit();
        editor.putBoolean("kiIsUsed",false);
        editor.commit();
        this.onStop();
        startActivity(new Intent(StartActivity.this,GameActivity.class));
    }
    public void playerVsKI(View view){
        SharedPreferences kiIsUsed = getSharedPreferences("kiIsUsed",0);
        SharedPreferences.Editor editor = kiIsUsed.edit();
        editor.putBoolean("kiIsUsed",true);
        editor.commit();
        this.onStop();
        startActivity(new Intent(StartActivity.this,GameActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // reset kiIsUsed to false ==>
        SharedPreferences kiIsUsed = getSharedPreferences("kiIsUsed",0);
        SharedPreferences.Editor editor = kiIsUsed.edit();
        editor.putBoolean("kiIsUsed",false);
        editor.commit(); // <==
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

}
