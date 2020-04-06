package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.TextView

import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.ResourceUtils

class HeaderWedge(private val text: String) : Wedge<Wedge.ViewHolder>(R.layout.item_attribouter_header) {

    override fun getViewHolder(v: View): Wedge.ViewHolder {
        return Wedge.ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: Wedge.ViewHolder) {
        (viewHolder.itemView as TextView).text = ResourceUtils.getString(context, this.text)
    }

}
