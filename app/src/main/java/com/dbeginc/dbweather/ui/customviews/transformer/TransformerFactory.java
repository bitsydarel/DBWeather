package com.dbeginc.dbweather.ui.customviews.transformer;

import android.view.View;

/**
 * Factory created to provide Transformer implementations like ResizeTransformer o
 * ScaleTransformer.
 *
 * @author Pedro Vicente Gómez Sánchez
 */

public class TransformerFactory {

    public Transformer getTransformer(final boolean resize, View view, View parent) {
        Transformer transformer = null;
        if (resize) {
            transformer = new ResizeTransformer(view, parent);
        } else {
            transformer = new ScaleTransformer(view, parent);
        }
        return transformer;
    }
}
