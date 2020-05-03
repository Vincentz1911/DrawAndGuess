package com.vincentz1911.drawandguess.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vincentz1911.drawandguess.R;
import com.vincentz1911.drawandguess.models.ImageModel;

import java.io.IOException;
import java.util.List;

public class ImageAdapter extends ArrayAdapter<ImageModel> {
    public ImageAdapter(Context c, List<ImageModel> images) {
        super(c, 0, images);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(getContext()).inflate(
                R.layout.griditem_image, parent, false);

        ImageModel img = getItem(position);
        if (img == null) return view;
        ImageView im = view.findViewById(R.id.img_image);
        TextView textView = view.findViewById(R.id.txt_image);
        textView.setText(img.Translation);
        try {
            im.setImageDrawable(Drawable.createFromStream(getContext().getAssets().open(
                    img.Category + "/" + img.Filename), null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }
}