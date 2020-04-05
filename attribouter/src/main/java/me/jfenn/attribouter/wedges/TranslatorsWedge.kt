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

class TranslatorsWedge : Wedge<TranslatorsWedge.ViewHolder>(R.layout.item_attribouter_translators) {

    private var translatorsTitle: String? by attr("title", "@string/title_attribouter_translators")
    private val overflow: Int? by attr("overflow", -1)
    private var sortedTranslators: MutableList<Wedge<*>>? = null

    /*override fun onInit(data: GitHubData) {
        if (data is ContributorsData) {
            if (!data.contributors.isNullOrEmpty()) for (contributor in data.contributors.filter { !it.login.isNullOrEmpty() }) {
                val child = addChild(TranslatorWedge(
                        login = contributor.login,
                        avatarUrl = contributor.avatar_url
                ).create())

                if (child is Mergeable<*> && !(child as Mergeable<*>).hasAll())
                    addRequest(UserData(contributor.login))
            }
        } else if (data is UserData) {
            addChild(0, TranslatorWedge(
                    login = data.login,
                    name = data.name,
                    avatarUrl = data.avatar_url,
                    blog = data.blog,
                    email = data.email
            ).create())
        }
    }*/

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        var remaining = overflow ?: -1
        val sortedList = ArrayList<Wedge<*>>()

        sortedTranslators = ArrayList()
        for (language in Locale.getISOLanguages()) {
            var isHeader = false
            for (translator in getTypedChildren<TranslatorWedge>().filter { !it.locales.isNullOrEmpty() }) {
                var isLocale = false
                translator.locales?.split(",")?.forEach { locale ->
                    if (language == locale)
                        isLocale = true
                }

                if (isLocale) {
                    if (!isHeader) { // add header/title wedge for language if not present yet
                        val header = HeaderWedge(Locale(language).displayLanguage)
                        sortedTranslators?.add(header)
                        if (remaining != 0)
                            sortedList.add(header)

                        isHeader = true
                    }

                    // add translator entry
                    sortedTranslators?.add(translator)
                    if (remaining != 0) {
                        sortedList.add(translator)
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
            visibility = if ((overflow ?: 0) != 0 && sortedTranslators?.size ?: -1 > sortedList.size) {
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

    class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var titleView: TextView? = v.findViewById(R.id.contributorsTitle)
        var recycler: RecyclerView? = v.findViewById(R.id.recycler)
        var expand: View? = v.findViewById(R.id.expand)
        var overflow: TextView? = v.findViewById(R.id.overflow)
    }

}
