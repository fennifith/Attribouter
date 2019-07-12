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

class LicensesWedge : Wedge<LicensesWedge.ViewHolder>(R.layout.item_attribouter_licenses) {

    private var title: String? by attr("title", "@string/title_attribouter_licenses")
    private var showDefaults: Boolean? by attr("showDefaults", true)
    private var overflow: Int? by attr("overflow", -1)

    override fun onCreate() {
        if (showDefaults != false)
            addDefaults()
    }

    /*override fun onInit(data: GitHubData) {
        (data as? RepositoryData)?.let { repo ->
            for (tag in data.tags) {
                val mergeLicense = LicenseWedge(
                        repo = tag,
                        description = data.description,
                        licenseName = data.license?.name,
                        websiteUrl = data.homepage,
                        gitHubUrl = "https://github.com/$tag"
                ).create<LicenseWedge>()

                if (getChildren().contains(mergeLicense)) {
                    val wedge = getChildren()[getChildren().indexOf(mergeLicense)] as? LicenseWedge
                    wedge?.let {
                        it.merge(mergeLicense)
                        if (!wedge.hasAllLicense()) data.license?.key?.let { key ->
                            addRequest(LicenseData(key).apply { addTag(tag) })
                        }
                    }

                    break
                }
            }
        } ?: (data as? LicenseData)?.let { license ->
            for (info in getChildren(LicenseWedge::class.java).filter { license.tags.contains(it.token) }) {
                info.merge(LicenseWedge(
                        licenseName = license.name,
                        gitHubUrl = info.repo?.let { "https://github.com/$it" },
                        licenseUrl = license.html_url,
                        licensePermissions = license.permissions,
                        licenseConditions = license.conditions,
                        licenseLimitations = license.limitations,
                        licenseDescription = license.description,
                        licenseBody = license.body,
                        licenseKey = license.key
                ).create())
            }
        }
    }*/

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.titleView?.apply {
            visibility = if (overflow != 0) {
                title?.let { text = ResourceUtils.getString(context, title) }
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.recycler?.apply {
            visibility = if (overflow != 0) {
                layoutManager = LinearLayoutManager(context)
                overflow?.let {
                    adapter = WedgeAdapter(getChildren().subList(0, if (it > getChildren().size || it < 0) getChildren().size else it))
                }
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.overflow?.apply {
            visibility = if (overflow == 0) {
                title?.let { text = ResourceUtils.getString(context, it) }
                viewHolder.itemView.setOnClickListener { v -> OverflowDialog(v.context, title, getChildren()).show() }
                View.VISIBLE
            } else {
                viewHolder.itemView.setOnClickListener(null)
                View.GONE
            }
        }

        viewHolder.expand?.apply {
            overflow?.let {
                visibility = if (it > 0 && it < getChildren().size) {
                    setOnClickListener { v -> OverflowDialog(v.context, title, getChildren()).show() }
                    View.VISIBLE
                } else View.GONE
            }
        }
    }

    class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var titleView: TextView? = v.findViewById(R.id.title)
        var recycler: RecyclerView? = v.findViewById(R.id.recycler)
        var expand: View? = v.findViewById(R.id.expand)
        var overflow: TextView? = v.findViewById(R.id.overflow)
    }
}
