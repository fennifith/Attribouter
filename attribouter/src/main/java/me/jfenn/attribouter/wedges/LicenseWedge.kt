package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.getProviderOrNull
import me.jfenn.attribouter.utils.toListString
import java.util.regex.Pattern

class LicenseWedge(
        repo: String? = null,
        title: String? = null,
        description: String? = null,
        licenseName: String? = null,
        websiteUrl: String? = null,
        gitHubUrl: String? = null,
        licenseUrl: String? = null,
        private var licensePermissions: Array<String>? = null,
        private var licenseConditions: Array<String>? = null,
        private var licenseLimitations: Array<String>? = null,
        var licenseDescription: String? = null,
        licenseBody: String? = null,
        licenseKey: String? = null
) : Wedge<LicenseWedge.ViewHolder>(R.layout.item_attribouter_license), Mergeable<LicenseWedge> {

    var repo: String? by attr("repo", repo)
    private var title: String? by attr("title", title)
    private var description: String? by attr("description", description)
    var licenseName: String? by attr("licenseName", licenseName)
    private var websiteUrl: String? by attr("website", websiteUrl)
    private var gitHubUrl: String? by attr("gitHubUrl", gitHubUrl)
    var licenseUrl: String? by attr("licenseUrl", licenseUrl)
    var licenseBody: String? by attr("licenseBody", licenseBody)
    private var licenseKey: String? by attr("license", licenseKey)

    internal var token: String? = null

    override fun onCreate() {
        token = repo ?: title

        if (!websiteUrl.isNullOrEmpty())
            websiteUrl?.let { addChild(WebsiteLinkWedge(it, 2)) }

        repo?.let { addChild(GitHubLinkWedge(it, 1))}

        if (!licenseBody.isNullOrEmpty() && !licenseUrl.isNullOrEmpty())
            addChild(LicenseLinkWedge(this, 0))

        if (!hasAllGeneric()) repo?.let {
            GlobalScope.launch { // TODO: use, err, the non-global scope...
                withContext(Dispatchers.IO) {
                    lifecycle?.getProvider(it.getProviderOrNull())?.getRepository(it)
                }?.let { data -> onRepository(data) }
            }
        }

        licenseKey?.let { key ->
            GlobalScope.launch { // TODO: use, err, the non-global scope...
                withContext(Dispatchers.IO) {
                    lifecycle?.getProvider("github")?.getLicense(key)
                }?.let { onLicense(it) }
            }
        }
    }

    private fun onRepository(data: RepoData) {
        merge(LicenseWedge(
                description = data.description,
                licenseName = if (data.license != null) data.license.name else null,
                websiteUrl = data.websiteUrl
        ).create(lifecycle))

        data.license?.key?.let { key ->
            if (!hasAllLicense()) GlobalScope.launch { // TODO: use, err, the non-global scope...
                withContext(Dispatchers.IO) {
                    lifecycle?.getProvider("github")?.getLicense(key)
                }?.let { onLicense(it) }
            }
        }

        notifyItemChanged()
    }

    private fun onLicense(data: LicenseData) {
        merge(LicenseWedge(
                licenseName = data.name,
                licenseUrl = data.infoUrl,
                licensePermissions = data.permissions,
                licenseConditions = data.conditions,
                licenseLimitations = data.limitations,
                licenseDescription = data.description,
                licenseBody = data.body,
                licenseKey = data.key
        ).create(lifecycle))

        notifyItemChanged()
    }

    private fun getFormattedName(): String? {
        return title ?: repo?.let { str ->
            var name: String = str
            if (name.contains("/")) {
                val names = name.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                name = run {
                    if (names.size > 1 && names[1].isNotEmpty())
                        names[1]
                    else names[0]
                }
            }

            name = name.replace('-', ' ')
                    .replace('_', ' ')
                    .replace("([a-z])([A-Z])".toRegex(), "$1 $2")
                    .replace("([A-Z])([A-Z][a-z])".toRegex(), "$1 $2")
                    .trim { it <= ' ' }

            val nameBuffer = StringBuffer()
            val pattern = Pattern.compile("\\b(\\w)")
            val matcher = pattern.matcher(name)
            while (matcher.find())
                matcher.appendReplacement(nameBuffer, matcher.group(1).toUpperCase())

            matcher.appendTail(nameBuffer).toString()
        }
    }

    fun getLicensePermissions(): String? {
        return licensePermissions?.toListString()
    }

    fun getLicenseConditions(): String? {
        return licenseConditions?.toListString()
    }

    fun getLicenseLimitations(): String? {
        return licenseLimitations?.toListString()
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

    override fun isHidden(): Boolean {
        return false
    }

    override fun equals(obj: Any?): Boolean {
        return (obj as? LicenseWedge)?.let {
            return repo?.toLowerCase().equals(it.repo?.toLowerCase())
                    || repo?.toLowerCase().equals(it.title?.toLowerCase())
                    || title?.toLowerCase().equals(it.repo?.toLowerCase())
                    || title?.toLowerCase().equals(it.title?.toLowerCase())
        } ?: super.equals(obj)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.titleView?.apply {
            getFormattedName()?.let { text = ResourceUtils.getString(context, it) }
        }

        viewHolder.descriptionView?.apply {
            text = ResourceUtils.getString(context, description)
        }

        viewHolder.licenseView?.apply {
            licenseName?.let {
                text = ResourceUtils.getString(context, licenseName)
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        val links = getChildren(LinkWedge::class.java).filter { !it.isHidden }

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
                if (importantLink == null || link.priority() > importantLink.priority()) {
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

    class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var titleView: TextView? = v.findViewById(R.id.title)
        var descriptionView: TextView? = v.findViewById(R.id.description)
        var licenseView: TextView? = v.findViewById(R.id.license)
        var links: RecyclerView? = v.findViewById(R.id.projectLinks)
    }

}
