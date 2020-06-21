package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.isResourceMutable
import me.jfenn.attribouter.utils.toTitleString

open class LicenseWedge(
        repo: String? = null,
        title: String? = null,
        description: String? = null,
        licenseName: String? = null,
        websiteUrl: String? = null,
        gitHubUrl: String? = null,
        licenseUrl: String? = null,
        var licensePermissions: Array<String>? = null,
        var licenseConditions: Array<String>? = null,
        var licenseLimitations: Array<String>? = null,
        var licenseDescription: String? = null,
        licenseBody: String? = null,
        licenseKey: String? = null
) : Wedge<LicenseWedge.ViewHolder>(R.layout.attribouter_item_license), Mergeable<LicenseWedge> {

    var repo: ProviderString? by attrProvider("repo", repo)
    var title: String? by attr("title", title)
    var description: String? by attr("description", description)
    var licenseName: String? by attr("licenseName", licenseName)
    var websiteUrl: String? by attr("website", websiteUrl)
    var gitHubUrl: String? by attr("gitHubUrl", gitHubUrl)
    var licenseUrl: String? by attr("licenseUrl", licenseUrl)
    var licenseBody: String? by attr("licenseBody", licenseBody)
    var licenseKey: ProviderString? by attrProvider("license", licenseKey)
    override val isHidden: Boolean = false

    internal var token: String? = null

    override fun onCreate() {
        token = repo?.id ?: title

        if (!websiteUrl.isNullOrEmpty())
            websiteUrl?.let { addChild(WebsiteLinkWedge(it, 2)) }

        repo?.let { addChild(RepoLinkWedge(it.id, 1))}

        if (!licenseBody.isNullOrEmpty() && !licenseUrl.isNullOrEmpty())
            addChild(LicenseLinkWedge(this, 0))

        if (!hasAllGeneric()) repo?.let {
            lifecycle?.launch {
                withContext(Dispatchers.IO) {
                    lifecycle?.provider?.getRepository(it)
                }?.let { data -> onRepository(data) }
            }
        }

        licenseKey?.let { key ->
            lifecycle?.launch {
                withContext(Dispatchers.IO) {
                    lifecycle?.provider?.getLicense(key)
                }?.let { onLicense(it) }
            }
        }
    }

    private fun onRepository(data: RepoData) {
        if (description.isResourceMutable() && !data.description.isNullOrEmpty())
            description = data.description
        if (licenseName.isResourceMutable() && !data.license?.name.isNullOrEmpty())
            licenseName = data.license?.name
        if (websiteUrl.isResourceMutable() && !data.websiteUrl.isNullOrEmpty())
            websiteUrl = data.websiteUrl

        data.license?.id?.let { id ->
            if (!hasAllLicense()) lifecycle?.launch {
                withContext(Dispatchers.IO) {
                    lifecycle?.provider?.getLicense(id)
                }?.let { onLicense(it) }
            }
        }

        notifyItemChanged()
    }

    private fun onLicense(data: LicenseData) {
        if (licenseName.isResourceMutable() && !data.name.isNullOrEmpty())
            licenseName = data.name
        if (licenseDescription.isResourceMutable() && !data.description.isNullOrEmpty())
            licenseDescription = data.description
        if (licenseUrl.isResourceMutable() && !data.infoUrl.isNullOrEmpty())
            licenseUrl = data.infoUrl

        licensePermissions = data.permissions
        licenseConditions = data.conditions
        licenseLimitations = data.limitations

        if (licenseBody.isResourceMutable() && !data.body.isNullOrEmpty())
            licenseBody = data.body

        if (!licenseBody.isNullOrEmpty() && !licenseUrl.isNullOrEmpty())
            addChild(LicenseLinkWedge(this, 0))

        notifyItemChanged()
    }

    override fun merge(mergee: LicenseWedge): LicenseWedge {
        if ((title == null || !title!!.startsWith("^")) && !mergee.title.isNullOrEmpty())
            title = mergee.title
        if ((description == null || !description!!.startsWith("^")) && !mergee.description.isNullOrEmpty())
            description = mergee.description
        if (licenseName == null || !licenseName!!.startsWith("^"))
            mergee.licenseName?.let { licenseName = it }
        if ((websiteUrl == null || !websiteUrl!!.startsWith("^")) && !mergee.websiteUrl.isNullOrEmpty())
            websiteUrl = mergee.websiteUrl
        if (gitHubUrl == null || !gitHubUrl!!.startsWith("^"))
            mergee.gitHubUrl?.let { gitHubUrl = it }
        if (licenseUrl == null || !licenseUrl!!.startsWith("^"))
            mergee.licenseUrl?.let { licenseUrl = it }

        mergee.licensePermissions?.let { licensePermissions = it }
        mergee.licenseConditions?.let { licenseConditions = it }
        mergee.licenseLimitations?.let { licenseLimitations = it }
        mergee.licenseDescription?.let { licenseDescription = it }

        if (licenseBody == null || !licenseBody!!.startsWith("^"))
            mergee.licenseBody?.let { licenseBody = it }

        addChildren(mergee.getChildren())
        return this
    }

    override fun hasAll(): Boolean {
        return hasAllGeneric() && hasAllLicense()
    }

    fun hasAllGeneric(): Boolean {
        return (description?.startsWith("^") ?: false)
                && (websiteUrl?.startsWith("^") ?: false)
                && (licenseName?.startsWith("^") ?: false)
    }

    fun hasAllLicense(): Boolean {
        return (licenseName?.startsWith("^") ?: false)
                && (licenseUrl?.startsWith("^") ?: false)
                && (licenseBody?.startsWith("^") ?: false)
    }

    override fun equals(other: Any?): Boolean {
        return (other as? LicenseWedge)?.let {
            return repo?.id?.equals(it.repo?.id, ignoreCase = true) ?: false
                    || repo?.id?.equals(it.title, ignoreCase = true) ?: false
                    || title?.equals(it.repo?.id, ignoreCase = true) ?: false
                    || title?.equals(it.title, ignoreCase = true) ?: false
        } ?: super.equals(other)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.titleView?.apply {
            (title ?: repo?.id?.toTitleString())?.let { text = ResourceUtils.getString(context, it) }
        }

        viewHolder.descriptionView?.apply {
            text = ResourceUtils.getString(context, description)?.replace("\n", "")
        }

        viewHolder.licenseView?.apply {
            licenseName?.let {
                text = ResourceUtils.getString(context, licenseName)
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        val links = getTypedChildren<LinkWedge>().filter { !it.isHidden }.sorted()

        viewHolder.links?.apply {
            if (links.isNotEmpty()) {
                adapter = WedgeAdapter(links)
                layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }

                visibility = View.VISIBLE
            } else visibility = View.GONE
        }

        viewHolder.itemView.apply {
            var importantLink: LinkWedge? = null
            var clickListener: View.OnClickListener? = null
            for (link in links) {
                if (importantLink == null || link.priority > importantLink.priority) {
                    val listener = link.getListener(context)
                    if (listener != null) {
                        clickListener = listener
                        importantLink = link
                    }
                }
            }

            setOnClickListener(clickListener)
        }
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var titleView: TextView? = v.findViewById(R.id.title)
        var descriptionView: TextView? = v.findViewById(R.id.description)
        var licenseView: TextView? = v.findViewById(R.id.license)
        var links: RecyclerView? = v.findViewById(R.id.projectLinks)
    }

}
