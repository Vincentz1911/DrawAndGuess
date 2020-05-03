package com.vincentz1911.drawandguess.models;

import java.io.Serializable;

public class ColorModel implements Serializable {
    public final int Color;
    public final boolean Inverse;
    public final boolean Used;

    public ColorModel(int color, boolean inverse) {
        Color = color;
        Inverse = inverse;
        Used = false;
    }
}
