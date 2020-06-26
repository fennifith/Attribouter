package me.jfenn.attribouter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.wedges.Wedge

class WedgeAdapter(private val wedges: List<Wedge<*>>) : RecyclerView.Adapter<Wedge.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Wedge.ViewHolder {
        val info = wedges[viewType]
        return info.getViewHolder(LayoutInflater.from(parent.context).inflate(info.layoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: Wedge.ViewHolder, position: Int) {
        (wedges[position] as? Wedge<Wedge.ViewHolder>)?.bind(holder.itemView.context, holder)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return wedges.size
    }

}