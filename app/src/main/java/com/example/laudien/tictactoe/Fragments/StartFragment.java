package com.example.laudien.tictactoe.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.laudien.tictactoe.R;

public class StartFragment extends Fragment implements View.OnClickListener {
    OnStartGameListener onStartGameListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        Button btn_playerVsPlayer = (Button) view.findViewById(R.id.btn_player_vs_player);
        Button btn_playerVsKi = (Button)view.findViewById(R.id.btn_player_vs_ki);
        btn_playerVsPlayer.setOnClickListener(this);
        btn_playerVsKi.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onStartGameListener = (OnStartGameListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_player_vs_player:
                onStartGameListener.onStartGame(false);
                break;
            case R.id.btn_player_vs_ki:
                onStartGameListener.onStartGame(true);
                break;
        }
    }

    public interface OnStartGameListener{
        void onStartGame(boolean aiIsUsed);
    }
}
