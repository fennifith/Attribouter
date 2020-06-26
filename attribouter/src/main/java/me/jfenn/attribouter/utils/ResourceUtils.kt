package me.jfenn.attribouter.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import me.jfenn.attribouter.R

fun Context.getDrawableSafe(res: Int) : Drawable? {
    return try {
        ContextCompat.getDrawable(this, res)
    } catch (e: Resources.NotFoundException) {
        VectorDrawableCompat.create(this.resources, res, this.theme)
    }
}

fun Context.loadDrawable(identifier: String?, defaultRes: Int, set: (drawable: Drawable) -> Unit) {
    val resInt = ResourceUtils.getResourceInt(this, identifier)
    if (resInt != null && resInt != -1)
        getDrawableSafe(resInt)?.let { set(it); return }

    if (identifier != null) {
        val default = getDrawableSafe(defaultRes)
        Glide.with(this)
                .load(identifier)
                .apply(RequestOptions().placeholder(default).error(default))
                .into(object : CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        set(resource)
                    }

                })
    } else getDrawableSafe(defaultRes)?.let { set(it) }
}

object ResourceUtils {

    @JvmStatic
    fun getString(context: Context, identifier: String?): String? {
        var ret = identifier
        if (identifier?.startsWith("^") == true)
            ret = identifier.substring(1)

        val resource = getResourceInt(context, ret)
        if (ret?.startsWith("@") == true)
            ret = null

        return if (resource != null) context.getString(resource) else ret
    }

    fun getResourceInt(context: Context, identifier: String?): Int? {
        var identifier = identifier
        if (identifier != null && identifier.startsWith("^")) identifier = identifier.substring(1)
        if (identifier != null && identifier.startsWith("@")) {
            identifier = identifier.substring(1)
            if (identifier.contains("/")) {
                val identifiers = identifier.split("/".toRegex()).toTypedArray()
                if (identifiers[0].length > 0 && identifiers[1].length > 0) {
                    val res = context.resources.getIdentifier(identifiers[1], identifiers[0], context.packageName)
                    return if (res == 0) null else res
                }
            } else {
                try {
                    return identifier.toInt()
                } catch (ignored: NumberFormatException) {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    @JvmStatic
    @StyleRes
    fun getThemeResourceAttribute(context: Context, @StyleableRes styleable: Int, @StyleRes defaultTheme: Int): Int {
        val array = context.obtainStyledAttributes(null, R.styleable.AttribouterTheme, 0, defaultTheme)
        val id = array.getResourceId(styleable, defaultTheme)
        array.recycle()
        return id
    }
}
