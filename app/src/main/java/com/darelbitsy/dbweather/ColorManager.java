package com.darelbitsy.dbweather;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class ColorManager {
    private String[] mColors = {
            "#ff39add1", // light blue
            "#ff3079ab", // dark blue
            "#ffc25975", // mauve
            "#ffe15258", // red
            "#fff9845b", // orange
            "#ff838cc7", // lavender
            "#ff7d669e", // purple
            "#ff53bbb4", // aqua
            "#ff51b46d", // green
            "#ffe0ab18", // mustard
            "#fff092b0", // pink
            "#FFFC970B" // orange
    };

    public int getColor() {
        int colorID;
        Random colorSelector = new Random();
        int choosedColor = colorSelector.nextInt(mColors.length);
        colorID = Color.parseColor(mColors[choosedColor]);

        return colorID;
    }
}
