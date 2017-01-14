package com.darelbitsy.dbweather;

import android.graphics.Color;
import com.darelbitsy.dbweather.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class ColorManager {
    private Map<Integer, String> mColorsLight;
    private Map<Integer, String> mColorsDark;
    private static int mColorToUse = 0;

    public ColorManager() {
        mColorsLight = new HashMap<>();
        mColorsDark = new HashMap<>();
        initialize();

    }

    private void initialize() {
        mColorsDark.put(R.drawable.bg_two, "#FF3079AB");
        mColorsDark.put(R.drawable.bg_three, "#ff729f98");
        mColorsDark.put(R.drawable.bg_five, "#ff98dafc");
        mColorsDark.put(R.drawable.bg_ten, "#0D47A1");
        mColorsDark.put(R.drawable.bg_eleven, "#FF7043");

        mColorsLight.put(R.drawable.bg_one, "#FFFC970B");
        mColorsLight.put(R.drawable.bg_four, "#FF19C28A");
        mColorsLight.put(R.drawable.bg_six, "#ff1b5e20");
        mColorsLight.put(R.drawable.bg_eight, "#64FFDA");
        mColorsLight.put(R.drawable.bg_seven, "#0011ff");

    }

    public int[] getDrawableForParent() {
        //TODO:db Try to to find a better way to implement it
        HashMap<Integer, String> colors = new HashMap<>(mColorsDark);
        colors.putAll(mColorsLight);
        if(mColorToUse == 0) { mColorToUse = colors.size(); }

        int drawableId = R.drawable.bg_one;

        Iterator<Integer> iterator = colors.keySet().iterator();
        for(int i = 0; i < colors.size(); i++) {
            int color = iterator.next();
            if(i == mColorToUse) {
                drawableId = color;
                break;
            }
        }

        int[] textAndBackground = new int[2];
        textAndBackground[0] = drawableId;
        textAndBackground[1] = getColorButtons(drawableId, colors);

        mColorToUse--;

        return textAndBackground;
    }

    private int getColorButtons(int drawableId, Map<Integer, String> colors) {
        return Color.parseColor(colors.get(drawableId));
    }
}
