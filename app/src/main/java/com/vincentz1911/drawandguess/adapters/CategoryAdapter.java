package com.vincentz1911.drawandguess.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vincentz1911.drawandguess.R;
import com.vincentz1911.drawandguess.models.CategoryModel;
import com.vincentz1911.drawandguess.models.ImageModel;

import java.util.List;
import java.util.Locale;

import static com.vincentz1911.drawandguess.Statics.fullscreen;

public class CategoryAdapter extends ArrayAdapter<CategoryModel> {
    public CategoryAdapter(Context c, List<CategoryModel> cat) {
        super(c, 0, cat);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(getContext())
                .inflate(R.layout.griditem_category, parent, false);

        CategoryModel cat = getItem(position);
        if (cat == null) return view;

        int unusedImages = 0;
        for (ImageModel image : cat.ImageList) if (!image.Used) unusedImages++;

        TextView categoryTextView = view.findViewById(R.id.txt_category);
        categoryTextView.setText(String.format(Locale.getDefault(), "%s\n%1d/%2d",
                cat.Translation.substring(0, 1).toUpperCase() + cat.Translation.substring(1),
                unusedImages, cat.ImageList.size()));

        if (cat.Enabled) categoryTextView.setBackgroundResource(R.drawable.category_enabled);
        else categoryTextView.setBackgroundResource(R.drawable.category_disabled);

        view.setOnClickListener(view1 -> imageDialog(cat, getContext()));
        view.setOnLongClickListener(view1 -> {
            cat.Enabled = !cat.Enabled;
            notifyDataSetChanged();
            return true;
        });
        return view;
    }

    private void imageDialog(CategoryModel category, final Context c) {
        GridView gridView = new GridView(c);
        gridView.setGravity(Gravity.CENTER);
        gridView.setAdapter(new ImageAdapter(c, category.ImageList));
        gridView.setNumColumns(4);
        gridView.setOnItemClickListener((parent, view, position, id) -> fullscreen((Activity) c));

        new AlertDialog.Builder(c).setView(gridView).setTitle(category.Translation).show();
        fullscreen((Activity) c);
    }
}