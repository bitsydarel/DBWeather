package com.darelbitsy.dbweather;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class ColorManager {
    private HashMap<Integer, String> colors;
    private int parentColor;

    public ColorManager() {
        colors = new HashMap<>();
        initialize();

    }

    private void initialize() {
        colors.put(R.drawable.bg_one, "#FFFC970B");
        colors.put(R.drawable.bg_two, "#FF3079AB");
        colors.put(R.drawable.bg_three, "#ff7d669e");
        colors.put(R.drawable.bg_four, "#FF19C28A");
    }
    private String[] mColors = {

            "#ffc25975", // mauve
            "#ffe15258", // red
            "#ff838cc7", // lavender
            "#ff53bbb4", // aqua
            "#ff51b46d", // green
            "#ffe0ab18", // mustard
            "#fff092b0", // pink// orange
    };

    public int getDrawableForParent() {
        //TODO:db Try to to find a better way to implement it
        int[] drawblePicker = new int[colors.size()];
        int drawableId;
        //Random colorSelector = new Random();
        //int choosedColor = colorSelector.nextInt(mColors.length);

        drawableId = colors.keySet().stream()
                .findAny()
                .get();

        return drawableId;
    }

    public int getColorButtons(int drawableId) {
        return Color.parseColor(colors.get(drawableId));
    }
}
