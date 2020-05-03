package com.vincentz1911.drawandguess.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vincentz1911.drawandguess.R;
import com.vincentz1911.drawandguess.models.PlayerModel;

import java.util.List;

public class ScoreboardAdapter extends ArrayAdapter<PlayerModel> {

    public ScoreboardAdapter(Context c, List<PlayerModel> list) { super(c, 0, list); }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(getContext()).inflate(
                R.layout.listitem_scoreboard, parent, false);
        view.setMinimumHeight(parent.getHeight() / getCount() - 1);

        PlayerModel player = getItem(position);
        if (player == null) return view;
        view.setBackgroundResource(player.Color.Color);
        int textColor = player.Color.Inverse ? Color.WHITE : Color.BLACK;

        TextView playerName = view.findViewById(R.id.playerName);
        playerName.setText(player.Name);
        playerName.setTextColor(textColor);
        TextView playerScore = view.findViewById(R.id.playerScore);
        playerScore.setTextColor(textColor);
        playerScore.setText(String.valueOf(player.Score));

        return view;
    }
}