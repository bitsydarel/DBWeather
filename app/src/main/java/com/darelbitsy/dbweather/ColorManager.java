package com.darelbitsy.dbweather;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Random;
import com.darelbitsy.dbweather.weather.Current;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class ColorManager {
    private HashMap<Integer, String> mColorsLight;
    private HashMap<Integer, String> mColorsDark;

    private int mDarkColorUsed;
    private int mLightColorUsed;

    public ColorManager() {
        mColorsLight = new HashMap<>();
        mColorsDark = new HashMap<>();
        initialize();

    }

    private void initialize() {
        mColorsDark.put(R.drawable.bg_two, "#FF3079AB");
        mColorsDark.put(R.drawable.bg_three, "#ff729f98");
        mColorsDark.put(R.drawable.bg_five, "#ff98dafc");

        mColorsLight.put(R.drawable.bg_one, "#FFFC970B");
        mColorsLight.put(R.drawable.bg_four, "#FF19C28A");
        mColorsLight.put(R.drawable.bg_six, "#ff1b5e20");

    }

    public int[] getDrawableForParent(Current currentWeather) {
        //TODO:db Try to to find a better way to implement it
        HashMap<Integer, String> colors = new HashMap<>();

        if(currentWeather.getTemperature() > 0) {
            if (currentWeather.getFormattedTime().contains("AM")) {
                colors.putAll(mColorsLight);
            }
            else {
                colors.putAll(mColorsDark);
            }
        } else { colors.putAll(mColorsDark); }

        int drawableId = R.drawable.bg_one;
        Random colorSelector = new Random();
        int choosedColor = colorSelector.nextInt(colors.size());


        for(int i = 0; i < colors.size(); i++) {
            int color = colors.keySet().iterator().next();
            if(i == choosedColor) {
                drawableId = color;
                break;
            }
        }

        int[] textAndBackgroun = new int[2];
        textAndBackgroun[0] = drawableId;
        textAndBackgroun[1] = getColorButtons(drawableId, colors);

        return textAndBackgroun;
    }

    private int getColorButtons(int drawableId, HashMap<Integer, String> colors) {
        return Color.parseColor(colors.get(drawableId));
    }
}
