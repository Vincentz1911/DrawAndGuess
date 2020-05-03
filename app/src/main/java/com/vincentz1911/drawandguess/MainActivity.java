package com.vincentz1911.drawandguess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.vincentz1911.drawandguess.adapters.CategoryAdapter;
import com.vincentz1911.drawandguess.adapters.PlayerAdapter;
import com.vincentz1911.drawandguess.models.CategoryModel;
import com.vincentz1911.drawandguess.models.ImageModel;
import com.vincentz1911.drawandguess.models.PlayerModel;
import com.vincentz1911.drawandguess.models.SettingsModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.vincentz1911.drawandguess.Statics.BACKGROUNDNAMES;
import static com.vincentz1911.drawandguess.Statics.BACKGROUNDS;
import static com.vincentz1911.drawandguess.Statics.CATEGORIES;
import static com.vincentz1911.drawandguess.Statics.COLORS;
import static com.vincentz1911.drawandguess.Statics.LANGUAGENAMES;
import static com.vincentz1911.drawandguess.Statics.TEXTS;
import static com.vincentz1911.drawandguess.Statics.fullscreen;
import static com.vincentz1911.drawandguess.Statics.msg;

public class MainActivity extends AppCompatActivity {

    private Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        act = MainActivity.this;
        setContentView(R.layout.activity_main);
        fullscreen(this);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(vis -> fullscreen(act));

        Log.d("TAG", "Number of Languages: " + LANGUAGENAMES.length);

        //region SETTINGS
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final SettingsModel settings = sp.contains("Settings")
                ? new Gson().fromJson(sp.getString("Settings", null), SettingsModel.class)
                : new SettingsModel(2, 3, 10, 12, 0, 0,
                0, TEXTS, newPlayerList(), newCategoryList());
        if (settings == null) return;
        //endregion

        //region UI LANGUAGE
        ((TextView) findViewById(R.id.txt_points)).setText(settings.Text[0]);
        ((TextView) findViewById(R.id.txt_retries)).setText(settings.Text[1]);
        ((TextView) findViewById(R.id.txt_preview)).setText(settings.Text[2]);
        ((TextView) findViewById(R.id.txt_drawing)).setText(settings.Text[3]);
        ((TextView) findViewById(R.id.txt_language)).setText(settings.Text[4]);
        ((TextView) findViewById(R.id.txt_canvas)).setText(settings.Text[5]);

        ((Button) findViewById(R.id.btn_play)).setText(settings.Text[6]);
        ((Button) findViewById(R.id.btn_join)).setText(settings.Text[7]);
        ((Button) findViewById(R.id.btn_reset)).setText(settings.Text[8]);
        ((Button) findViewById(R.id.btn_credits)).setText(settings.Text[9]);
        //endregion

        //region CATEGORY- AND PLAYER LIST
        settings.Images = 0;
        GridView categoryListView = findViewById(R.id.gv_category);
        categoryListView.setAdapter(new CategoryAdapter(act, settings.Categories));
        categoryListView.setNumColumns(2);
        for (CategoryModel category : settings.Categories)
            settings.Images += category.ImageList.size();

        ListView listView = findViewById(R.id.lv_players);
        listView.setAdapter(new PlayerAdapter(this, settings.Players));
        //endregion

        //region LANGUAGE SPINNER
        final Spinner spnLanguage = findViewById(R.id.spn_language);
        spnLanguage.setAdapter(new ArrayAdapter<>(act, R.layout.listitem_spinner, LANGUAGENAMES));
        spnLanguage.setSelection(settings.Language);
        spnLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != settings.Language) {
                    settings.Language = i;
                    if (i != 0) new MongoDB().readDB(settings, act);
                    else {
                        settings.Categories = newCategoryList();
                        settings.Text = TEXTS;
                        sp.edit().putString("Settings", new Gson().toJson(settings)).apply();
                        act.recreate();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //endregion

        //region CANVAS SPINNER
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), BACKGROUNDS[settings.Background]));
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        findViewById(R.id.lay_main).setBackground(bitmapDrawable);

        Spinner spnCanvas = findViewById(R.id.spn_canvas);
        spnCanvas.setAdapter(new ArrayAdapter<>(this, R.layout.listitem_spinner, BACKGROUNDNAMES));
        spnCanvas.setSelection(settings.Background);
        spnCanvas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BitmapDrawable bitmapDrawable1 = new BitmapDrawable(getResources(),
                        BitmapFactory.decodeResource(act.getResources(), BACKGROUNDS[i]));
                bitmapDrawable1.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                findViewById(R.id.lay_main).setBackground(bitmapDrawable1);
                settings.Background = i;
                sp.edit().putString("Settings", new Gson().toJson(settings)).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //endregion

        //region NUMBER PICKERS
        String[] npTimeValues = new String[12];
        for (int i = 0; i < 12; i++) npTimeValues[i] = String.valueOf(i * 10 + 10);

        final NumberPicker npPoints = findViewById(R.id.np_points);
        npPoints.setMinValue(1);
        npPoints.setMaxValue(5);
        npPoints.setValue(settings.Points);

        final NumberPicker npMulligans = findViewById(R.id.np_retries);
        npMulligans.setMinValue(0);
        npMulligans.setMaxValue(3);
        npMulligans.setValue(settings.Mulligans);

        final NumberPicker npPreview = findViewById(R.id.np_preview);
        npPreview.setMinValue(3);
        npPreview.setMaxValue(20);
        npPreview.setValue(settings.Preview);

        final NumberPicker npTime = findViewById(R.id.np_drawing);
        npTime.setMinValue(1);
        npTime.setMaxValue(12);
        npTime.setDisplayedValues(npTimeValues);
        npTime.setValue(settings.Timer);
        //endregion

        //region BUTTONS
        findViewById(R.id.btn_join).setOnClickListener(view -> {
            sp.edit().putString("Settings", new Gson().toJson(settings)).apply();
            Intent intent = new Intent(act, NetworkActivity.class);
            //intent.putExtra("GameSettings", gameSettings);
            startActivity(intent);
        });

        //PRESS PLAY SAVES GAME SETTINGS AND LAUNCHES GAME ACTIVITY WITH INTENT
        findViewById(R.id.btn_play).setOnClickListener(view -> {
            SettingsModel gameSettings = new SettingsModel(
                    npPoints.getValue(), npMulligans.getValue(), npPreview.getValue(),
                    npTime.getValue(), spnLanguage.getSelectedItemPosition(),
                    settings.Background, settings.Images, settings.Text,
                    settings.Players, settings.Categories);
            sp.edit().putString("Settings", new Gson().toJson(settings)).apply();

            Intent intent = new Intent(act, GameActivity.class);
            intent.putExtra("GameSettings", gameSettings);
            startActivity(intent);
        });

        //DELETES ALL PREFERENCES (GAME SETTINGS) AND RESTARTS MAIN ACTIVITY
        findViewById(R.id.btn_reset).setOnClickListener(view -> new AlertDialog.Builder(act)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure you want to reset?")
                .setMessage("By resetting all settings will return to default.")
                .setPositiveButton("OK", (dialog, which) -> {
                    fullscreen(this);
                    msg("Resetting players, settings, and images", act);
                    sp.edit().clear().apply();
                    act.recreate();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show());

        findViewById(R.id.btn_credits).setOnClickListener(view -> {
            WebView web = new WebView(act);
            web.loadUrl("file:///android_asset/credits.html");
            new AlertDialog.Builder(act).setView(web).setTitle("Credits")
                    .setIcon(R.drawable.launch_screen).show();
        });
        //endregion
    }

    //CREATES LIST OF SHUFFLES COLORS AND PLAYERS, ENABLES FIRST 2 PLAYERS
    private List<PlayerModel> newPlayerList() {
        List<Integer> rnd = new ArrayList<>();
        for (int i = 0; i < COLORS.size(); i++) rnd.add(i);
        Collections.shuffle(rnd);

        List<PlayerModel> playerList = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            playerList.add(new PlayerModel(TEXTS[20] + (i + 1),
                    COLORS.get(rnd.get(i)), 0, (i < 2)));
        return playerList;
    }

    //CREATES A LIST WITH ID OF CATEGORY NAME FROM STATIC ARRAY, AND GETS THE LOCALE NAME
    private List<CategoryModel> newCategoryList() {
        List<CategoryModel> categoryList = new ArrayList<>();
        for (String category : CATEGORIES)
            categoryList.add(new CategoryModel(category, true, newImageList(category)));
        return categoryList;
    }

    //CREATES A LIST OF IMAGES FOR EACH CATEGORY
    private List<ImageModel> newImageList(String category) {
        try {
            StringBuilder language = new StringBuilder();
            List<ImageModel> imageList = new ArrayList<>();
            String[] files = getAssets().list(category);
            if (files != null && files.length > 0)
                for (String file : files) {
                    String[] split = file.split("[_.]+");
                    if (split[1].length() > 0 && !split[1].equals("png")) Log.d("", split[1]);
                    int points = (split[1].matches("\\d+")) ? Integer.parseInt(split[1]) : 0;
                    imageList.add(new ImageModel(file, category, split[0], points, false));
                    language.append(split[0].toLowerCase()).append(", ");

                    //                  byte[] fileContent = FileUtils.readFileToByteArray(new File(String.valueOf(getAssets().openFd(file))));
//                    String encodedString = android.util.Base64.encodeToString(fileContent, Base64.DEFAULT);
                }
//            if (language.length() > 0)
//                Log.d("LNG", category + ": "
//                        + language.substring(0, language.length() - 2));
            return imageList;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}