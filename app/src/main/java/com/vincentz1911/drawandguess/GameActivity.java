package com.vincentz1911.drawandguess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.gson.Gson;
import com.vincentz1911.drawandguess.adapters.ColorAdapter;
import com.vincentz1911.drawandguess.adapters.ScoreboardAdapter;
import com.vincentz1911.drawandguess.models.CategoryModel;
import com.vincentz1911.drawandguess.models.ImageModel;
import com.vincentz1911.drawandguess.models.PlayerModel;
import com.vincentz1911.drawandguess.models.SettingsModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.vincentz1911.drawandguess.Statics.BACKGROUNDS;
import static com.vincentz1911.drawandguess.Statics.COLORS;
import static com.vincentz1911.drawandguess.Statics.PAINT;
import static com.vincentz1911.drawandguess.Statics.fullscreen;
import static com.vincentz1911.drawandguess.Statics.msg;

public class GameActivity extends AppCompatActivity {

    //region Fields
    private Activity act;
    private SettingsModel settings;
    private final List<PlayerModel> playerList = new ArrayList<>();
    private final List<ImageButton> toolButtons = new ArrayList<>();
    private List<ImageModel> imageList;
    private int gameRound = 1, playerTurn, imageTurn, difficulty, mulligan, timeLeft;
    private Boolean isEraser = false;
    private Timer timer;

    private Button btnGame;
    private ImageView imageView;
    private DrawingView canvas;
    private ListView scoreBoard;
    private ImageButton btnPaint, btnColor, btnSize, btnEasy, btnNormal, btnHard, btnRandom;
    private TextView txtHint, txtImage, txtClock, txtPlayer;
    private LinearLayout canvasLayout, paintTools, difficultyLayout;
    private MediaPlayer ding, tock, bib, beeb, pling, dingding;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = GameActivity.this;
        settings = (SettingsModel) getIntent().getSerializableExtra("GameSettings");

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(act)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Quit to Main Menu?")
                        .setPositiveButton("OK", (dialog, which) ->
                                startActivity(new Intent(act, MainActivity.class)))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        initSoundAndUI();
        initPlayerList();
        initImageList();
        getReadyPlayer(playerTurn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    private void initSoundAndUI() {
        setContentView(R.layout.activity_game);
        fullscreen(this);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                visibility -> fullscreen(act));

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), BitmapFactory
                .decodeResource(getResources(), BACKGROUNDS[settings.Background]));
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        findViewById(R.id.lay_game).setBackground(bitmapDrawable);

        ding = MediaPlayer.create(this, R.raw.ding);
        tock = MediaPlayer.create(this, R.raw.tock);
        bib = MediaPlayer.create(this, R.raw.bib);
        beeb = MediaPlayer.create(this, R.raw.beeb);
        pling = MediaPlayer.create(this, R.raw.pling);
        dingding = MediaPlayer.create(this, R.raw.dingding);

        canvasLayout = findViewById(R.id.lay_canvas);
        paintTools = findViewById(R.id.lay_tools);
        scoreBoard = findViewById(R.id.lv_scoreboard);
        difficultyLayout = findViewById(R.id.lay_difficulty);
        imageView = findViewById(R.id.img_picture);
        txtImage = findViewById(R.id.txt_picture);
        txtHint = findViewById(R.id.txt_hint);
        txtClock = findViewById(R.id.txt_clock);
        txtPlayer = findViewById(R.id.txt_player);
        btnGame = findViewById(R.id.btn_game);
        btnPaint = findViewById(R.id.btn_clear);
        btnColor = findViewById(R.id.btn_color);
        btnSize = findViewById(R.id.btn_size);
        btnEasy = findViewById(R.id.btn_easy);
        btnNormal = findViewById(R.id.btn_normal);
        btnHard = findViewById(R.id.btn_hard);
        btnRandom = findViewById(R.id.btn_random);
        toolButtons.add(btnPaint);
        toolButtons.add(btnColor);
        toolButtons.add(btnSize);

        paintTools.setVisibility(View.GONE);
    }

    private void initPlayerList() {
        List<PlayerModel> settingsPlayerList = settings.Players;
        for (PlayerModel player : settingsPlayerList) if (player.Enabled) playerList.add(player);

        final ScoreboardAdapter adapter = new ScoreboardAdapter(this, playerList);
        scoreBoard.setAdapter(adapter);
        scoreBoard.setOnItemClickListener((adapterView, view, i, l) -> {
            playerList.get(i).Score += settings.Points + imageList.get(imageTurn).Points;
            pling.start();
            adapter.notifyDataSetChanged();
        });
        scoreBoard.setOnItemLongClickListener((adapterView, view, i, l) -> {
            playerList.get(i).Score--;
            adapter.notifyDataSetChanged();
            return true;
        });
    }

    private void initImageList() {
        imageList = new ArrayList<>();
        for (CategoryModel category : settings.Categories)
            if (category.Enabled)
                for (ImageModel im : category.ImageList)
                    if (!im.Used) imageList.add(im);
        Collections.shuffle(imageList);
    }

    private ImageModel getImageFromList() {
        imageTurn++;
        ImageModel image = imageList.get(imageTurn);
        image.Used = true;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString("Settings", new Gson().toJson(settings)).apply();
        return image;
    }

    private void getReadyPlayer(int playerTurn) {
        if (imageList.size() < playerList.size() * gameRound) {
            msg("There is not enough images left! Reset the game!", this);
            startActivity(new Intent(act, MainActivity.class));
        }
        difficulty = 0;
        mulligan = 0;
        txtHint.setText(R.string.hint_start);
        txtImage.setText(String.format("%s %s", settings.Text[15], settings.Text[19]));
        txtPlayer.setVisibility(View.VISIBLE);
        difficultyLayout.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        txtClock.setText(String.valueOf(settings.Preview));

        int c = playerList.get(playerTurn).Color.Inverse ? Color.WHITE : Color.BLACK;
        txtPlayer.setTextColor(c);

        txtPlayer.setText(String.format(Locale.getDefault(), "%s %d\n%s",
                settings.Text[23], gameRound, playerList.get(playerTurn).Name));
        txtPlayer.setBackgroundResource(playerList.get(playerTurn).Color.Color);

        btnGame.setTextColor(c);
        btnGame.setText(settings.Text[10]);
        btnGame.setBackgroundResource(playerList.get(playerTurn).Color.Color);

        btnEasy.setOnClickListener(view -> {
            difficulty = 1;
            btnGame.setText(settings.Text[11]);
            txtImage.setText(String.format("%s %s", settings.Text[15], settings.Text[19]));
        });

        btnNormal.setOnClickListener(view -> {
            difficulty = 2;
            btnGame.setText(settings.Text[11]);
            txtImage.setText(String.format("%s %s", settings.Text[16], settings.Text[19]));
        });

        btnHard.setOnClickListener(view -> {
            difficulty = 3;
            btnGame.setText(settings.Text[11]);
            txtImage.setText(String.format("%s %s", settings.Text[17], settings.Text[19]));
        });

        btnRandom.setOnClickListener(view -> {
            difficulty = new Random().nextInt(3) + 1;
            btnGame.setText(settings.Text[11]);
            txtImage.setText(String.format("%s %s", settings.Text[18], settings.Text[19]));
        });

        btnGame.setOnClickListener(view -> {
            if (difficulty > 0) preview();
            //else txtHint.setText(R.string.difficulty);
        });
    }

    private void preview() {
        txtHint.setText(R.string.hint_click_draw);
        imageView.setVisibility(View.VISIBLE);
        txtImage.setVisibility(View.VISIBLE);
        txtClock.setVisibility(View.VISIBLE);
        txtPlayer.setVisibility(View.GONE);
        scoreBoard.setVisibility(View.GONE);
        difficultyLayout.setVisibility(View.GONE);

        ImageModel img = getImageFromList();
        try {
            imageView.setImageDrawable(Drawable.createFromStream(getAssets()
                    .open(img.Category + "/" + img.Filename), null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtImage.setText(String.format("%s (%s)", img.Translation, img.Category));
        imageView.setOnLongClickListener(view -> retry());

        timeLeft = settings.Preview;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                clockStarted(true);
            }
        }, 0, 1000);

        btnGame.setText(settings.Text[12]);
        btnGame.setOnClickListener(view -> {
            if (timeLeft > 0) timeLeft = 0;
        });
    }

    private Boolean retry() {
        mulligan++;
        if (mulligan > settings.Mulligans) {
            msg("No more Retries!", act);
            return true;
        }
        dingding.start();
        timer.cancel();
        playerList.get(playerTurn).Score--;
        msg("Getting new Image!", act);
        preview();
        return true;
    }

    private void countdown() {
        txtHint.setText(R.string.hint_get_ready);
        txtImage.setText(null);
        imageView.setVisibility(View.GONE);

        PAINT.setAntiAlias(true);
        PAINT.setDither(true);
        PAINT.setColor(Color.BLACK);
        PAINT.setStyle(Paint.Style.STROKE);
        PAINT.setStrokeJoin(Paint.Join.ROUND);
        PAINT.setStrokeCap(Paint.Cap.ROUND);
        PAINT.setStrokeWidth(10);

        for (ImageButton btn : toolButtons)
            DrawableCompat.setTint(DrawableCompat.wrap(btn.getDrawable()), PAINT.getColor());
        colorDialog();
    }

    void colorDialog() {
        GridView gridView = new GridView(this);
        gridView.setAdapter(new ColorAdapter(this, COLORS));
        gridView.setNumColumns(5);
        gridView.setGravity(Gravity.CENTER);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(gridView)
                .setTitle(String.format("%s %s", settings.Text[10], settings.Text[24])).create();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            for (ImageButton btn : toolButtons)
                DrawableCompat.setTint(DrawableCompat.wrap(btn.getDrawable()),
                        ContextCompat.getColor(this, COLORS.get(position).Color));
            PAINT.setColor(ContextCompat.getColor(this, COLORS.get(position).Color));
            dialog.dismiss();
            fullscreen(this);
        });
        dialog.show();
        fullscreen(this);
    }

    private void clockStarted(final boolean preview) {
        runOnUiThread(() -> {
            if (preview) {
                if (timeLeft >= 0) txtClock.setText(String.valueOf(timeLeft));
                if (timeLeft == 0) countdown();
                if (timeLeft < 1 && timeLeft > -3) {
                    txtClock.setText(String.valueOf(timeLeft + 3));
                    bib.start();
                }
                if (timeLeft < -2) {
                    beeb.start();
                    timer.cancel();
                    drawing();
                }
            } else {
                txtClock.setText(String.valueOf(timeLeft));
                if (timeLeft < 30 && timeLeft > 25) {
                    txtImage.setText(imageList.get(imageTurn).Category);
                    tock.start();
                }

                if (timeLeft < 11 && timeLeft > 0) {
                    //txtImage.setText(hint);
                    tock.start();
                }
                if (timeLeft < 1) {
                    txtHint.setText(R.string.hint_times_up);
                    PAINT.setColor(Color.TRANSPARENT);
                    paintTools.setVisibility(View.GONE);
                    ding.start();
                    timer.cancel();
                }
            }
            timeLeft--;
        });
    }

    private void drawing() {
        newCanvas();
        txtHint.setText(R.string.hint_reveal);
        paintTools.setVisibility(View.VISIBLE);
        txtClock.setVisibility(View.VISIBLE);

        btnPaint.setOnLongClickListener(view -> newCanvas());
        btnPaint.setOnClickListener(view -> paintOrErase());
        btnColor.setOnClickListener(view -> {
            if (isEraser) paintOrErase();
            colorDialog();
        });
        btnSize.setOnClickListener(view -> {
            if (isEraser) PAINT.setStrokeWidth(PAINT.getStrokeWidth() < 60
                    ? PAINT.getStrokeWidth() * 2 : 5);
            else PAINT.setStrokeWidth(PAINT.getStrokeWidth() < 40
                    ? PAINT.getStrokeWidth() * 2 : 5);
        });

        timeLeft = settings.Timer * 10;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                clockStarted(false);
            }
        }, 0, 1000);

        btnGame.setText(settings.Text[13]);
        btnGame.setOnClickListener(view -> finishedDrawing());
    }

    private boolean newCanvas() {
        canvasLayout.removeView(canvas);
        canvas = new DrawingView(this);
        canvasLayout.addView(canvas);
        return true;
    }

    private void paintOrErase() {
        if (isEraser) {
            isEraser = false;
            PAINT.setMaskFilter(new MaskFilter());
            PAINT.setXfermode(null);
            PAINT.setStrokeWidth(PAINT.getStrokeWidth() / 2);
            btnPaint.setImageResource(R.drawable.ic_paint);
            DrawableCompat.setTint(DrawableCompat.wrap(btnPaint.getDrawable()), PAINT.getColor());
        } else {
            isEraser = true;
            PAINT.setMaskFilter(null);
            PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            PAINT.setStrokeWidth(PAINT.getStrokeWidth() * 2);
            btnPaint.setImageResource(R.drawable.ic_eraser);
            DrawableCompat.setTint(DrawableCompat.wrap(btnPaint.getDrawable()), PAINT.getColor());
        }
    }

    private void finishedDrawing() {
        txtHint.setText(R.string.hint_award_points);
        txtImage.setText(String.format("%s (%s)", imageList.get(imageTurn).Translation,
                imageList.get(imageTurn).Category));
        canvasLayout.removeView(canvas);
        scoreBoard.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        paintTools.setVisibility(View.GONE);
        timer.cancel();
        btnGame.setText(settings.Text[14]);
        btnGame.setOnClickListener(view -> {
            playerTurn++;
            if (playerTurn >= playerList.size()) {
                playerTurn = 0;
                msg("New Round!", act);
                gameRound++;
            }
            getReadyPlayer(playerTurn);
        });
    }
}