package com.vincentz1911.drawandguess.models;

import java.io.Serializable;

public class ImageModel implements Serializable {

    public final String Filename;
    public String Category;
    public final String Name;
    public String Translation;
    public final int Points;
    public Boolean Used;

    public ImageModel(String filename, String category, String name, int points, Boolean used) {
        Filename = filename;
        Category = category;
        Name = name;
        Translation = name;
        Points = points;
        Used = used;
    }
}
