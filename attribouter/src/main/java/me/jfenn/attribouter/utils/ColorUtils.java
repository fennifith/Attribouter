package me.jfenn.attribouter.utils;

import android.graphics.Color;
import androidx.annotation.ColorInt;

public class ColorUtils {

    public static boolean isColorLight(@ColorInt int color) {
        return getColorDarkness(color) < 0.5;
    }

    private static double getColorDarkness(@ColorInt int color) {
        if (color == Color.BLACK)
            return 1.0;
        else if (color == Color.WHITE || color == Color.TRANSPARENT)
            return 0.0;
        else
            return (1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255);
    }

}
