package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.dialogs.OverflowDialog
import me.jfenn.attribouter.utils.ResourceUtils
import java.util.*

open class TranslatorsWedge : Wedge<TranslatorsWedge.ViewHolder>(R.layout.attribouter_item_translators) {

    var translatorsTitle: String by attr("title", "@string/attribouter_title_translators")
    val overflow: Int by attr("overflow", Int.MAX_VALUE)
    private var sortedTranslators: MutableList<Wedge<*>> = ArrayList()

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        var remaining = overflow
        val sortedList = ArrayList<Wedge<*>>()

        sortedTranslators.clear()
        for (language in Locale.getISOLanguages()) {
            var isHeader = false
            for (translator in getTypedChildren<TranslatorWedge>().filter { !it.locales.isNullOrEmpty() }) {
                var isLocale = false
                translator.locales?.split(",")?.forEach { locale ->
                    if (language == locale)
                        isLocale = true
                }

                if (isLocale) {
                    var item = translator
                    if (!isHeader) {
                        item = translator.clone().apply {
                            locales = language
                            isFirst = true
                        }

                        isHeader = true
                    }

                    // add translator entry
                    sortedTranslators?.add(item)
                    if (remaining != 0) {
                        sortedList.add(item)
                        remaining--
                    }
                }
            }
        }

        viewHolder.titleView?.apply {
            visibility = if (overflow != 0) {
                translatorsTitle?.let { text = ResourceUtils.getString(context, it) }
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.overflow?.apply {
            visibility = if (overflow == 0) {
                translatorsTitle?.let { text = ResourceUtils.getString(context, it) }
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.recycler?.apply {
            visibility = if (overflow != 0) {
                layoutManager = LinearLayoutManager(context)
                adapter = WedgeAdapter(sortedList)
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.expand?.apply {
            visibility = if (overflow != 0 && sortedTranslators?.size ?: -1 > sortedList.size) {
                setOnClickListener { v -> OverflowDialog(v.context, translatorsTitle, sortedTranslators).show() }
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.itemView.apply {
            setOnClickListener(
                    if (overflow == 0)
                        View.OnClickListener { v -> OverflowDialog(v.context, translatorsTitle, sortedTranslators).show() }
                    else null
            )
        }
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var titleView: TextView? = v.findViewById(R.id.header)
        var recycler: RecyclerView? = v.findViewById(R.id.recycler)
        var expand: View? = v.findViewById(R.id.expand)
        var overflow: TextView? = v.findViewById(R.id.overflow)
    }

}
