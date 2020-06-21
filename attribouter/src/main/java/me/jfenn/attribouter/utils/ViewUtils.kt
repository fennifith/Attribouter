package me.jfenn.attribouter.utils

import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

var View.backgroundTint: Int
    get() = 0
    set(@ColorInt color) {
        background = DrawableCompat.wrap(background).apply {
            DrawableCompat.setTint(this, color)
        }
    }
