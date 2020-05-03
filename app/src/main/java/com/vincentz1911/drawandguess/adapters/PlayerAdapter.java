package com.vincentz1911.drawandguess.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.vincentz1911.drawandguess.R;
import com.vincentz1911.drawandguess.models.PlayerModel;

import java.util.List;

import static com.vincentz1911.drawandguess.Statics.COLORS;
import static com.vincentz1911.drawandguess.Statics.fullscreen;
import static com.vincentz1911.drawandguess.Statics.msg;

public class PlayerAdapter extends ArrayAdapter<PlayerModel> {

    public PlayerAdapter(Context c, List<PlayerModel> list) {
        super(c, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        final Context c = getContext();
        if (view == null) view = LayoutInflater.from(c).inflate(
                R.layout.listitem_player, parent, false);
        view.setMinimumHeight(parent.getHeight() / getCount() - 1);

        final PlayerModel player = getItem(position);
        if (player == null) return view;
        int textColor = player.Color.Inverse ? R.color.White : R.color.Black;

        TextView playerName = view.findViewById(R.id.playerName);
        playerName.setText(player.Name);
        playerName.setTextColor(ContextCompat.getColor(c, textColor));
        playerName.setBackgroundResource(player.Color.Color);

        playerName.setOnClickListener(view1 -> {
            nameDialog(c, player);
            PlayerAdapter.this.notifyDataSetChanged();
        });

        ImageView selectColor = view.findViewById(R.id.playerColor);
        selectColor.setOnClickListener(view12 -> colorDialog(player, c));

        ImageView playerEnabled = view.findViewById(R.id.playerEnabled);
        if (player.Enabled) playerEnabled.setImageResource(R.drawable.ic_player_enabled);
        else playerEnabled.setImageResource(R.drawable.ic_player_disabled);

        playerEnabled.setOnClickListener(view13 -> {
            player.Enabled = !player.Enabled;
            PlayerAdapter.this.notifyDataSetChanged();
        });

        return view;
    }

    private void nameDialog(final Context c, final PlayerModel player) {
        final EditText input = new EditText(c);
        input.setText(player.Name);
        input.setTextSize(48);
        input.setMaxLines(1);
        input.setGravity(Gravity.CENTER);
        input.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.selectAll();

        new AlertDialog.Builder(c)
                .setTitle("Input Name")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String name = input.getText().toString();
                    if (name.length() == 0) dialog.cancel();
                    if (name.length() > 12) msg("Max 12 Characters!", c);

                    name = name.substring(0, Math.min(name.length(), 15));
                    player.Name = name;
                    PlayerAdapter.this.notifyDataSetChanged();
                    dialog.dismiss();
                    fullscreen((Activity) c);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    fullscreen((Activity) c);
                }).show();
    }

    private void colorDialog(PlayerModel player, Context c) {
        GridView gridView = new GridView(c);
        gridView.setAdapter(new ColorAdapter(c, COLORS));
        gridView.setNumColumns(5);
        gridView.setGravity(Gravity.CENTER);

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setView(gridView).setTitle("Colors").create();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            player.Color = COLORS.get(position);
            notifyDataSetChanged();
            dialog.dismiss();
            fullscreen((Activity) c);
        });
        dialog.show();
        fullscreen((Activity) c);
    }
}