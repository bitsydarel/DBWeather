package com.dbeginc.dbweather;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LIST_OF_TYPEFACES;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.USER_LANGUAGE;

/**
 * Created by Darel Bitsy on 26/04/17.
 * Custom Binder for my layout
 */

public class CustomBinderAdapter {

    private CustomBinderAdapter() {
        // to prevent this to be instantiate
    }

    @BindingAdapter("setImage")
    public static void setImageViewResource(@NonNull final ImageView imageView, final int resource) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(VectorDrawableCompat.create(imageView.getResources(), resource, imageView.getContext().getTheme()));

        } else { imageView.setImageResource(resource); }
    }

    @BindingAdapter("setImageUrl")
    public static void setImage(@Nonnull final ImageView imageView, final String url) {
        if (!url.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(imageView);
        }
    }

    @BindingAdapter("setFont")
    public static void setFont(@NonNull final TextView textView, final boolean shouldSet) {
        if (shouldSet) {
            textView.setTypeface(getAppGlobalTypeFace(textView.getContext()));
        }
    }

    @BindingAdapter("setFont")
    public static void setFont(@NonNull final Button button, final boolean shouldSet) {
        if (shouldSet) {
            button.setTypeface(getAppGlobalTypeFace(button.getContext()));
        }
    }

    @BindingAdapter("setToolbarFont")
    public static void setToolbarFont(@NonNull final TextView textView, final boolean shouldSet) {
        if (shouldSet) {
            textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/toolbar_font.ttf"));
        }
    }

    @BindingAdapter("tintMyBackground")
    public static void setBackgroundTint(@NonNull final ImageButton button, final boolean shouldTint) {
        if (shouldTint && Build.VERSION_CODES.M > Build.VERSION.SDK_INT) {
            button.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private static Typeface getAppGlobalTypeFace(final Context context) {
        Typeface typeface = null;

        for (final Map.Entry<List<String>, String> languages : LIST_OF_TYPEFACES.entrySet()) {
            if (languages.getKey().contains(USER_LANGUAGE)) {
                typeface = Typeface.createFromAsset(context.getAssets(),
                        languages.getValue());
            }
        }

        return typeface;
    }
}
