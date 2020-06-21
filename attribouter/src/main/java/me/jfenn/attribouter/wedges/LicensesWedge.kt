package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.addDefaults
import me.jfenn.attribouter.dialogs.OverflowDialog
import me.jfenn.attribouter.utils.ResourceUtils

open class LicensesWedge : Wedge<LicensesWedge.ViewHolder>(R.layout.attribouter_item_licenses) {

    var title: String by attr("title", "@string/attribouter_title_licenses")
    var showDefaults: Boolean by attr("showDefaults", true)
    var overflow: Int by attr("overflow", Int.MAX_VALUE)

    override fun onCreate() {
        if (showDefaults)
            addDefaults()
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.titleView?.apply {
            visibility = if (overflow != 0) {
                text = ResourceUtils.getString(context, title)
                View.VISIBLE
            } else View.GONE
        }

        val licenses = getTypedChildren<LicenseWedge>().take(overflow)

        viewHolder.recycler?.apply {
            visibility = if (overflow != 0) {
                layoutManager = LinearLayoutManager(context)
                adapter = WedgeAdapter(licenses)
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.overflow?.apply {
            visibility = if (overflow == 0) {
                text = ResourceUtils.getString(context, title)
                viewHolder.itemView.setOnClickListener { v -> OverflowDialog(v.context, title, getChildren()).show() }
                View.VISIBLE
            } else {
                viewHolder.itemView.setOnClickListener(null)
                View.GONE
            }
        }

        viewHolder.expand?.apply {
            visibility = if (overflow > 0 && overflow < getChildren().size) {
                setOnClickListener { v -> OverflowDialog(v.context, title, getChildren()).show() }
                View.VISIBLE
            } else View.GONE
        }
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var titleView: TextView? = v.findViewById(R.id.header)
        var recycler: RecyclerView? = v.findViewById(R.id.recycler)
        var expand: View? = v.findViewById(R.id.expand)
        var overflow: TextView? = v.findViewById(R.id.overflow)
    }
}
