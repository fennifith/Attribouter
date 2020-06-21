package me.jfenn.attribouter.views

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.appbar.MaterialToolbar
import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.ColorUtils
import me.jfenn.attribouter.utils.getThemedColor

class CustomToolbar : MaterialToolbar {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { init() }

    fun init() {
        val color = if (ColorUtils.isColorLight(context.getThemedColor(R.attr.colorPrimary))) Color.BLACK else Color.WHITE
        setTitleTextColor(color)

        navigationIcon = VectorDrawableCompat.create(resources, R.drawable.attribouter_ic_arrow_back, context.theme)?.let {
            DrawableCompat.wrap(it)
        }?.apply {
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

}