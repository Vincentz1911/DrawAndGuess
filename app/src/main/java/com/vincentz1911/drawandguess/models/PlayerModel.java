package com.vincentz1911.drawandguess.models;

import java.io.Serializable;

public class PlayerModel implements Serializable {
    public String Name;
    public ColorModel Color;
    public int Score;
    public Boolean Enabled;

    public PlayerModel(String name, ColorModel color, int score, Boolean enabled) {
        Name = name;
        Color = color;
        Score = score;
        Enabled = enabled;
    }
}
