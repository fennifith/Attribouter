package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import com.google.android.material.button.MaterialButton
import me.jfenn.androidutils.getThemedColor
import me.jfenn.androidutils.setBackgroundTint
import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.UrlClickListener
import me.jfenn.attribouter.utils.loadDrawable

open class LinkWedge(
        id: String? = null,
        name: String? = null,
        url: String? = null,
        icon: String? = null,
        priority: Int = 0
) : Wedge<LinkWedge.ViewHolder>(R.layout.attribouter_item_link), Comparable<LinkWedge> {

    var id: String? by attr("id", id)
    var name: String? by attr("name", name)
    var url: String? by attr("url", url)
    var icon: String? by attr("icon", icon)
    var priority: Int by object : attrInt<LinkWedge>("priority", priority) {
        // ignore new "0" values (likely unset)
        override fun apply(original: Int?, value: Int?): Int? = if (value == 0) original else value
    }

    var tintColor: Int? = null

    override fun onCreate() {
        if (!url.isNullOrEmpty()) this.url = url?.let {
            if (it.startsWith("http")) it else "http://$it"
        }
    }

    /**
     * Returns a View.OnClickListener that opens the link.
     *
     * @param context the current context
     * @return a click listener to be applied to the respective view
     */
    open fun getListener(context: Context): View.OnClickListener? {
        return if (!url.isNullOrEmpty() && URLUtil.isValidUrl(url))
            UrlClickListener(url)
        else null
    }

    override fun equals(obj: Any?): Boolean {
        return (obj as? LinkWedge)?.let {
            (id?.equals(it.id) ?: false) || (url?.equals(obj.url) ?: false)
        } ?: super.equals(obj)
    }

    override fun compareTo(other: LinkWedge): Int {
        return other.priority - priority
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        val title = ResourceUtils.getString(context, name)
        val listener = getListener(context)

        viewHolder.nameView?.text = title
        viewHolder.iconView?.contentDescription = title

        context.loadDrawable(icon, R.drawable.attribouter_ic_link) { drawable ->
            (viewHolder.nameView as? MaterialButton)?.icon = drawable
            viewHolder.iconView?.apply {
                setImageDrawable(drawable)

                val color = tintColor ?: context.getThemedColor(android.R.attr.textColorPrimary)
                setBackgroundTint(color)
                setColorFilter(color)
            }
        }

        TooltipCompat.setTooltipText(viewHolder.itemView, title)

        (viewHolder.iconView as? ImageButton)?.isClickable = false
        (viewHolder.nameView as? MaterialButton)?.isClickable = false
        viewHolder.itemView.setOnClickListener(listener)
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var nameView: TextView? = v.findViewById(R.id.name)
        var iconView: ImageView? = v.findViewById(R.id.icon)
    }

}
