package me.jfenn.attribouter.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

fun Context.getThemedInt(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.getDimension(Resources.getSystem().displayMetrics).toInt()
}

@StyleRes
fun Context.getThemeAttr(@AttrRes attr: Int, @StyleRes default: Int? = null): Int {
    val typedValue = TypedValue()
    return if (theme.resolveAttribute(attr, typedValue, true))
        typedValue.resourceId
    else default!!
}
