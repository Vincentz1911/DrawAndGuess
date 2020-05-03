package com.vincentz1911.drawandguess.models;

import java.io.Serializable;
import java.util.List;

public class SettingsModel implements Serializable {
    public final int Points;
    public final int Mulligans;
    public final int Preview;
    public final int Timer;
    public int Language;
    public int Background;
    public int Images;
    public String[] Text;
    public final List<PlayerModel> Players;
    public List<CategoryModel> Categories;

    public SettingsModel(int points, int mulligans, int preview, int timer,
                         int language, int background, int images, String[] text,
                         List<PlayerModel> players, List<CategoryModel> categories) {
        Points = points;
        Mulligans = mulligans;
        Preview = preview;
        Timer = timer;
        Language = language;
        Background = background;
        Images = images;
        Text = text;
        Players = players;
        Categories = categories;
    }
}