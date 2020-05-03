package com.vincentz1911.drawandguess.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.vincentz1911.drawandguess.R;
import com.vincentz1911.drawandguess.models.ColorModel;

import java.util.List;

public class ColorAdapter extends ArrayAdapter<ColorModel> {
    public ColorAdapter(Context c, List<ColorModel> color) { super(c, 0, color); }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(getContext()).inflate(
                R.layout.griditem_color, parent, false);

        //SETS BACKGROUND COLOR = COLOR AND BRUSH COLOR IF INVERSE
        ColorModel color = getItem(position);
        if (color == null) return view;
        ImageView imageView = view.findViewById(R.id.img_color);
        imageView.setBackgroundResource(color.Color);
        imageView.setImageResource(color.Inverse ? R.drawable.ic_brush_inv : R.drawable.ic_brush);
        return view;
    }
}