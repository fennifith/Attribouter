package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import com.google.android.material.button.MaterialButton
import me.jfenn.attribouter.R
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.UrlClickListener
import me.jfenn.attribouter.utils.isResourceMutable
import me.jfenn.attribouter.utils.loadDrawable

open class LinkWedge(
        id: String? = null,
        name: String? = null,
        url: String? = null,
        icon: String? = null,
        isHidden: Boolean = false,
        priority: Int = 0
) : Wedge<LinkWedge.ViewHolder>(R.layout.attribouter_item_link), Mergeable<LinkWedge>, Comparable<LinkWedge> {

    private var id: String? by attr("id", id)
    private var name: String? by attr("name", name)
    protected var url: String? by attr("url", url)
    private var icon: String? by attr("icon", icon)
    private var isHidden: Boolean? by attr("hidden", isHidden)
    private var priority: Int? by attrInt("priority", priority)

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
            UrlClickListener(ResourceUtils.getString(context, url))
        else null
    }

    override fun merge(mergee: LinkWedge): LinkWedge {
        if (id == null)
            mergee.id?.let { id = it }
        if (name.isResourceMutable())
            mergee.name?.let { name = it }
        if (url.isResourceMutable())
            mergee.url?.let { url = it }
        if (icon.isResourceMutable())
            mergee.icon?.let { icon = it }
        if (mergee.isHidden())
            isHidden = true
        if (mergee.priority != 0)
            priority = mergee.priority

        return this
    }

    override fun hasAll(): Boolean {
        return true
    }

    override fun isHidden(): Boolean {
        return isHidden ?: false
    }

    open fun priority(): Int {
        return priority ?: 0
    }

    override fun equals(obj: Any?): Boolean {
        return (obj as? LinkWedge)?.let {
            (id?.equals(it.id) ?: false) || (url?.equals(obj.url) ?: false)
        } ?: super.equals(obj)
    }

    override fun compareTo(other: LinkWedge): Int {
        val a = priority()
        val b = other.priority()
        return a - b
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        val title = ResourceUtils.getString(context, name)

        viewHolder.nameView?.text = title
        viewHolder.iconView?.contentDescription = title

        context.loadDrawable(icon, R.drawable.ic_attribouter_link) { drawable ->
            (viewHolder.nameView as? MaterialButton)?.icon = drawable
            viewHolder.iconView?.setImageDrawable(drawable)
        }

        TooltipCompat.setTooltipText(viewHolder.itemView, title)
        viewHolder.itemView.setOnClickListener(getListener(context))
    }

    fun compareTo(context: Context, o: LinkWedge): Int {
        val name = ResourceUtils.getString(context, this.name)
        val oname = ResourceUtils.getString(context, o.name)
        val comparison = if (name != null && oname != null) name.compareTo(oname) else 0
        return (o.priority() - priority()) * 2 + if (comparison != 0) comparison / Math.abs(comparison) else 0
    }

    class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var nameView: TextView? = v.findViewById(R.id.name)
        var iconView: ImageView? = v.findViewById(R.id.icon)
    }

    class Comparator(private val context: Context) : java.util.Comparator<LinkWedge> {

        override fun compare(o1: LinkWedge, o2: LinkWedge): Int {
            return o1.compareTo(context, o2)
        }
    }

}
