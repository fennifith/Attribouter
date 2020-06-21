package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.TextView
import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.ResourceUtils

open class HeaderWedge(private val text: String) : Wedge<HeaderWedge.ViewHolder>(R.layout.attribouter_item_header) {

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.headerView?.text = ResourceUtils.getString(context, this.text)
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var headerView: TextView? = v.findViewById(R.id.header)
    }

}
