package com.vincentz1911.drawandguess.models;

import java.io.Serializable;
import java.util.List;

public class CategoryModel implements Serializable {
    public String Name;
    public String Translation;
    public boolean Enabled;
    public List<ImageModel> ImageList;

    public CategoryModel(String name, boolean enabled, List<ImageModel> imageList) {
        Name = name;
        Translation = name;
        Enabled = enabled;
        ImageList = imageList;
    }
}